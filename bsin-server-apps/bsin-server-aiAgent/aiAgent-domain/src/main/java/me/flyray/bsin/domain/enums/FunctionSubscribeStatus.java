package me.flyray.bsin.domain.enums;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum FunctionSubscribeStatus {
  //  c应用|功能状态：0、待缴费 1、待审核  2、正常 3、欠费停止 4、冻结
  /** */
  PENDING_PAYMENT("0", "待缴费"),
  /** */
  PENDING_AUDIT("1", "待审核"),
  /** */
  NORMAL("2", "正常"),

  /** */
  ARREARS_STOP("3", "欠费停止"),

  /** */
  FREEZE("4", "冻结");

  private String code;

  private String desc;

  FunctionSubscribeStatus(String code, String desc) {
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
  public static FunctionSubscribeStatus getInstanceById(String id) {
    if (id == null) {
      return null;
    }
    for (FunctionSubscribeStatus status : values()) {
      if (id.equals(status.getCode())) {
        return status;
      }
    }
    return null;
  }
}
