package me.flyray.bsin.payment.enums;

import com.fasterxml.jackson.annotation.JsonCreator;

/**
 * @author leonard
 * @date 2024/11/06 0:02
 * @desc 支付接口版本枚举
 */
public enum PayInterfaceVersion {
  WX_V2("V2", "微信支付V2"),
  WX_V3("V3", "微信支付V3"),
  ALI_V2("V2", "支付宝支付V2"),
  ALI_V3("V3", "支付宝支付V3"),
  ;

  private String code;

  private String desc;

  PayInterfaceVersion(String code, String desc) {
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
  public static PayInterfaceVersion getInstanceById(String id) {
    if (id == null) {
      return null;
    }
    for (PayInterfaceVersion status : values()) {
      if (id.equals(status.getCode())) {
        return status;
      }
    }
    return null;
  }
}
