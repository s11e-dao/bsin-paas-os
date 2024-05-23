package me.flyray.bsin.server.impl;

import static java.util.stream.Collectors.joining;

import org.apache.commons.collections4.MapUtils;
import org.apache.shenyu.client.apache.dubbo.annotation.ShenyuDubboService;
import org.apache.shenyu.client.apidocs.annotations.ApiDoc;
import org.apache.shenyu.client.apidocs.annotations.ApiModule;
import org.apache.shenyu.client.dubbo.common.annotation.ShenyuDubboClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.output.Response;
import dev.langchain4j.model.output.TokenUsage;
import lombok.extern.slf4j.Slf4j;
import me.flyray.bsin.constants.ResponseCode;
import me.flyray.bsin.domain.domain.LLMParam;
import me.flyray.bsin.domain.domain.QuickReplyMessage;
import me.flyray.bsin.domain.domain.RedisChatMessage;
import me.flyray.bsin.domain.enums.VectorStoreType;
import me.flyray.bsin.exception.BusinessException;
import me.flyray.bsin.facade.response.EmbeddingVO;
import me.flyray.bsin.facade.service.ChatService;
import me.flyray.bsin.infrastructure.mapper.KnowledgeBaseMapper;
import me.flyray.bsin.infrastructure.mapper.LLMMapper;
import me.flyray.bsin.infrastructure.mapper.PromptTemplateMapper;
import me.flyray.bsin.security.contex.LoginInfoContextHelper;
import me.flyray.bsin.security.domain.LoginUser;
import me.flyray.bsin.server.biz.ChatBiz;
import me.flyray.bsin.server.biz.ModelBiz;
import me.flyray.bsin.server.biz.PromptEngineeringBiz;
import me.flyray.bsin.server.biz.VectorRetrievalBiz;
import me.flyray.bsin.server.memory.chat.BufferWindowInRamStoreMemory;
import me.flyray.bsin.server.memory.store.InRamStore;
import me.flyray.bsin.server.memory.store.InRedisStore;
import me.flyray.bsin.server.utils.RespBodyHandler;

@ShenyuDubboService(path = "/chat", timeout = 6000)
@ApiModule(value = "chat")
@Service
@Slf4j
public class ChatServiceImpl implements ChatService {

  @Value("${milvus.datasource.merchantCollectionName}")
  private String merchantCollectionName;

  @Value("${langchain4j.chat-model.openai.api-key}")
  private String OPENAI_API_KEY;

  @Autowired private EmbeddingModel embeddingModel;
  @Autowired private VectorRetrievalBiz vectorRetrievalBiz;
  @Autowired private PromptTemplateMapper promptTemplateMapper;
  @Autowired private KnowledgeBaseMapper knowledgeBaseMapper;
  @Autowired private LLMMapper llmMapper;

  @Autowired private InRamStore inRamStore;
  @Autowired private PromptEngineeringBiz promptEngineeringBiz;
  @Autowired private InRedisStore inRedisStore; // 用于缓存历史聊天记录
  @Autowired private ModelBiz modelBiz;
  @Autowired private ChatBiz chatBiz;

  interface Assistant {
    // String chat(@MemoryId int memoryId, @UserMessage String userMessage);
    //    @SystemMessage("你是一个专业的web3.0品牌运营官")
    //    AiMessage chat(@MemoryId String memoryId, @UserMessage String userMessage);
  }

  @ApiDoc(desc = "getChatHistoryList")
  @ShenyuDubboClient("/getChatHistoryList")
  @Override
  public Map<String, Object> getChatHistoryList(Map<String, Object> requestMap) {
    LoginUser loginUser = LoginInfoContextHelper.getLoginUser();
    String merchantNo = MapUtils.getString(requestMap, "merchantNo");
    if (merchantNo == null) {
      merchantNo = loginUser.getMerchantNo();
      //      if (merchantNo == null) {
      //        throw new BusinessException(ResponseCode.MERCHANT_NO_IS_NULL);
      //      }
    }
    String customerNo = MapUtils.getString(requestMap, "customerNo");
    String receiver = MapUtils.getString(requestMap, "receiver");
    String chatType = MapUtils.getString(requestMap, "chatType"); // chat|retrieval
    if (chatType == null) {
      chatType = "chat";
    }
    chatType += ":";

    if (receiver == null) {
      throw new BusinessException(ResponseCode.CUSTOMER_NO_NOT_ISNULL);
    }
    if (customerNo == null) {
      customerNo = MapUtils.getString(requestMap, "sender");
      if (customerNo == null) {
        customerNo = loginUser.getCustomerNo();
      }
      if (customerNo == null) {
        throw new BusinessException(ResponseCode.CUSTOMER_NO_NOT_ISNULL);
      }
    }
    String tenantId = loginUser.getTenantId();
    List<RedisChatMessage> redisChatMessages =
        inRedisStore.getMessages(chatType, customerNo, receiver);
    return RespBodyHandler.setRespBodyListDto(redisChatMessages);
  }

  @ApiDoc(desc = "chatWithKnowledgeBase")
  @ShenyuDubboClient("/chatWithKnowledgeBase")
  @Override
  public Map<String, Object> chatWithKnowledgeBase(Map<String, Object> requestMap) {
    LoginUser loginUser = LoginInfoContextHelper.getLoginUser();
    String merchantNo = MapUtils.getString(requestMap, "merchantNo");
    if (merchantNo == null) {
      merchantNo = loginUser.getMerchantNo();
      if (merchantNo == null) {
        throw new BusinessException(ResponseCode.MERCHANT_NO_IS_NULL);
      }
    }
    String customerNo = MapUtils.getString(requestMap, "customerNo");
    if (customerNo == null) {
      customerNo = loginUser.getCustomerNo();
      if (customerNo == null) {
        throw new BusinessException(ResponseCode.CUSTOMER_NO_NOT_ISNULL);
      }
    }

    String tenantId = loginUser.getTenantId();
    String knowledgeBaseNo = MapUtils.getString(requestMap, "knowledgeBaseNo");
    String question = MapUtils.getString(requestMap, "question");
    String promptTemplateNo = MapUtils.getString(requestMap, "promptTemplateNo");
    String llmNo = MapUtils.getString(requestMap, "llmNo");
    String quickRepliesStr = MapUtils.getString(requestMap, "quickReplies");
    boolean quickReplies = false;
    if (quickRepliesStr != null) {
      quickReplies = Boolean.parseBoolean(quickRepliesStr);
    }

    // TODO: knowledgeBaseNo = null 和商户所有知识库聊天
    inRedisStore.rightPushMessage(
        "chat:",
        RedisChatMessage.newBuilder()
            .withSender(customerNo)
            .withReceiver(knowledgeBaseNo)
            .withMessage(question)
            .build());
    try {
      // 1.知识库search
      List<EmbeddingVO> knowledgeBaseEmbeddingVOs =
          vectorRetrievalBiz.retrievalBaseOnKnowledgeBase(
              question, merchantNo, knowledgeBaseNo, merchantCollectionName, false);

      // 3.1  查找知识库对应的LLM
      if (llmNo == null) {
        llmNo = knowledgeBaseEmbeddingVOs.get(0).getLlmNo();
      }
      LLMParam llmParam = llmMapper.selectById(llmNo);
      if (llmParam == null) {
        throw new BusinessException("100000", "未找到LLM！！！");
      }

      BufferWindowInRamStoreMemory chatMemory =
          BufferWindowInRamStoreMemory.builder()
              .id(customerNo)
              .maxMessages(llmParam.getMaxMessages())
              .chatMemoryStore(inRamStore)
              .build();

      // 2.2 提示词模板 knowledgeBase
      String knowledgeBase =
          knowledgeBaseEmbeddingVOs.stream().map(match -> match.getText()).collect(joining("\n\n"));

      // 2.2 最近k条历史聊天记录
      List<ChatMessage> chatBufferWindowList = chatMemory.messages();

      if (promptTemplateNo == null) {
        promptTemplateNo = knowledgeBaseEmbeddingVOs.get(0).getPromptTemplateNo();
      }
      List<ChatMessage> promptChatMessage =
          promptEngineeringBiz.generatePromptMessages(
              promptTemplateNo,
              knowledgeBase,
              chatBufferWindowList,
              null,
              question,
              chatMemory,
              VectorStoreType.KNOWLEDGE_BASE);

      // 3. chat with LLM
      // 默认模型
      ChatLanguageModel chatModel = modelBiz.getChatModel(llmParam);

      // 3.1.Send the prompt to the OpenAI chat model
      Response<AiMessage> response = chatModel.generate(promptChatMessage);
      TokenUsage tokenUsage = response.tokenUsage();
      AiMessage aiMessage = response.content();

      // 3.1.See an answer from the model
      String answer = aiMessage.text();
      chatMemory.add(aiMessage);

      inRedisStore.rightPushMessage(
          "chat:",
          RedisChatMessage.newBuilder()
              .withSender(knowledgeBaseNo)
              .withReceiver(customerNo)
              .withMessage(answer)
              .build());

      if (quickReplies) {
        List<QuickReplyMessage> quickReplyMessages =
            chatBiz.generateQuickReplies(new ArrayList<String>(Arrays.asList(question, answer)), 3);
        requestMap.put("quickReplyMessages", quickReplyMessages);
      }

      // TODO: return chatVO
      requestMap.put("answer", answer);
      requestMap.put("tokenUsage", tokenUsage);
      requestMap.put("quotes", knowledgeBaseEmbeddingVOs);
      requestMap.put("prompt", promptEngineeringBiz.toPromptMessages(promptChatMessage));
      requestMap.put("knowledgeBase", knowledgeBase);
      requestMap.put("chatHistorySummary", null);
      requestMap.put(
          "chatBufferWindow", promptEngineeringBiz.toPromptMessages(chatBufferWindowList));
      return RespBodyHandler.setRespBodyDto(requestMap);
    } catch (Exception e) {
      inRedisStore.rightPushMessage(
          "chat:",
          RedisChatMessage.newBuilder()
              .withSender(knowledgeBaseNo)
              .withReceiver(customerNo)
              .withMessage(e.toString())
              .build());
      throw new BusinessException("100000", e.toString());
    }
  }

  @ApiDoc(desc = "chatWithCopilot")
  @ShenyuDubboClient("/chatWithCopilot")
  @Override
  // 在copilotService中实现
  public Map<String, Object> chatWithCopilot(Map<String, Object> requestMap) {
    LoginUser loginUser = LoginInfoContextHelper.getLoginUser();
    String merchantNo = MapUtils.getString(requestMap, "merchantNo");
    if (merchantNo == null) {
      merchantNo = loginUser.getMerchantNo();
      if (merchantNo == null) {
        throw new BusinessException(ResponseCode.MERCHANT_NO_IS_NULL);
      }
    }
    String tenantId = loginUser.getTenantId();
    String knowledgeBaseNo = MapUtils.getString(requestMap, "knowledgeBaseNo");
    String question = MapUtils.getString(requestMap, "question");
    String promptTemplateNo = MapUtils.getString(requestMap, "promptTemplateNo");

    return RespBodyHandler.setRespBodyDto(requestMap);
  }

  @ApiDoc(desc = "chatWithChiefBrandOfficer")
  @ShenyuDubboClient("/chatWithChiefBrandOfficer")
  @Override
  public Map<String, Object> chatWithChiefBrandOfficer(Map<String, Object> requestMap) {
    LoginUser loginUser = LoginInfoContextHelper.getLoginUser();
    String merchantNo = MapUtils.getString(requestMap, "merchantNo");
    String tenantId = loginUser.getTenantId();
    // TODO 根据商户号查询商户对应的默认Copilot

    String knowledgeBaseNo = MapUtils.getString(requestMap, "knowledgeBaseNo");
    String question = MapUtils.getString(requestMap, "question");
    String promptTemplateNo = MapUtils.getString(requestMap, "promptTemplateNo");

    return RespBodyHandler.setRespBodyDto(requestMap);
  }

  @ApiDoc(desc = "chatWithDigitalAvatar")
  @ShenyuDubboClient("/chatWithDigitalAvatar")
  @Override
  public Map<String, Object> chatWithDigitalAvatar(Map<String, Object> requestMap) {
    LoginUser loginUser = LoginInfoContextHelper.getLoginUser();
    String tenantId = loginUser.getTenantId();
    String customerNo = MapUtils.getString(requestMap, "customerNo");
    // TODO 根据customerNo查询customerNo对应的默认Copilot
    if (customerNo == null) {
      customerNo = loginUser.getCustomerNo();
      if (customerNo == null) {
        throw new BusinessException(ResponseCode.CUSTOMER_NO_NOT_ISNULL);
      }
    }
    String knowledgeBaseNo = MapUtils.getString(requestMap, "knowledgeBaseNo");
    String question = MapUtils.getString(requestMap, "question");
    String promptTemplateNo = MapUtils.getString(requestMap, "promptTemplateNo");

    return RespBodyHandler.setRespBodyDto(requestMap);
  }

  @ApiDoc(desc = "getQuickReplies")
  @ShenyuDubboClient("/getQuickReplies")
  @Override
  public Map<String, Object> getQuickReplies(Map<String, Object> requestMap) {
    LoginUser loginUser = LoginInfoContextHelper.getLoginUser();
    String merchantNo = MapUtils.getString(requestMap, "merchantNo");
    if (merchantNo == null) {
      merchantNo = loginUser.getMerchantNo();
      if (merchantNo == null) {
        throw new BusinessException(ResponseCode.MERCHANT_NO_IS_NULL);
      }
    }
    String customerNo = MapUtils.getString(requestMap, "customerNo");
    String receiver = MapUtils.getString(requestMap, "receiver");
    String question = MapUtils.getString(requestMap, "question");
    if (customerNo == null) {
      customerNo = MapUtils.getString(requestMap, "sender");
      if (customerNo == null) {
        customerNo = loginUser.getCustomerNo();
      }
    }
    String tenantId = loginUser.getTenantId();
    List<QuickReplyMessage> quickReplyMessages =
        chatBiz.generateQuickReplies(new ArrayList<String>(Arrays.asList(question)), 3);
    return RespBodyHandler.setRespBodyListDto(quickReplyMessages);
  }

}
