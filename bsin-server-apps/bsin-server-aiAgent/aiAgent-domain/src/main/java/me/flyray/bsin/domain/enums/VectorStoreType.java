package me.flyray.bsin.domain.enums;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum VectorStoreType {

  /** knowledge base */
  KNOWLEDGE_BASE("1", "knowledgeBase"),

  /** chat history */
  CHAT_HISTORY("2", "chatHistory"),

  /** chat history summary */
  CHAT_HISTORY_SUMMARY("3", "chatHistorySummary");

  private String code;

  private String desc;

  VectorStoreType(String code, String desc) {
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
  public static VectorStoreType getInstanceById(String id) {
    if (id == null) {
      return null;
    }
    for (VectorStoreType status : values()) {
      if (id.equals(status.getCode())) {
        return status;
      }
    }
    return null;
  }
}
