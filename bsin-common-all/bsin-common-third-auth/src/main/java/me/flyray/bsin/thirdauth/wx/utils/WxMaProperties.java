package me.flyray.bsin.thirdauth.wx.utils;

import java.util.List;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

/**
 * wechat miniapp properties
 *
 * @author <a href="https://github.com/binarywang">Binary Wang</a>
 */
@Data
public class WxMaProperties {
  /** 是否使用redis存储access token */
  private boolean useRedis;

  /** redis 配置 */
  private WxRedisConfig redisConfig;

  /** 多个小程序配置信息 */
  private List<MaConfig> configs;

  @Getter
  @Setter
  public static class MaConfig {
    /** 设置微信小程序的appid */
    private String appId;

    /** 设置微信小程序的Secret */
    private String secret;

    /** 设置微信小程序消息服务器配置的token */
    private String token;

    /** 设置微信小程序消息服务器配置的EncodingAESKey */
    private String aesKey;

    /** 消息格式，XML或者JSON */
    private String msgDataFormat;
  }

//  @Override
//  public String toString() {
//    return JsonUtils.toJson(this);
//  }
}
