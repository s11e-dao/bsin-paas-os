package me.flyray.bsin.enums;

import com.fasterxml.jackson.annotation.JsonCreator;

/** 微信平台类别：mp(公众号服务订阅号)、miniapp(小程序)、 cp(企业号|企业微信)、pay(微信支付)、open(微信开放平台) */
public enum AppType {

  /** mp(公众号服务订阅号) */
  APPLICATION("1", "bsin-paas应用"),
  /** mp(公众号服务订阅号) */
  INTERFACE("2", "bsin-paas接口"),
  /** mp(公众号服务订阅号) */
  WX_MP("3", "公众号服务订阅号"),

  /** miniapp(小程序) */
  WX_MINIAPP("4", "小程序"),

  /** cp(企业号|企业微信) */
  WX_CP("5", "企业号|企业微信"),

  /** cp(企业号|企业微信) */
  WX_PAY("6", "pay(微信支付)"),

  /** open(微信开放平台) */
  WX_OPEN("7", "微信开放平台"),

  /** open(微信开放平台) */
  WX_WECHAT("8", "个人微信"),
  /** open(微信开放平台) */
  WX_MENU("9", "微信菜单");
  private String type;

  private String desc;

  AppType(String code, String desc) {
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
  public static AppType getInstanceByType(String id) {
    if (id == null) {
      return null;
    }
    for (AppType status : values()) {
      if (id.equals(status.getType())) {
        return status;
      }
    }
    return null;
  }
}
