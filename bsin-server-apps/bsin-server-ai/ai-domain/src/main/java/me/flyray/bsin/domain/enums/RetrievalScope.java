package me.flyray.bsin.domain.enums;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum RetrievalScope {

  /** KNOWLEDGE_BASE */
  KNOWLEDGE_BASE("1", "知识库检索"),

  /** KNOWLEDGE_BASE */
  KNOWLEDGE_BASE_FILE("2", "知识库文件检索"),

  /** CHAT_CONTEX */
  CHAT_CONTEX("3", "聊天上下文"),

  /** CHAT_CONTEX_WITH_KNOWLEDGE_BASE */
  CHAT_CONTEX_WITH_KNOWLEDGE_BASE("4", "知识库+聊天上下文");

  private String code;

  private String desc;

  RetrievalScope(String code, String desc) {
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
  public static RetrievalScope getInstanceById(String id) {
    if (id == null) {
      return null;
    }
    for (RetrievalScope status : values()) {
      if (id.equals(status.getCode())) {
        return status;
      }
    }
    return null;
  }
}
