package me.flyray.bsin.domain.enums;

import com.fasterxml.jackson.annotation.JsonCreator;

/**
 * @author
 * @description 客户|租户|商户|代理商类型 0、个人客户 1、企业客户
 * @createDate 2024/10/2024/10/13 /23/59
 */
public enum CustomerType {

  /** 个人客户 */
  PERSONAL("0", "个人客户"),

  /** 企业客户 */
  ENTERPRISE("1", "企业客户");

  private String code;

  private String desc;

  CustomerType(String code, String desc) {
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
  public static CustomerType getInstanceById(Integer id) {
    if (id == null) {
      return null;
    }
    for (CustomerType status : values()) {
      if (id.equals(status.getCode())) {
        return status;
      }
    }
    return null;
  }
}
