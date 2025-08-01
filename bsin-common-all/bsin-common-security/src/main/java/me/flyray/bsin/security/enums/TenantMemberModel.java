package me.flyray.bsin.security.enums;

import com.fasterxml.jackson.annotation.JsonCreator;

/**
 * @author leonard
 * @description 接入平台的会员模型：1 租户会员，会员属于租户（实际属于租户直属商户会员） 2 商户会员，会员属于商户
 * @createDate 2024/10/30 20:57
 */
public enum TenantMemberModel {

  /** 租户会员 */
  UNDER_TENANT("1", "租户会员"),

  /** 商户会员 */
  UNDER_MERCHANT("2", "商户会员"),

  /** 商户会员 */
  UNDER_STORE("3", "门店会员");

  private String code;

  private String desc;

  TenantMemberModel(String code, String desc) {
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
  public static TenantMemberModel getInstanceById(Integer id) {
    if (id == null) {
      return null;
    }
    for (TenantMemberModel status : values()) {
      if (id.equals(status.getCode())) {
        return status;
      }
    }
    return null;
  }
}
