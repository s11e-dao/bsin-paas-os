package me.flyray.bsin.domain.enums;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum OutputParsersType {

  /** JSON */
  JSON("1", "(JSON) parser"),

  /** Structure */
  STRUCTURE("2", "Structure");

  private String code;

  private String desc;

  OutputParsersType(String code, String desc) {
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
  public static OutputParsersType getInstanceById(String id) {
    if (id == null) {
      return null;
    }
    for (OutputParsersType status : values()) {
      if (id.equals(status.getCode())) {
        return status;
      }
    }
    return null;
  }
}
