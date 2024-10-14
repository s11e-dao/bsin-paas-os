package me.flyray.bsin.enums;

import com.fasterxml.jackson.annotation.JsonCreator;

/** 0、手机号 1、邮箱 2、QQ 3、微信 4、用户名 5、微博 */
public enum AuthMethod {

  /** 手机号验证登录 */
  PHONE("1", "手机号验证登录"),
  /** 邮箱验证登录 */
  EMAIL("2", "邮箱验证登录"),
  /** QQ授权登录 */
  QQ("3", "QQ授权登录"),

  /** 微信授权登录 */
  WECHAT("4", "微信授权登录"),

  /** 用户名登录 */
  USERNAME("5", "用户名登录"),

  /** open(微信开放平台) */
  WEIBO("6", "微博验证登录");
  private String type;

  private String desc;

  AuthMethod(String code, String desc) {
    this.type = code;
    this.desc = desc;
  }

  public String getType() {
    return type;
  }

  public String getDesc() {
    return desc;
  }

  /** Json 枚举序列化 */
  @JsonCreator
  public static AuthMethod getInstanceByType(String id) {
    if (id == null) {
      return null;
    }
    for (AuthMethod status : values()) {
      if (id.equals(status.getType())) {
        return status;
      }
    }
    return null;
  }
}
