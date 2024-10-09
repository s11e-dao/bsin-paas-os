package me.flyray.bsin.thirdauth.wx.utils;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @author <a href="https://github.com/binarywang">Binary Wang</a>
 */
@Data
public class WxCpProperties {

  private List<CpConfig> CpConfigs;

  @Getter
  @Setter
  public static class CpConfig {

    /** 设置企业微信的corpId */
    private String corpId;

    /** 设置企业微信应用的AgentId */
    private Integer agentId;

    /** 设置企业微信应用的Secret */
    private String secret;

    /** 设置企业微信应用的token */
    private String token;

    /** 设置企业微信应用的EncodingAESKey */
    private String aesKey;
  }

//  @Override
//  public String toString() {
//    return JsonUtils.toJson(this);
//  }
}
