package me.flyray.bsin.domain.enums;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum CopilotType {
  //  copilot类型：1、品牌官 2、数字分身 3、通用Copilot
  /** */
  BRAND_OFFICER("1", "品牌官"),

  /** */
  DIGITAL_AVATAR("2", "数字分身"),

  /** */
  KNOWLEDGE_BASE_BOT("3", "通用Copilot");

  private String code;

  private String desc;

  CopilotType(String code, String desc) {
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
  public static CopilotType getInstanceById(String id) {
    if (id == null) {
      return null;
    }
    for (CopilotType status : values()) {
      if (id.equals(status.getCode())) {
        return status;
      }
    }
    return null;
  }
}
