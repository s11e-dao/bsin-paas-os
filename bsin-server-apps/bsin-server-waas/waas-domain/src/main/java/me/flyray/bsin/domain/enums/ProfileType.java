package me.flyray.bsin.domain.enums;

import com.fasterxml.jackson.annotation.JsonCreator;

/**
 * @author leonard
 * @date 2024/1/15 9:32
 * @desc profile分类： Brand|Individual
 */
public enum ProfileType {

  /** Brand */
  BRAND("1", "Brand"),
  /** Individual */
  INDIVIDUAL("2", "Individual");

  private String code;

  private String desc;

  ProfileType(String code, String desc) {
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
  public static ProfileType getInstanceById(String id) {
    if (id == null) {
      return null;
    }
    for (ProfileType status : values()) {
      if (id.equals(status.getCode())) {
        return status;
      }
    }
    return null;
  }
}
