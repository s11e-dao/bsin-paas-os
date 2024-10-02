package me.flyray.bsin.domain.enums;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum WxPlatformType {
  //  微信平台类别：mp(公众号服务订阅号)、miniapp(小程序)、 cp(企业号|企业微信)、pay(微信支付)、open(微信开放平台)、wechat(个人微信)
  /** */
  MP("mp", "公众号服务订阅号"),

  /** */
  MINIAPP("miniapp", "小程序"),

  /** */
  CP("cp", "企业号|企业微信"),

  /** */
  PAY("pay", "微信支付"),

  /** */
  OPEN("open", "微信开放平台"),

  /** */
  WECHAT("wechat", "个人微信"),
  /** */
  MENU("menu", "菜单模版");

  private String code;

  private String desc;

  WxPlatformType(String code, String desc) {
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
  public static WxPlatformType getInstanceById(String id) {
    if (id == null) {
      return null;
    }
    for (WxPlatformType status : values()) {
      if (id.equals(status.getCode())) {
        return status;
      }
    }
    return null;
  }
}
