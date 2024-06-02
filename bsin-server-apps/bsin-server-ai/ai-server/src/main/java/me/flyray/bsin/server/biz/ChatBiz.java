package me.flyray.bsin.server.biz;

import static java.util.concurrent.TimeUnit.SECONDS;
import static java.util.stream.Collectors.joining;

import com.google.common.base.Joiner;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeoutException;

import ai.djl.util.Preconditions;
import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.chat.StreamingChatLanguageModel;
import dev.langchain4j.model.output.Response;
import dev.langchain4j.model.output.TokenUsage;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.service.TokenStream;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage;
import me.flyray.bsin.domain.entity.CopilotInfo;
import me.flyray.bsin.domain.entity.KnowledgeBase;
import me.flyray.bsin.domain.entity.LLMParam;
import me.flyray.bsin.domain.entity.QuickReplyMessage;
import me.flyray.bsin.domain.entity.RedisChatMessage;
import me.flyray.bsin.domain.entity.WxPlatform;
import me.flyray.bsin.domain.enums.CopilotType;
import me.flyray.bsin.domain.enums.VectorStoreType;
import me.flyray.bsin.exception.BusinessException;
import me.flyray.bsin.facade.response.AiChatDTO;
import me.flyray.bsin.facade.response.EmbeddingVO;
import me.flyray.bsin.infrastructure.mapper.CopilotMapper;
import me.flyray.bsin.infrastructure.mapper.KnowledgeBaseMapper;
import me.flyray.bsin.infrastructure.mapper.LLMMapper;
import me.flyray.bsin.server.memory.chat.BufferWindowInRamStoreMemory;
import me.flyray.bsin.server.memory.chat.SummaryEmbeddingVectorStoreMemory;
import me.flyray.bsin.server.memory.store.InRamStore;
import me.flyray.bsin.server.memory.store.InRedisStore;
import me.flyray.bsin.server.memory.store.InVectorDataBaseEmbeddingStore;
import me.flyray.bsin.server.push.CustomerService;
import me.flyray.bsin.server.push.PushTemplateUtil;
import me.flyray.bsin.server.websocket.WebSocketServer;

/**
 * @author leonard
 * @description
 * @createDate 2023/12/2023/12/28 /22/35
 */
@Component
@Slf4j
public class ChatBiz {

  @Value("${milvus.datasource.merchantCollectionName}")
  private String merchantCollectionName;

  @Value("${milvus.datasource.customerCollectionName}")
  private String customerCollection;

  @Value("${milvus.datasource.partitionName}")
  private String partitionName;

  @Autowired private VectorRetrievalBiz vectorRetrievalBiz;
  @Autowired private LLMMapper llmMapper;
  @Autowired private CopilotMapper copilotMapper;
  @Autowired private KnowledgeBaseMapper knowledgeBaseMapper;

  @Autowired private InVectorDataBaseEmbeddingStore inVectorDataBaseEmbeddingStore;
  @Autowired private InRedisStore inRedisStore; // 用于缓存历史聊天记录
  private InRamStore inRamStore = new InRamStore(); // 用于缓存最近K条上下文窗口聊天记录
  private InRamStore inRamCacheStore = new InRamStore(); // 用于缓存总结的历史聊天记录
  @Autowired private PromptEngineeringBiz promptEngineeringBiz;
  @Autowired private ModelBiz modelBiz;

  // 历史聊天记录总结和存储异步线程执行服务
  ExecutorService chatSummaryAndStoreExecutorService = Executors.newFixedThreadPool(10);

  interface Assistant {
    TokenStream chat(List<ChatMessage> chatMessages);
  }

  public AiChatDTO chat(AiChatDTO aiChatDTOReq, WebSocketServer webSocketServer)
      throws IOException, ExecutionException, InterruptedException, TimeoutException {
    Preconditions.checkArgument(aiChatDTOReq != null, "aiChatDTO不能为空");
    Preconditions.checkArgument(aiChatDTOReq.getQuestion() != null, "qustion不能为空");
    AiChatDTO aiChatDTORes = null;
    // 1： 品牌官
    if (aiChatDTOReq.getType().equals(CopilotType.BRAND_OFFICER.getCode())) {
      aiChatDTORes = chatWithCopilot(aiChatDTOReq, webSocketServer);
    }
    // 2： 个人数字分身
    else if (aiChatDTOReq.getType().equals(CopilotType.DIGITAL_AVATAR.getCode())) {
      aiChatDTORes = chatWithCopilot(aiChatDTOReq, webSocketServer);
    }
    // 3： 知识库问答
    else if (aiChatDTOReq.getType().equals(CopilotType.KNOWLEDGE_BASE_BOT.getCode())) {
      aiChatDTORes = chatWithKnowledgeBase(aiChatDTOReq, webSocketServer);
    } else {

    }
    return aiChatDTORes;
  }

  public AiChatDTO chat(
      AiChatDTO aiChatDTOReq,
      WxPlatform wxPlatform,
      WxMpXmlMessage wxMessage,
      WxMpService wxMpService)
      throws IOException, ExecutionException, InterruptedException, TimeoutException {
    Preconditions.checkArgument(aiChatDTOReq != null, "aiChatDTO不能为空");
    Preconditions.checkArgument(aiChatDTOReq.getQuestion() != null, "qustion不能为空");
    return chatWithCopilot(aiChatDTOReq, wxPlatform, wxMessage, wxMpService);
  }

  public AiChatDTO chatWithCopilot(AiChatDTO aiChatDTOReq, WebSocketServer webSocketServer)
      throws ExecutionException, InterruptedException, TimeoutException, IOException {
    if (aiChatDTOReq.getToNo() == null) {
      throw new BusinessException("100000", "copilot ID为空!!!");
    }
    CopilotInfo copilotInfo = copilotMapper.selectById(aiChatDTOReq.getToNo());
    if (copilotInfo == null) {
      throw new BusinessException("100000", "未查询到copilot：" + aiChatDTOReq.getToNo());
    }
    String knowledgeBaseNo = copilotInfo.getKnowledgeBaseNo();

    if (StringUtils.isEmpty(aiChatDTOReq.getTenantId())) {
      aiChatDTOReq.setTenantId(copilotInfo.getTenantId());
    }
    if (StringUtils.isEmpty(aiChatDTOReq.getMerchantNo())) {
      aiChatDTOReq.setMerchantNo(copilotInfo.getMerchantNo());
    }

    // 1.聊天记录存储在redis
    inRedisStore.rightPushMessage(
        "chat:",
        RedisChatMessage.newBuilder()
            .withSender(aiChatDTOReq.getFromNo())
            .withReceiver(aiChatDTOReq.getToNo())
            .withMessage(aiChatDTOReq.getQuestion())
            .build());

    // 2.知识库search
    List<EmbeddingVO> knowledgeBaseEmbeddingVOs =
        vectorRetrievalBiz.retrievalBaseOnKnowledgeBase(
            aiChatDTOReq.getQuestion(),
            aiChatDTOReq.getMerchantNo(),
            knowledgeBaseNo,
            merchantCollectionName,
            false);

    //  3.查找copilot对应的LLM
    String llmNo = copilotInfo.getLlmNo();

    LLMParam llmParam = llmMapper.selectById(llmNo);
    if (llmParam == null) {
      throw new BusinessException("100000", "未找到LLM！！！");
    }
    inVectorDataBaseEmbeddingStore.setInRamStore(inRamStore);
    inVectorDataBaseEmbeddingStore.setInRamCacheStore(inRamCacheStore);
    SummaryEmbeddingVectorStoreMemory chatMemory =
        SummaryEmbeddingVectorStoreMemory.builder()
            .id(aiChatDTOReq.getFromNo())
            .triggerSummaryMessages(llmParam.getMaxMessages())
            .maxMessages(llmParam.getMaxMessages())
            .triggerSummaryMessages(llmParam.getMaxSummaryMessages())
            .triggerSummaryTokens(llmParam.getMaxMessages())
            .chatMemoryStore(inVectorDataBaseEmbeddingStore)
            .build();

    // 4.提示词上下文补充
    // copilot绑定的知识库
    String knowledgeContent =
        knowledgeBaseEmbeddingVOs.stream().map(match -> match.getText()).collect(joining("\n\n"));

    // 5.最近k条历史聊天记录
    List<ChatMessage> chatBufferWindowList = chatMemory.messages();

    // 6.历史聊天记录向量搜索匹配
    List<EmbeddingVO> chatHistorySummaryEmbeddingVOs =
        vectorRetrievalBiz.retrievalBaseOnChatMemory(
            aiChatDTOReq.getQuestion(),
            aiChatDTOReq.getFromNo(),
            aiChatDTOReq.getToNo(),
            customerCollection,
            null,
            false);
    StringBuilder result = new StringBuilder();
    for (EmbeddingVO embeddingVO : chatHistorySummaryEmbeddingVOs) {
      if (StringUtils.isNotEmpty(embeddingVO.getText())) {
        result.append(embeddingVO.getText()).append("\n");
      }
    }
    String chatHistorySummary = result.toString();

    // 7.查找copilot对应的提示词模板
    String promptTemplateNo = copilotInfo.getPromptTemplateNo();
    List<ChatMessage> promptChatMessage =
        promptEngineeringBiz.generatePromptMessages(
            promptTemplateNo,
            knowledgeContent,
            chatBufferWindowList,
            chatHistorySummary,
            aiChatDTOReq.getQuestion(),
            chatMemory,
            VectorStoreType.CHAT_HISTORY_SUMMARY);

    // 8. chat with LLM
    ChatLanguageModel chatModel = null;
    StreamingChatLanguageModel streamingModel = null;
    TokenUsage tokenUsage = null;
    // 流式返回
    if (llmParam.isStreaming()) {
      streamingModel = modelBiz.getStreamingChatModel(llmParam);
      Assistant assistant = AiServices.create(Assistant.class, streamingModel);

      StringBuilder answerBuilder = new StringBuilder();
      CompletableFuture<String> futureAnswer = new CompletableFuture<>();
      CompletableFuture<Response<AiMessage>> futureResponse = new CompletableFuture<>();
      System.out.println("promptChatMessage:\n" + promptChatMessage.toString());
      try {
        assistant
            .chat(promptChatMessage)
            .onNext(
                token -> {
                  try {
                    System.out.println(token);
                    webSocketServer.sendMessageToUser(token, webSocketServer.getFromNo());
                  } catch (IOException e) {
                    throw new RuntimeException(e);
                  }
                })
            .onComplete(
                response -> {
                  try {
                    webSocketServer.sendMessageToUser("done", webSocketServer.getFromNo());
                  } catch (IOException e) {
                    throw new RuntimeException(e);
                  }
                  futureAnswer.complete(answerBuilder.toString());
                  futureResponse.complete(response);
                })
            .onError(futureAnswer::completeExceptionally)
            .start();
      } catch (Exception e) {
        webSocketServer.sendMessageToUser(e.toString(), webSocketServer.getFromNo());
        webSocketServer.sendMessageToUser("done", webSocketServer.getFromNo());
      }
      String answer = futureAnswer.get(30, SECONDS);
      Response<AiMessage> response = futureResponse.get(30, SECONDS);
    } else {
      chatModel = modelBiz.getChatModel(llmParam);
      System.out.println("promptChatMessage：\n" + promptChatMessage.toString());
      try {
        // 10.Send the prompt to the OpenAI chat model
        Response<AiMessage> response = chatModel.generate(promptChatMessage);
        tokenUsage = response.tokenUsage();
        AiMessage aiMessage = response.content();

        // 11.See an answer from the model
        String answer = aiMessage.text();
        chatMemory.add(aiMessage);

        inRedisStore.rightPushMessage(
            "chat:",
            RedisChatMessage.newBuilder()
                .withSender(aiChatDTOReq.getToNo())
                .withReceiver(aiChatDTOReq.getFromNo())
                .withMessage(answer)
                .build());
        aiChatDTOReq.setAnswer(answer);
        webSocketServer.sendMessageToUser(answer, webSocketServer.getFromNo());
      } catch (Exception e) {
        webSocketServer.sendMessageToUser(e.toString(), webSocketServer.getFromNo());
      }
    }
    // 9.异步执行需要总结对话并存储在向量数据库
    if (((SummaryEmbeddingVectorStoreMemory) chatMemory).triggerSummaryMessages() != null) {
      // generate summary prompt
      String chatHistory = ((SummaryEmbeddingVectorStoreMemory) chatMemory).toString();
      UserMessage userMessage =
          promptEngineeringBiz.generateUserMessage(
              "chatBufferWindowList", copilotInfo.getSummaryPromptTemplate(), chatHistory);
      chatSummaryAndStoreExecutorService.submit(
          new ChatHistorySummaryAndStoreBiz(
              chatModel,
              List.of(userMessage),
              knowledgeBaseEmbeddingVOs.get(0).getEmbeddingModel(),
              chatMemory,
              vectorRetrievalBiz,
              aiChatDTOReq.getFromNo(),
              aiChatDTOReq.getToNo(),
              customerCollection,
              partitionName));
    }
    log.debug("tokenUsage:", tokenUsage.toString());
    log.debug("quotes:", knowledgeBaseEmbeddingVOs);
    log.debug("prompt:", promptEngineeringBiz.toPromptMessages(promptChatMessage).toString());
    log.debug("knowledgeContent:", knowledgeContent);
    log.debug("chatHistorySummary:", chatHistorySummary);
    return aiChatDTOReq;
  }

  public AiChatDTO chatWithCopilot(
      AiChatDTO aiChatDTOReq,
      WxPlatform wxPlatform,
      WxMpXmlMessage wxMessage,
      WxMpService wxMpService)
      throws ExecutionException, InterruptedException, TimeoutException, IOException {
    String token = null;
    try {
      token = wxMpService.getAccessToken();
      System.out.println("token: " + token);
      if (aiChatDTOReq.getToNo() == null) {
        throw new BusinessException("100000", "copilot ID为空!!!");
      }
      CopilotInfo copilotInfo = copilotMapper.selectById(aiChatDTOReq.getToNo());
      if (copilotInfo == null) {
        throw new BusinessException("100000", "未查询到copilot：" + aiChatDTOReq.getToNo());
      }
      String knowledgeBaseNo = copilotInfo.getKnowledgeBaseNo();

      if (StringUtils.isEmpty(aiChatDTOReq.getTenantId())) {
        aiChatDTOReq.setTenantId(copilotInfo.getTenantId());
      }
      if (StringUtils.isEmpty(aiChatDTOReq.getMerchantNo())) {
        aiChatDTOReq.setMerchantNo(copilotInfo.getMerchantNo());
      }

      // 1.聊天记录存储在redis
      inRedisStore.rightPushMessage(
          "chat:",
          RedisChatMessage.newBuilder()
              .withSender(aiChatDTOReq.getFromNo())
              .withReceiver(aiChatDTOReq.getToNo())
              .withMessage(aiChatDTOReq.getQuestion())
              .build());

      // 2.知识库search
      List<EmbeddingVO> knowledgeBaseEmbeddingVOs =
          vectorRetrievalBiz.retrievalBaseOnKnowledgeBase(
              aiChatDTOReq.getQuestion(),
              aiChatDTOReq.getMerchantNo(),
              knowledgeBaseNo,
              merchantCollectionName,
              false);

      //  3.查找copilot对应的LLM
      String llmNo = copilotInfo.getLlmNo();

      LLMParam llmParam = llmMapper.selectById(llmNo);
      if (llmParam == null) {
        throw new BusinessException("100000", "未找到LLM！！！");
      }
      inVectorDataBaseEmbeddingStore.setInRamStore(inRamStore);
      inVectorDataBaseEmbeddingStore.setInRamCacheStore(inRamCacheStore);
      SummaryEmbeddingVectorStoreMemory chatMemory =
          SummaryEmbeddingVectorStoreMemory.builder()
              .id(aiChatDTOReq.getFromNo())
              .triggerSummaryMessages(llmParam.getMaxMessages())
              .maxMessages(llmParam.getMaxMessages())
              .triggerSummaryMessages(llmParam.getMaxSummaryMessages())
              .triggerSummaryTokens(llmParam.getMaxMessages())
              .chatMemoryStore(inVectorDataBaseEmbeddingStore)
              .build();

      // 4.提示词上下文补充
      // copilot绑定的知识库
      String knowledgeContent =
          knowledgeBaseEmbeddingVOs.stream().map(match -> match.getText()).collect(joining("\n\n"));

      // 5.最近k条历史聊天记录
      List<ChatMessage> chatBufferWindowList = chatMemory.messages();

      // 6.历史聊天记录向量搜索匹配
      List<EmbeddingVO> chatHistorySummaryEmbeddingVOs =
          vectorRetrievalBiz.retrievalBaseOnChatMemory(
              aiChatDTOReq.getQuestion(),
              aiChatDTOReq.getFromNo(),
              aiChatDTOReq.getToNo(),
              customerCollection,
              null,
              false);
      StringBuilder result = new StringBuilder();
      for (EmbeddingVO embeddingVO : chatHistorySummaryEmbeddingVOs) {
        if (StringUtils.isNotEmpty(embeddingVO.getText())) {
          result.append(embeddingVO.getText()).append("\n");
        }
      }
      String chatHistorySummary = result.toString();

      // 7.查找copilot对应的提示词模板
      String promptTemplateNo = copilotInfo.getPromptTemplateNo();
      List<ChatMessage> promptChatMessage =
          promptEngineeringBiz.generatePromptMessages(
              promptTemplateNo,
              knowledgeContent,
              chatBufferWindowList,
              chatHistorySummary,
              aiChatDTOReq.getQuestion(),
              chatMemory,
              VectorStoreType.CHAT_HISTORY_SUMMARY);

      // 8. chat with LLM
      ChatLanguageModel chatModel = null;
      StreamingChatLanguageModel streamingModel = null;
      TokenUsage tokenUsage = null;
      chatModel = modelBiz.getChatModel(llmParam);
      System.out.println("promptChatMessage：\n" + promptChatMessage.toString());
      // 10.Send the prompt to the OpenAI chat model
      Response<AiMessage> response = chatModel.generate(promptChatMessage);
      tokenUsage = response.tokenUsage();
      AiMessage aiMessage = response.content();

      // 11.See an answer from the model
      String answer = aiMessage.text();
      chatMemory.add(aiMessage);

      inRedisStore.rightPushMessage(
          "chat:",
          RedisChatMessage.newBuilder()
              .withSender(aiChatDTOReq.getToNo())
              .withReceiver(aiChatDTOReq.getFromNo())
              .withMessage(answer)
              .build());
      aiChatDTOReq.setAnswer(answer);
      CustomerService.connectWeiXinInterface(token, wxMessage.getFromUser(), answer);
      // 启用模版推送
      if (wxPlatform.isTemplateEnable()) {
        PushTemplateUtil.sendMessage(
            wxMessage.getFromUser(), wxMessage.getContent(), answer, wxMpService);
      }

      // 9.异步执行需要总结对话并存储在向量数据库
      if (((SummaryEmbeddingVectorStoreMemory) chatMemory).triggerSummaryMessages() != null) {
        // generate summary prompt
        String chatHistory = ((SummaryEmbeddingVectorStoreMemory) chatMemory).toString();
        UserMessage userMessage =
            promptEngineeringBiz.generateUserMessage(
                "chatBufferWindowList", copilotInfo.getSummaryPromptTemplate(), chatHistory);
        chatSummaryAndStoreExecutorService.submit(
            new ChatHistorySummaryAndStoreBiz(
                chatModel,
                List.of(userMessage),
                knowledgeBaseEmbeddingVOs.get(0).getEmbeddingModel(),
                chatMemory,
                vectorRetrievalBiz,
                aiChatDTOReq.getFromNo(),
                aiChatDTOReq.getToNo(),
                customerCollection,
                partitionName));
      }
      log.debug("tokenUsage:", tokenUsage.toString());
      log.debug("quotes:", knowledgeBaseEmbeddingVOs);
      log.debug("prompt:", promptEngineeringBiz.toPromptMessages(promptChatMessage).toString());
      log.debug("knowledgeContent:", knowledgeContent);
      log.debug("chatHistorySummary:", chatHistorySummary);
    } catch (Exception e) {
      e.printStackTrace();
      if (!StringUtils.isEmpty(token)) {
        CustomerService.connectWeiXinInterface(token, wxMessage.getFromUser(), e.toString());
      }
    }
    return aiChatDTOReq;
  }

  public AiChatDTO chatWithKnowledgeBase(AiChatDTO aiChatDTOReq, WebSocketServer webSocketServer) {
    if (aiChatDTOReq.getToNo() == null) {
      throw new BusinessException("100000", "knowledgeBase ID为空!!!");
    }
    KnowledgeBase knowledgeBase = knowledgeBaseMapper.selectById(aiChatDTOReq.getToNo());
    if (knowledgeBase == null) {
      throw new BusinessException("100000", "未查询到knowledgeBase：" + aiChatDTOReq.getToNo());
    }

    if (StringUtils.isEmpty(aiChatDTOReq.getTenantId())) {
      aiChatDTOReq.setTenantId(knowledgeBase.getTenantId());
    }
    if (StringUtils.isEmpty(aiChatDTOReq.getMerchantNo())) {
      aiChatDTOReq.setMerchantNo(knowledgeBase.getMerchantNo());
    }

    // 1.聊天记录存储在redis
    inRedisStore.rightPushMessage(
        "chat:",
        RedisChatMessage.newBuilder()
            .withSender(aiChatDTOReq.getFromNo())
            .withReceiver(aiChatDTOReq.getToNo())
            .withMessage(aiChatDTOReq.getQuestion())
            .build());

    // 2.知识库search，和是知识库聊天，aiNo为知识库ID
    List<EmbeddingVO> knowledgeBaseEmbeddingVOs =
        vectorRetrievalBiz.retrievalBaseOnKnowledgeBase(
            aiChatDTOReq.getQuestion(),
            aiChatDTOReq.getMerchantNo(),
            aiChatDTOReq.getToNo(),
            merchantCollectionName,
            false);

    // 3.查找知识库对应的LLM
    String llmNo = knowledgeBaseEmbeddingVOs.get(0).getLlmNo();
    LLMParam llmParam = llmMapper.selectById(llmNo);
    if (llmParam == null) {
      throw new BusinessException("100000", "未找到LLM！！！");
    }

    // 4.聊天上下文
    BufferWindowInRamStoreMemory chatMemory =
        BufferWindowInRamStoreMemory.builder()
            .id(aiChatDTOReq.getFromNo())
            .maxMessages(llmParam.getMaxMessages())
            .chatMemoryStore(inRamStore)
            .build();

    // 5.提示词模板 knowledgeBase
    String knowledgeContent =
        knowledgeBaseEmbeddingVOs.stream().map(match -> match.getText()).collect(joining("\n\n"));

    // 6.最近k条历史聊天记录
    List<ChatMessage> chatBufferWindowList = chatMemory.messages();
    String promptTemplateNo = knowledgeBaseEmbeddingVOs.get(0).getPromptTemplateNo();
    List<ChatMessage> promptChatMessage =
        promptEngineeringBiz.generatePromptMessages(
            promptTemplateNo,
            knowledgeContent,
            chatBufferWindowList,
            null,
            aiChatDTOReq.getQuestion(),
            chatMemory,
            VectorStoreType.KNOWLEDGE_BASE);

    // 7. chat with LLM
    ChatLanguageModel chatModel = modelBiz.getChatModel(llmParam);
    // 8.Send the prompt to the OpenAI chat model
    Response<AiMessage> response = chatModel.generate(promptChatMessage);
    TokenUsage tokenUsage = response.tokenUsage();
    AiMessage aiMessage = response.content();

    // 9.See an answer from the model
    String answer = aiMessage.text();
    chatMemory.add(aiMessage);

    inRedisStore.rightPushMessage(
        "chat:",
        RedisChatMessage.newBuilder()
            .withSender(aiChatDTOReq.getToNo())
            .withReceiver(aiChatDTOReq.getFromNo())
            .withMessage(answer)
            .build());

    aiChatDTOReq.setAnswer(answer);
    log.debug("tokenUsage:", tokenUsage.toString());
    log.debug("quotes:", knowledgeBaseEmbeddingVOs);
    log.debug("prompt:", promptEngineeringBiz.toPromptMessages(promptChatMessage).toString());
    log.debug("knowledgeBase:", knowledgeBase);
    return aiChatDTOReq;
  }

  public List<QuickReplyMessage> generateQuickReplies(List<String> questions, int topK) {
    String mergeString = Joiner.on(",").skipNulls().join(questions);
    System.out.println("mergeString: " + mergeString);
    List<String> keywords = extractKeywords(mergeString, topK);
    System.out.println("Keywords: " + keywords);
    List<QuickReplyMessage> quickReplyMessages = new ArrayList<>();
    for (String word : keywords) {
      quickReplyMessages.add(
          QuickReplyMessage.newBuilder().withName(word).withCode("text").build());
    }
    return quickReplyMessages;
  }

  private Map<String, Double> calculateTFIDF(List<String> sentences) {
    Map<String, Double> tfidfMap = new HashMap<>();
    int totalSentences = sentences.size();
    Map<String, Integer> tfMap = new HashMap<>();

    // 计算TF值
    for (String sentence : sentences) {
      StringTokenizer tokenizer = new StringTokenizer(sentence);
      while (tokenizer.hasMoreTokens()) {
        String word = tokenizer.nextToken();
        if (tfMap.containsKey(word)) {
          tfMap.put(word, tfMap.get(word) + 1);
        } else {
          tfMap.put(word, 1);
        }
      }
    }

    // 计算IDF值
    Map<String, Integer> dfMap = new HashMap<>();
    for (String word : tfMap.keySet()) {
      for (String sentence : sentences) {
        if (sentence.contains(word)) {
          if (dfMap.containsKey(word)) {
            dfMap.put(word, dfMap.get(word) + 1);
          } else {
            dfMap.put(word, 1);
          }
        }
      }
      double idf = Math.log((double) totalSentences / (dfMap.get(word) + 1));
      double tfidf = tfMap.get(word) * idf;
      tfidfMap.put(word, tfidf);
    }

    return tfidfMap;
  }

  private List<String> extractKeywords(String text, int topK) {
    List<String> sentences = new ArrayList<>();
    StringTokenizer tokenizer1 = new StringTokenizer(text, ".");
    StringTokenizer tokenizer2 = new StringTokenizer(text, "。");
    StringTokenizer tokenizer3 = new StringTokenizer(text, ",");
    StringTokenizer tokenizer4 = new StringTokenizer(text, "，");
    while (tokenizer1.hasMoreTokens()) {
      String sentence = tokenizer1.nextToken().trim();
      sentences.add(sentence);
    }
    while (tokenizer2.hasMoreTokens()) {
      String sentence = tokenizer2.nextToken().trim();
      sentences.add(sentence);
    }
    while (tokenizer3.hasMoreTokens()) {
      String sentence = tokenizer3.nextToken().trim();
      sentences.add(sentence);
    }
    while (tokenizer4.hasMoreTokens()) {
      String sentence = tokenizer4.nextToken().trim();
      sentences.add(sentence);
    }
    Map<String, Double> tfidfMap = calculateTFIDF(sentences);
    List<Map.Entry<String, Double>> sortedList = new ArrayList<>(tfidfMap.entrySet());
    sortedList.sort(Map.Entry.comparingByValue());

    List<String> keywords = new ArrayList<>();
    if (sortedList.size() - topK > 0) {
      for (int i = sortedList.size() - 1; i >= sortedList.size() - topK; i--) {
        Map.Entry<String, Double> entry = sortedList.get(i);
        keywords.add(entry.getKey());
      }
    }
    return keywords;
  }
}
