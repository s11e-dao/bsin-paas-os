package me.flyray.bsin.domain.entity;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import me.flyray.bsin.utils.BsinSnowflake;

import static dev.langchain4j.internal.Utils.getOrDefault;

/**
 * @TableName ai_llm
 */
@Data
@TableName(value = "ai_llm")
public class LLMParam implements Serializable {

  @TableId(value = "serial_no", type = IdType.ASSIGN_ID)
  private String serialNo;

  /** 租户id */
  private String tenantId;

  /** 商户号 */
  private String merchantNo;

  /** 客户编号 */
  private String customerNo;

  /** 提示词模版名称 */
  private String name;

  /** 模型类型：1、 */
  private String type;

  /** 代理地址 */
  private String proxyUrl;

  /** 代理端口 */
  private Integer proxyPort;

  /** api请求地址 */
  private String apiBaseUrl;

  /** api秘钥 */
  private String apiKey;

  /** */
  private String secretKey;

  /** 调整模型生成文本时创造性和多样性的超参数:0-1 */
  private Double temperature;

  /** 回复最多token */
  private Integer maxRespTokens;

  /** 请求最多token */
  private Integer maxRequestTokens;

  /** 最多上下文消息数量 */
  private Integer maxMessages;

  /** 触发总结报文的消息数量 */
  private Integer maxSummaryMessages;

  /** 流式返回 */
  private boolean streaming;

  /** 是否参考搜索结果:通义千问 */
  private boolean enableSearch;

  /** 模型封面图片 */
  private String coverImage;

  /** 状态： 0：禁用 1:启用 */
  private String status;

  /** 访问权限： 1-private 2-public */
  private String accessAuthority;

  /** 是否为默认商户或者用户copilot */
  private Boolean defaultFlag;

  /** 逻辑删除 0、未删除 1、已删除 */
  @TableLogic(value = "0", delval = "1")
  private Integer delFlag;

  /** 是否可编辑|删除 */
  private Boolean editable;

  /** 创建人 */
  private String createBy;

  /** 模型描述 */
  private String description;

  /** 创建时间 */
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
  private Date createTime;

  /** 更新时间 */
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
  private Date updateTime;

  public LLMParam() {}

  public LLMParam(
      String serialNo,
      String tenantId,
      String merchantNo,
      String customerNo,
      String name,
      String type,
      String apiBaseUrl,
      String apiKey,
      String proxyUrl,
      Integer proxyPort,
      Double temperature,
      Integer maxRespTokens,
      Integer maxRequestTokens,
      Integer maxMessages,
      Integer maxSummaryMessages,
      String description) {
    Builder builder =
        newBuilder()
            .withSerialNo(getOrDefault(serialNo, BsinSnowflake.getId()))
            .withTenantId(getOrDefault(tenantId, "tenantId"))
            .withMerchantNo(getOrDefault(merchantNo, "merchantNo"))
            .withCustomerNo(getOrDefault(customerNo, "customerNo"))
            .withName(getOrDefault(name, "OpenAI-Turbo-3.5"))
            .withType(getOrDefault(type, "GPT-3.5-Turbo"))
            .withApiBaseUrl(getOrDefault(apiBaseUrl, "https://api.openai.com/v1/chat/completions"))
            .withApiKey(getOrDefault(apiKey, "demo"))
            .withProxyUrl(getOrDefault(proxyUrl, "127.0.0.1"))
            .withProxyPort(getOrDefault(proxyPort, Integer.valueOf("8889")))
            .withTemperature(getOrDefault(temperature, Double.valueOf("0.7")))
            .withMaxRespTokens(getOrDefault(maxRespTokens, Integer.valueOf("4095")))
            .withMaxResquestTokens(getOrDefault(maxRequestTokens, Integer.valueOf("4095")))
            .withMaxMessages(getOrDefault(maxMessages, Integer.valueOf("10")))
            .withMaxSummaryMessages(getOrDefault(maxSummaryMessages, Integer.valueOf("20")))
            .withDescription(getOrDefault(description, "description"));
    this.serialNo = builder.getSerialNo();
    this.tenantId = builder.getTenantId();
    this.merchantNo = builder.getMerchantNo();
    this.customerNo = builder.getCustomerNo();
    this.name = builder.getName();
    this.type = builder.getType();
    this.apiKey = builder.getApiKey();
    this.apiBaseUrl = builder.getApiBaseUrl();
    this.proxyUrl = builder.getProxyUrl();
    this.proxyPort = builder.getProxyPort();
    this.temperature = builder.getTemperature();
    this.description = builder.getDescription();
    this.maxRespTokens = builder.getMaxRespTokens();
    this.maxRequestTokens = builder.getMaxRequestTokens();
    this.maxMessages = builder.getMaxMessages();
    this.maxSummaryMessages = builder.getMaxSummaryMessages();
  }

  public static Builder newBuilder() {
    return new Builder();
  }

  @Data
  public static class Builder {
    private String serialNo;
    private String tenantId;
    private String merchantNo;
    private String customerNo;
    private String name;
    private String type;
    private String description;
    private String apiBaseUrl;
    private String apiKey;
    private Double temperature;
    private String proxyUrl;
    private Integer proxyPort;
    private Integer maxRespTokens;
    private Integer maxRequestTokens;
    private Integer maxMessages;
    private Integer maxSummaryMessages;

    protected Builder() {}

    public Builder withSerialNo(String serialNo) {
      this.serialNo = serialNo;
      return this;
    }

    public Builder withMerchantNo(String merchantNo) {
      this.merchantNo = merchantNo;
      return this;
    }

    public Builder withTenantId(String tenantId) {
      this.tenantId = tenantId;
      return this;
    }

    public Builder withCustomerNo(String customerNo) {
      this.customerNo = customerNo;
      return this;
    }

    public Builder withName(String name) {
      this.name = name;
      return this;
    }

    public Builder withApiBaseUrl(String apiBaseUrl) {
      this.apiBaseUrl = apiBaseUrl;
      return this;
    }

    public Builder withApiKey(String apiKey) {
      this.apiKey = apiKey;
      return this;
    }

    public Builder withProxyUrl(String proxyUrl) {
      this.proxyUrl = proxyUrl;
      return this;
    }

    public Builder withProxyPort(int proxyPort) {
      this.proxyPort = proxyPort;
      return this;
    }

    public Builder withTemperature(Double temperature) {
      this.temperature = temperature;
      return this;
    }

    public Builder withMaxRespTokens(Integer maxRespTokens) {
      this.maxRespTokens = maxRespTokens;
      return this;
    }

    public Builder withMaxResquestTokens(Integer maxRequestTokens) {
      this.maxRequestTokens = maxRequestTokens;
      return this;
    }

    public Builder withMaxMessages(Integer maxMessages) {
      this.maxMessages = maxMessages;
      return this;
    }

    public Builder withMaxSummaryMessages(Integer maxSummaryMessages) {
      this.maxSummaryMessages = maxSummaryMessages;
      return this;
    }

    public Builder withType(String type) {
      this.type = type;
      return this;
    }

    public Builder withDescription(String description) {
      this.description = description;
      return this;
    }

    public LLMParam build() {
      return new LLMParam(
          serialNo,
          tenantId,
          merchantNo,
          customerNo,
          name,
          type,
          apiBaseUrl,
          apiKey,
          proxyUrl,
          proxyPort,
          temperature,
          maxRespTokens,
          maxRequestTokens,
          maxMessages,
          maxSummaryMessages,
          description);
    }
  }
}
