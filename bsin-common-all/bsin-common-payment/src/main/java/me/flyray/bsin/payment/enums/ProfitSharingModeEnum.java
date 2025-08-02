package me.flyray.bsin.payment.enums;

import com.fasterxml.jackson.annotation.JsonCreator;

/**
 * @author leonard
 * @date 2025/08/02 18:02
 * @desc 0-该笔订单不允许分账, 1-支付成功按配置自动完成分账, 2-商户手动分账(解冻商户金额)
 */
public enum ProfitSharingModeEnum {
  NONE("0", "不允许分账"),
  AUTO("1", "自动分账"),
  MANUAL("2", "手动分钟"),
  ;

  private String code;

  private String desc;

  ProfitSharingModeEnum(String code, String desc) {
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
  public static ProfitSharingModeEnum getInstanceById(String id) {
    if (id == null) {
      return null;
    }
    for (ProfitSharingModeEnum status : values()) {
      if (id.equals(status.getCode())) {
        return status;
      }
    }
    return null;
  }
}
