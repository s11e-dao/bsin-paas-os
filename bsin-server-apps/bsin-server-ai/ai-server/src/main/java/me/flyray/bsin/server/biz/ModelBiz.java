package me.flyray.bsin.server.biz;

import static java.net.Proxy.Type.HTTP;
import static dev.langchain4j.agent.tool.JsonSchemaProperty.INTEGER;
import static dev.langchain4j.model.dashscope.QwenModelName.QWEN_PLUS;
import static dev.langchain4j.model.openai.OpenAiModelName.GPT_3_5_TURBO;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.time.Duration;

import cn.hutool.core.util.CharsetUtil;
import cn.hutool.crypto.symmetric.SymmetricAlgorithm;
import cn.hutool.crypto.symmetric.SymmetricCrypto;
import dev.langchain4j.agent.tool.ToolSpecification;
import dev.langchain4j.data.document.DocumentSplitter;
import dev.langchain4j.data.document.splitter.DocumentSplitters;
import dev.langchain4j.model.Tokenizer;
import dev.langchain4j.model.azure.AzureOpenAiChatModel;
import dev.langchain4j.model.azure.AzureOpenAiStreamingChatModel;
import dev.langchain4j.model.bedrock.BedrockAnthropicChatModel;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.chat.StreamingChatLanguageModel;
import dev.langchain4j.model.chatglm.ChatGlmChatModel;
import dev.langchain4j.model.dashscope.QwenChatModel;
import dev.langchain4j.model.dashscope.QwenEmbeddingModel;
import dev.langchain4j.model.dashscope.QwenStreamingChatModel;
import dev.langchain4j.model.dashscope.QwenTokenizer;
import dev.langchain4j.model.embedding.AllMiniLmL6V2EmbeddingModel;
import dev.langchain4j.model.embedding.BertTokenizer;
import dev.langchain4j.model.embedding.BgeSmallZhEmbeddingModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.openai.OpenAiStreamingChatModel;
import dev.langchain4j.model.openai.OpenAiTokenizer;
import dev.langchain4j.model.qianfan.QianfanChatModel;
import dev.langchain4j.model.qianfan.QianfanStreamingChatModel;
import lombok.extern.slf4j.Slf4j;
import me.flyray.bsin.domain.domain.EmbeddingModel;
import me.flyray.bsin.domain.domain.LLMParam;
import me.flyray.bsin.domain.enums.EmbeddingModeType;
import me.flyray.bsin.domain.enums.LLMType;
import me.flyray.bsin.domain.enums.TokenizerModeType;
import me.flyray.bsin.exception.BusinessException;
import software.amazon.awssdk.regions.Region;

/**
 * @author leonard
 * @description
 * @createDate 2024/01/2024/1/10 /17/01
 */
@Component
@Slf4j
public class ModelBiz {

  @Value("${bsin.ai.aesKey}")
  private String aesKey;

  @Value("${bsin.ai.chatGlmBaseUrl}")
  private String chatGlmBaseUrl;

  public dev.langchain4j.model.embedding.EmbeddingModel getEmbeddingModel(
      EmbeddingModel embeddingModel) {
    dev.langchain4j.model.embedding.EmbeddingModel langchin4jEmbeddingModel = null;

    if (EmbeddingModeType.EMBEDDING_2.getDesc().equals(embeddingModel.getName())) {
      //      OpenAiEmbeddingModel(String baseUrl, String apiKey, String organizationId, String
      // modelName, Integer dimensions, String user, Duration timeout, Integer maxRetries, Proxy
      // proxy, Boolean logRequests, Boolean logResponses, Tokenizer tokenizer)
      // TODO: openAI embedding模型
      //      langchin4jEmbeddingModel = new OpenAiEmbeddingModel();
    } else if (EmbeddingModeType.All_MINI_LM_L6_V2.getDesc().equals(embeddingModel.getName())) {
      // TODO: 默认时384维度的向量？？？
      langchin4jEmbeddingModel = new AllMiniLmL6V2EmbeddingModel();
    } else if (EmbeddingModeType.BGE_SMALL_ZH.getDesc().equals(embeddingModel.getName())) {
      // TODO: 默认时512维度的向量？？？
      langchin4jEmbeddingModel = new BgeSmallZhEmbeddingModel();
    } else if (EmbeddingModeType.QWEN_TEXT_EMBEDDING_V2
        .getDesc()
        .equals(embeddingModel.getName())) {
      //      // Use with QwenEmbeddingModel
      //      public static final String TEXT_EMBEDDING_V1 = "text-embedding-v1";  // Support: en,
      // zh, es, fr, pt, id
      //      public static final String TEXT_EMBEDDING_V2 = "text-embedding-v2";  // Support: en,
      // zh, es, fr, pt, id, ja, ko, de, ru
      langchin4jEmbeddingModel =
          new QwenEmbeddingModel(embeddingModel.getApiKey(), "text-embedding-v2");
    } else {
      throw new BusinessException("100000", "不支持的Tokenizer：" + embeddingModel.getTokenizerModel());
    }
    return langchin4jEmbeddingModel;
  }

  public Tokenizer getTokenizerModel(EmbeddingModel embeddingModel) {
    Tokenizer tokenizer = new OpenAiTokenizer(GPT_3_5_TURBO);
    if (TokenizerModeType.OPENAI.getDesc().equals(embeddingModel.getTokenizerModel())) {
    } else if (TokenizerModeType.BERT.getDesc().equals(embeddingModel.getTokenizerModel())) {
      tokenizer = new BertTokenizer();
    } else {
      throw new BusinessException("100000", "不支持的Tokenizer：" + embeddingModel.getTokenizerModel());
    }
    return tokenizer;
  }

  public DocumentSplitter getDocumentSplitter(EmbeddingModel embeddingModel) {
    Tokenizer tokenizer = null;
    if (TokenizerModeType.OPENAI.getDesc().equals(embeddingModel.getTokenizerModel())) {
      tokenizer = new OpenAiTokenizer(GPT_3_5_TURBO);
    } else if (TokenizerModeType.BERT.getDesc().equals(embeddingModel.getTokenizerModel())) {
      tokenizer = new BertTokenizer();
    } else if (TokenizerModeType.QWEN.getDesc().equals(embeddingModel.getTokenizerModel())) {
      tokenizer = new QwenTokenizer(embeddingModel.getApiKey(), QWEN_PLUS);
    } else {
      throw new BusinessException("100000", "不支持的Tokenizer：" + embeddingModel.getTokenizerModel());
    }
    DocumentSplitter documentSplitter =
        DocumentSplitters.recursive(
            embeddingModel.getSegmentSizeInTokens(),
            embeddingModel.getOverlapSizeInTokens(),
            tokenizer);
    return documentSplitter;
  }

  public ChatLanguageModel getChatModel(LLMParam llmParam) {
    ChatLanguageModel chatModel = null;
    SymmetricCrypto aes = new SymmetricCrypto(SymmetricAlgorithm.AES, aesKey.getBytes());
    String apiKey = aes.decryptStr(llmParam.getApiKey(), CharsetUtil.CHARSET_UTF_8);

    if (LLMType.OPEN_Ai_GPT_3_5_TURBO.getDesc().equals(llmParam.getName())) {
      // 开启代理
      if (StringUtils.isNotEmpty(llmParam.getProxyUrl()) && llmParam.getProxyPort() != null) {
        if (LLMType.AZURE_OPEN_AI.getDesc().equals(llmParam.getType())) {
          chatModel =
              AzureOpenAiChatModel.builder()
                  .apiKey(apiKey)
                  .endpoint(llmParam.getApiBaseUrl())
                  .temperature(llmParam.getTemperature())
                  .maxTokens(llmParam.getMaxRequestTokens())
                  .timeout(Duration.ofSeconds(20L))
                  .maxRetries(1)
                  .build();
        } else {
          chatModel =
              OpenAiChatModel.builder()
                  .apiKey(apiKey)
                  .baseUrl(llmParam.getApiBaseUrl())
                  .temperature(llmParam.getTemperature())
                  .maxTokens(llmParam.getMaxRequestTokens())
                  .timeout(Duration.ofSeconds(20L))
                  .maxRetries(1)
                  .proxy(
                      new Proxy(
                          HTTP,
                          new InetSocketAddress(llmParam.getProxyUrl(), llmParam.getProxyPort())))
                  .build();
        }
      } else {
        if (LLMType.AZURE_OPEN_AI.getDesc().equals(llmParam.getType())) {
          chatModel =
              AzureOpenAiChatModel.builder()
                  .apiKey(apiKey)
                  .endpoint(llmParam.getApiBaseUrl())
                  .temperature(llmParam.getTemperature())
                  .maxTokens(llmParam.getMaxRequestTokens())
                  .timeout(Duration.ofSeconds(20L))
                  .maxRetries(1)
                  .build();
        } else {
          chatModel =
              OpenAiChatModel.builder()
                  .apiKey(apiKey)
                  .baseUrl(llmParam.getApiBaseUrl())
                  .temperature(llmParam.getTemperature())
                  .maxTokens(llmParam.getMaxRequestTokens())
                  .timeout(Duration.ofSeconds(20L))
                  .maxRetries(1)
                  .build();
        }
      }
    } else if (LLMType.QWEN_PLUS.getDesc().equals(llmParam.getName())) {
      chatModel =
          QwenChatModel.builder()
              .apiKey(apiKey)
              .enableSearch(llmParam.isEnableSearch())
              .modelName(llmParam.getName())
              .build();

    } else if (LLMType.CHAT_GLM.getDesc().equals(llmParam.getName())) {
      chatModel = ChatGlmChatModel.builder().baseUrl(chatGlmBaseUrl).build();
    } else if (LLMType.QIANFAN.getDesc().equals(llmParam.getName())) {
      // see your api key and secret key here:
      // https://console.bce.baidu.com/qianfan/ais/console/applicationConsole/application
      chatModel =
          QianfanChatModel.builder()
              .modelName("ERNIE-Bot 4.0")
              .temperature(llmParam.getTemperature())
              .topP(1.0) // ？
              .apiKey(apiKey)
              .secretKey(llmParam.getSecretKey())
              .maxRetries(1)
              .build();
      // TODO: ???
      ToolSpecification calculator =
          ToolSpecification.builder()
              .name("calculator")
              .description("returns a sum of two numbers")
              .addParameter("first", INTEGER)
              .addParameter("second", INTEGER)
              .build();

    } else if (LLMType.QIANFAN.getDesc().equals(llmParam.getName())) {
      chatModel =
          BedrockAnthropicChatModel.builder()
              .temperature(llmParam.getTemperature().floatValue())
              .maxTokens(llmParam.getMaxRequestTokens())
              .region(Region.US_EAST_1)
              .model(BedrockAnthropicChatModel.Types.AnthropicClaudeV2)
              .maxRetries(1)
              .build();

    }
    //    else if (LLMType.VERTEX_AI_GEMINI.getDesc().equals(llmParam.getName())) {
    //      // TODO: GCP_PROJECT_ID|GCP_LOCATION
    //      chatModel =
    //          VertexAiGeminiChatModel.builder()
    //              .project(System.getenv("GCP_PROJECT_ID"))
    //              .location(System.getenv("GCP_LOCATION"))
    //              .modelName("gemini-pro")
    //              .build();
    //    }
    // TODO: support other LLM
    else {
      throw new BusinessException("100000", "not supported LLM: " + llmParam.getName());
    }
    return chatModel;
  }

  public StreamingChatLanguageModel getStreamingChatModel(LLMParam llmParam) {
    StreamingChatLanguageModel streamingModel = null;
    SymmetricCrypto aes = new SymmetricCrypto(SymmetricAlgorithm.AES, aesKey.getBytes());
    String apiKey = aes.decryptStr(llmParam.getApiKey(), CharsetUtil.CHARSET_UTF_8);
    if (LLMType.OPEN_Ai_GPT_3_5_TURBO.getDesc().equals(llmParam.getName())) {
      if (StringUtils.isNotEmpty(llmParam.getProxyUrl()) && llmParam.getProxyPort() != null) {
        if (LLMType.AZURE_OPEN_AI.getDesc().equals(llmParam.getType())) {
          streamingModel =
              AzureOpenAiStreamingChatModel.builder()
                  .apiKey(apiKey)
                  .endpoint(llmParam.getApiBaseUrl())
                  .temperature(llmParam.getTemperature())
                  .serviceVersion(System.getenv("AZURE_OPENAI_SERVICE_VERSION"))
                  //                      .deploymentName(deploymentName)
                  //                      .tokenizer(new OpenAiTokenizer(gptVersion))
                  .logRequestsAndResponses(true)
                  .maxTokens(llmParam.getMaxRequestTokens())
                  .build();
        } else {
          streamingModel =
              OpenAiStreamingChatModel.builder()
                  .apiKey(apiKey)
                  .baseUrl(llmParam.getApiBaseUrl())
                  .temperature(llmParam.getTemperature())
                  .maxTokens(llmParam.getMaxRequestTokens())
                  .proxy(
                      new Proxy(
                          HTTP,
                          new InetSocketAddress(llmParam.getProxyUrl(), llmParam.getProxyPort())))
                  .build();
        }
      } else {
        if (LLMType.AZURE_OPEN_AI.getDesc().equals(llmParam.getType())) {
          streamingModel =
              AzureOpenAiStreamingChatModel.builder()
                  .apiKey(apiKey)
                  .endpoint(llmParam.getApiBaseUrl())
                  .temperature(llmParam.getTemperature())
                  .serviceVersion(System.getenv("AZURE_OPENAI_SERVICE_VERSION"))
                  //                      .deploymentName(deploymentName)
                  //                      .tokenizer(new OpenAiTokenizer(gptVersion))
                  .logRequestsAndResponses(true)
                  .maxTokens(llmParam.getMaxRequestTokens())
                  .build();
        } else {
          streamingModel =
              OpenAiStreamingChatModel.builder()
                  .apiKey(apiKey)
                  .baseUrl(llmParam.getApiBaseUrl())
                  .temperature(llmParam.getTemperature())
                  .maxTokens(llmParam.getMaxRequestTokens())
                  .build();
        }
      }
    } else if (LLMType.QWEN_PLUS.getDesc().equals(llmParam.getName())) {
      streamingModel =
          QwenStreamingChatModel.builder()
              .apiKey(apiKey)
              .enableSearch(llmParam.isEnableSearch())
              .modelName(llmParam.getName())
              .build();

    } else if (LLMType.CHAT_GLM.getDesc().equals(llmParam.getName())) {
      // TODO:
      throw new BusinessException("100000", "not supported LLM: " + llmParam.getName());

    } else if (LLMType.QIANFAN.getDesc().equals(llmParam.getName())) {
      // see your api key and secret key here:
      // https://console.bce.baidu.com/qianfan/ais/console/applicationConsole/application
      streamingModel =
          QianfanStreamingChatModel.builder()
              .modelName("ERNIE-Bot 4.0")
              .temperature(0.7)
              .topP(1.0)
              .apiKey(apiKey)
              .secretKey(llmParam.getSecretKey())
              .build();
      ToolSpecification calculator =
          ToolSpecification.builder()
              .name("calculator")
              .description("returns a sum of two numbers")
              .addParameter("first", INTEGER)
              .addParameter("second", INTEGER)
              .build();
    } else if (LLMType.BEDROCK.getDesc().equals(llmParam.getName())) {
      // TODO:
      throw new BusinessException("100000", "not supported LLM: " + llmParam.getName());
    }
    //    else if (LLMType.VERTEX_AI_GEMINI.getDesc().equals(llmParam.getName())) {
    //      // TODO: GCP_PROJECT_ID|GCP_LOCATION
    //      streamingModel =
    //          VertexAiGeminiStreamingChatModel.builder()
    //              .project(System.getenv("GCP_PROJECT_ID"))
    //              .location(System.getenv("GCP_LOCATION"))
    //              .modelName("gemini-pro")
    //              .build();
    //    }
    // TODO: support other LLM
    else {
      throw new BusinessException("100000", "not supported LLM: " + llmParam.getName());
    }
    return streamingModel;
  }
}
