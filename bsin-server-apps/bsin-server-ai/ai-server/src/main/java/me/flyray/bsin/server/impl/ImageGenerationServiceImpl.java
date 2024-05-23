package me.flyray.bsin.server.impl;

import static java.net.Proxy.Type.HTTP;
import static dev.ai4j.openai4j.image.ImageModel.DALL_E_QUALITY_HD;


import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.shenyu.client.apache.dubbo.annotation.ShenyuDubboService;
import org.apache.shenyu.client.apidocs.annotations.ApiDoc;
import org.apache.shenyu.client.apidocs.annotations.ApiModule;
import org.apache.shenyu.client.dubbo.common.annotation.ShenyuDubboClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.List;
import java.util.Map;

import cn.hutool.core.util.CharsetUtil;
import cn.hutool.crypto.symmetric.SymmetricAlgorithm;
import cn.hutool.crypto.symmetric.SymmetricCrypto;
import dev.langchain4j.data.image.Image;
import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.image.ImageModel;
import dev.langchain4j.model.openai.OpenAiImageModel;
import dev.langchain4j.model.output.Response;
import dev.langchain4j.model.output.TokenUsage;
import lombok.extern.slf4j.Slf4j;
import me.flyray.bsin.constants.ResponseCode;
import me.flyray.bsin.domain.domain.CopilotInfo;
import me.flyray.bsin.domain.domain.LLMParam;
import me.flyray.bsin.domain.domain.RedisChatMessage;
import me.flyray.bsin.domain.enums.LLMType;
import me.flyray.bsin.domain.enums.VectorStoreType;
import me.flyray.bsin.exception.BusinessException;
import me.flyray.bsin.facade.service.ImageGenerationService;
import me.flyray.bsin.infrastructure.mapper.CopilotMapper;
import me.flyray.bsin.infrastructure.mapper.LLMMapper;
import me.flyray.bsin.security.contex.LoginInfoContextHelper;
import me.flyray.bsin.security.domain.LoginUser;
import me.flyray.bsin.server.biz.ModelBiz;
import me.flyray.bsin.server.biz.PromptEngineeringBiz;
import me.flyray.bsin.server.memory.chat.BufferWindowInRamStoreMemory;
import me.flyray.bsin.server.memory.store.InRamStore;
import me.flyray.bsin.server.memory.store.InRedisStore;
import me.flyray.bsin.server.utils.RespBodyHandler;


@ShenyuDubboService(path = "/imageGeneration", timeout = 6000)
@ApiModule(value = "imageGeneration")
@Service
@Slf4j
public class ImageGenerationServiceImpl implements ImageGenerationService {

  @Value("${langchain4j.chat-model.openai.api-key}")
  private String OPENAI_API_KEY;

  @Value("${bsin.ai.aesKey}")
  private String aesKey;

  @Autowired private EmbeddingModel embeddingModel;
  @Autowired private LLMMapper llmMapper;
  @Autowired private CopilotMapper copilotMapper;

  @Autowired private InRamStore inRamStore;
  @Autowired private PromptEngineeringBiz promptEngineeringBiz;
  @Autowired private InRedisStore inRedisStore;
  @Autowired private ModelBiz modelBiz;

  @ApiDoc(desc = "generateImage")
  @ShenyuDubboClient("/generateImage")
  @Override
  public Map<String, Object> generateImage(Map<String, Object> requestMap) {
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
    String copilotNo = MapUtils.getString(requestMap, "copilotNo");
    String question = MapUtils.getString(requestMap, "question");
    String promptTemplateNo = MapUtils.getString(requestMap, "promptTemplateNo");
    String llmNo = MapUtils.getString(requestMap, "llmNo");
    inRedisStore.rightPushMessage(
        "chat:",
        RedisChatMessage.newBuilder()
            .withSender(customerNo)
            .withReceiver(copilotNo)
            .withMessage(question)
            .build());

    // 1.查询Copilot
    CopilotInfo copilotInfo = copilotMapper.selectById(copilotNo);
    if (copilotInfo == null) {
      throw new BusinessException("100000", "未找到Copilot！！！" + copilotNo);
    }

    // 3.1  查找Copilot对应的LLM
    if (llmNo == null) {
      llmNo = copilotInfo.getLlmNo();
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

    // 3. generate Image with ImageModel
    ImageModel imageModel = null;

    SymmetricCrypto aes = new SymmetricCrypto(SymmetricAlgorithm.AES, aesKey.getBytes());
    String apiKey = aes.decryptStr(llmParam.getApiKey(), CharsetUtil.CHARSET_UTF_8);

    if (LLMType.OPEN_Ai_GPT_3_5_TURBO.getDesc().equals(llmParam.getName())) {
      // 开启代理
      if (StringUtils.isNotEmpty(llmParam.getProxyUrl()) && llmParam.getProxyPort() != null) {
        imageModel =
            OpenAiImageModel.builder()
                .apiKey(apiKey)
                .quality(DALL_E_QUALITY_HD)
                .logRequests(true)
                .logResponses(true)
                //                .withPersisting()   //本地缓存
                .proxy(
                    new Proxy(
                        HTTP,
                        new InetSocketAddress(llmParam.getProxyUrl(), llmParam.getProxyPort())))
                .build();
      } else {
        imageModel = OpenAiImageModel.withApiKey(apiKey);
      }
    } else {

    }

    Response<Image> response = imageModel.generate(question);
    System.out.println(response);
    System.out.println(response.content().url());

    TokenUsage tokenUsage = response.tokenUsage();
    AiMessage aiMessage = new AiMessage(response.content().url().toString());
    String answer = aiMessage.text();
    chatMemory.add(aiMessage);

    inRedisStore.rightPushMessage(
        "chat:",
        RedisChatMessage.newBuilder()
            .withSender(copilotNo)
            .withReceiver(customerNo)
            .withMessage(answer)
            .withType("image")
            .build());

    requestMap.put("answer", answer);
    requestMap.put("url", response.content());
    requestMap.put("tokenUsage", tokenUsage);
    return RespBodyHandler.setRespBodyDto(requestMap);
  }

  //  ImageModel model = OpenAiImageModel.builder()
  //          .apiKey(System.getenv("OPENAI_API_KEY"))
  //          .quality(DALL_E_QUALITY_HD)
  //          .logRequests(true)
  //          .logResponses(true)
  //          .withPersisting()
  //          .build();
  //
  //  EmbeddingModel embeddingModel = new AllMiniLmL6V2EmbeddingModel();
  //  EmbeddingStore<TextSegment> embeddingStore = new InMemoryEmbeddingStore<>();
  //
  //  EmbeddingStoreIngestor ingestor = EmbeddingStoreIngestor
  //          .builder()
  //          .documentSplitter(DocumentSplitters.recursive(1000, 0))
  //          .embeddingModel(embeddingModel)
  //          .embeddingStore(embeddingStore)
  //          .build();
  //
  //  Document document = loadDocument(
  //          Paths.get(
  //                  Objects
  //                          .requireNonNull(
  //
  // OpenAiImageModelExamples.class.getResource("example-files/story-about-happy-carrot.txt")
  //                          )
  //                          .toURI()
  //          ),
  //          new TextDocumentParser()
  //  );
  //            ingestor.ingest(document);
  //
  //  ConversationalRetrievalChain chain = ConversationalRetrievalChain
  //          .builder()
  //
  // .chatLanguageModel(OpenAiChatModel.builder().apiKey(System.getenv("OPENAI_API_KEY")).build())
  //          .retriever(EmbeddingStoreRetriever.from(embeddingStore, embeddingModel))
  //          .build();
  //
  //  PromptTemplate drawPromptTemplate = PromptTemplate.from(
  //          "Draw {{object}}. Base the picture on following information:\n\n{{information}}"
  //  );
  //
  //  Map<String, Object> variables = new HashMap<>();
  //            variables.put("information", chain.execute("Who is Charlie?"));
  //            variables.put("object", "Ultra realistic Charlie on the party, cinematic lighting");
  //
  //  Response<Image> response = model.generate(drawPromptTemplate.apply(variables).text());
  //
  //            System.out.println(response.content().url()); // Enjoy your locally stored picture
  // of Charlie on the party :)

  @ApiDoc(desc = "generateImageByDocument")
  @ShenyuDubboClient("/generateImageByDocument")
  @Override
  public Map<String, Object> generateImageByDocument(Map<String, Object> requestMap) {
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
    String copilotNo = MapUtils.getString(requestMap, "copilotNo");
    String question = MapUtils.getString(requestMap, "question");
    String promptTemplateNo = MapUtils.getString(requestMap, "promptTemplateNo");
    String llmNo = MapUtils.getString(requestMap, "llmNo");
    inRedisStore.rightPushMessage(
        "chat:",
        RedisChatMessage.newBuilder()
            .withSender(customerNo)
            .withReceiver(copilotNo)
            .withMessage(question)
            .build());

    // 1.查询Copilot
    CopilotInfo copilotInfo = copilotMapper.selectById(copilotNo);
    if (copilotInfo == null) {
      throw new BusinessException("100000", "未找到Copilot！！！" + copilotNo);
    }

    // 3.1  查找知识库对应的LLM
    if (llmNo == null) {
      llmNo = copilotInfo.getLlmNo();
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

    // 2.2 最近k条历史聊天记录
    List<ChatMessage> chatBufferWindowList = chatMemory.messages();

    if (promptTemplateNo == null) {
      promptTemplateNo = copilotInfo.getPromptTemplateNo();
    }
    List<ChatMessage> promptChatMessage =
        promptEngineeringBiz.generatePromptMessages(
            promptTemplateNo,
            null,
            chatBufferWindowList,
            null,
            question,
            chatMemory,
            VectorStoreType.KNOWLEDGE_BASE);

    // 3. generate Image with LLM
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
            .withSender(copilotNo)
            .withReceiver(customerNo)
            .withMessage(answer)
            .build());

    // TODO: return chatVO
    requestMap.put("answer", answer);
    requestMap.put("tokenUsage", tokenUsage);
    requestMap.put("prompt", promptEngineeringBiz.toPromptMessages(promptChatMessage));
    requestMap.put("chatHistorySummary", null);
    requestMap.put("chatBufferWindow", promptEngineeringBiz.toPromptMessages(chatBufferWindowList));
    return RespBodyHandler.setRespBodyDto(requestMap);
  }

}
