package me.flyray.bsin.domain.enums;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum PromptTemplateType {

  /** knowledge base */
  RAG("1", "RAG(知识库问答的提示词模版"),

  /** chat with contex */
  BUFFER_WINDOW("2", "滑动窗口上下文聊天提示词模板"),

  /** chat with copilot */
  CHAT_COPILOT("3", "Copilot(个人助手的提示词模板)-上下文聊天+知识库+连天Summary");

  private String code;

  private String desc;

  PromptTemplateType(String code, String desc) {
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
  public static PromptTemplateType getInstanceById(String id) {
    if (id == null) {
      return null;
    }
    for (PromptTemplateType status : values()) {
      if (id.equals(status.getCode())) {
        return status;
      }
    }
    return null;
  }
}
