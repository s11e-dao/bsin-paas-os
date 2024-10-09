package me.flyray.bsin.thirdauth.wx.utils;

import java.util.List;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

/**
 * wechat mp properties
 *
 * @author <a href="https://github.com/binarywang">Binary Wang</a>
 */
@Data
public class WxMpProperties {
  /** 是否使用redis存储access token */
  private boolean useRedis;

  /** redis 配置 */
  private WxRedisConfig redisConfig;

  /** 多个公众号配置信息 */
  private List<MpConfig> configs;

  @Getter
  @Setter
  public static class MpConfig {
    /** 设置微信公众号的appid */
    private String appId;

    /** 设置微信公众号的app secret */
    private String secret;

    /** 设置微信公众号的token */
    private String token;

    /** 设置微信公众号的EncodingAESKey */
    private String aesKey;

    /** chatGPT key */
    private String gptKey;
  }

//  @Override
//  public String toString() {
//    return JsonUtils.toJson(this);
//  }
}
