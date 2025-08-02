package me.flyray.bsin.payment.enums;

import com.fasterxml.jackson.annotation.JsonCreator;

/**
 * @author leonard
 * @date 2025/08/02 18:02
 * @desc 分润类型（1-按订单分润，2-按商品分润）
 */
public enum ProfitSharingTypeEnum {
  BY_ORDER("1", "按订单分润"),
  BY_PRODUCT("2", "按商品分润"),
  ;

  private String code;

  private String desc;

  ProfitSharingTypeEnum(String code, String desc) {
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
  public static ProfitSharingTypeEnum getInstanceById(String id) {
    if (id == null) {
      return null;
    }
    for (ProfitSharingTypeEnum status : values()) {
      if (id.equals(status.getCode())) {
        return status;
      }
    }
    return null;
  }
}
