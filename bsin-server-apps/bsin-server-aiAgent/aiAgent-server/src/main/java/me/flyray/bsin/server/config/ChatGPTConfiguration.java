package me.flyray.bsin.server.config;

import lombok.Data;
import me.flyray.bsin.utils.JsonUtils;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import java.util.List;

@Data
public class ChatGPTConfiguration {

  private String userId;
  private String userContent;
  /** 是否启用模板推送 */
  private boolean templateEnable;
  /** 是否启用上下文 */
  private boolean contextEnable;

  /** 是否启用systemRole */
  private boolean systemRoleEnable;

  /** 是否启用proxy */
  private boolean proxyEnable = false;

  /** 计费模式 */
  private boolean rechargeable = false;

  /** 上下文条数 */
  private int contextLimitNum;

  /** 上下文时间 */
  private long contextExpire;

  /** 设置chatGPT模型 */
  private String model;

  /** 设置chatGPT url */
  private String url;

  /** 设置镜像chatGPT url */
  private String mirrorUrl;

  /** 设置chatGPT maxToken */
  private Integer maxToken;

  /** 设置chatGPT temperature */
  private Double temperature;

  /** 多key配置 */
  private String key;

  /** 多key配置 */
  private List<String> keyList;

  /** 接口异常回复 */
  private String exceptionResponse;
  /** 回复前置 */
  private String replyPrefix;

  /** 预回复 */
  private String preResponse;

  /** 欠费回复 */
  private String outofcreditResponse;

  /** 请求时间限制:ms */
  private long requestTimeLimit;

  /** 敏感词回复 */
  private String filterCheckResponse;

  /** 替换词汇 */
  private List<String> sessionSelfIntroduction;

  /** 自我介绍词汇 */
  private String selfIntroductioncopywriting;

  /** systemContent */
  private String systemContent;

  /** 代理服务器地址 */
  private String proxyServer = "127.0.0.1";

  /** 代理服务器端口 */
  private int proxyPort = 8889;
  /** role */
  private List<String> role;

  @Override
  public String toString() {
    return JsonUtils.toJson(this);
  }
}
