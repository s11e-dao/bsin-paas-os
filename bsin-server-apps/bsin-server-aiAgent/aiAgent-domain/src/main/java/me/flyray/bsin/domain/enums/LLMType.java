package me.flyray.bsin.domain.enums;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum LLMType {
  // 对应LLMParams中的name字段
  /** OpenAI GPT_3_5_TURBO */
  OPEN_Ai_GPT_3_5_TURBO("1", "GPT-3.5-Turbo"),

  /** Bert */
  BERT("2", "Bert"),

  /** 阿里dashscope(模型服务灵积) 的通义千问 */
  QWEN_TURBO("3", "qwen-turbo"),

  /** 阿里dashscope(模型服务灵积) 的通义千问 */
  QWEN_PLUS("4", "qwen-plus"),

  /** 阿里dashscope(模型服务灵积) 百川开源大模型 */
  BAICHUAN_7B_V1("5", "baichuan-7b-v1"),

  /** chatGLM local 清华智普清言 */
  CHAT_GLM("6", "ChatGLM"),

  // 百度千帆大模型
  QIANFAN("7", "qianfan"),
  /** amazon */
  BEDROCK("8", "Bedrock"),
  VERTEX_AI_GEMINI("8", "VertexAiGemini"),
  AZURE_OPEN_AI("101", "AzureOpenAi");

  //  // Use with QwenChatModel and QwenLanguageModel
  //  public static final String QWEN_TURBO = "qwen-turbo";  // Qwen base model, 4k context.
  //  public static final String QWEN_PLUS = "qwen-plus";  // Qwen plus model, 8k context.
  //  public static final String QWEN_MAX = "qwen-max";  // Qwen max model, 200-billion-parameters,
  // 8k context.
  //  public static final String QWEN_7B_CHAT = "qwen-7b-chat";  // Qwen open sourced
  // 7-billion-parameters version
  //  public static final String QWEN_14B_CHAT = "qwen-14b-chat";  // Qwen open sourced
  // 14-billion-parameters version
  //
  //  // Use with QwenEmbeddingModel
  //  public static final String TEXT_EMBEDDING_V1 = "text-embedding-v1";

  private String code;

  private String desc;

  LLMType(String code, String desc) {
    this.code = code;
    this.desc = desc;
  }

  public String getCode() {
    return code;
  }

  public String getDesc() {
    return desc;
  }

  /** Json 枚举序列化 */
  @JsonCreator
  public static LLMType getInstanceById(String id) {
    if (id == null) {
      return null;
    }
    for (LLMType status : values()) {
      if (id.equals(status.getCode())) {
        return status;
      }
    }
    return null;
  }
}
