package me.flyray.bsin.domain.enums;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum TokenizerModeType {

  /** OpenAI GPT_3_5_TURBO */
  OPENAI("1", "OpenAi"),

  /** Bert */
  BERT("2", "Bert"),

  /** 需要key */
  QWEN("3", "Qwen");

  private String code;

  private String desc;

  TokenizerModeType(String code, String desc) {
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
  public static TokenizerModeType getInstanceById(String id) {
    if (id == null) {
      return null;
    }
    for (TokenizerModeType status : values()) {
      if (id.equals(status.getCode())) {
        return status;
      }
    }
    return null;
  }
}
