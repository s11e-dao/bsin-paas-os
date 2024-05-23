package me.flyray.bsin.domain.enums;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum CommandWord {
  /** */
  MP_VERIFY_CODE("1", "获取验证码");

  private String code;

  private String desc;

  CommandWord(String code, String desc) {
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
  public static CommandWord getInstanceById(String id) {
    if (id == null) {
      return null;
    }
    for (CommandWord status : values()) {
      if (id.equals(status.getCode())) {
        return status;
      }
    }
    return null;
  }
}
