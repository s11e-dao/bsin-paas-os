package me.flyray.bsin.payment.enums;

import com.fasterxml.jackson.annotation.JsonCreator;

/**
 * @author leonard
 * @date 2025/07/23 0:02
 * @desc 支付商户模式
 */
public enum PayMerchantModeEnum {
  NORMAL_MERCHANT_MODE("1", "普通商户模式"),
  SPECIAL_MERCHANT_MODE("2", "特约商户模式"),
  ISV_SUB_MERCHANT_MODE("3", "服务商子商户模式"),
  ;

  private String code;

  private String desc;

  PayMerchantModeEnum(String code, String desc) {
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
  public static PayMerchantModeEnum getInstanceById(String id) {
    if (id == null) {
      return null;
    }
    for (PayMerchantModeEnum status : values()) {
      if (id.equals(status.getCode())) {
        return status;
      }
    }
    return null;
  }
}
