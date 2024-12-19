package me.flyray.bsin.domain.enums;

import com.fasterxml.jackson.annotation.JsonCreator;

/**
 * 会员模型
 * 1平台会员 2商户会员 3店铺会员
 */
public enum MemberModel {

  PLATFORM_MEMBER("1", "平台会员"),

  MERCHANT_MEMBER("2", "商户会员"),

  STORE_MEMBER("3", "店铺会员");

  private String code;

  private String desc;

  MemberModel(String code, String desc) {
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
  public static MemberModel getInstanceById(String id) {
    if (id == null) {
      return null;
    }
    for (MemberModel status : values()) {
      if (id.equals(status.getCode())) {
        return status;
      }
    }
    return null;
  }
}
