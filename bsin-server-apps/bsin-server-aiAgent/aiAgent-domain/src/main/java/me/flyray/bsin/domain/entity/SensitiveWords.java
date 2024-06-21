package me.flyray.bsin.domain.entity;

import static dev.langchain4j.internal.Utils.getOrDefault;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;
import me.flyray.bsin.utils.BsinSnowflake;

/**
 * @TableName ai_sensitive_words
 */
@Data
@TableName(value = "ai_sensitive_words")
public class SensitiveWords implements Serializable {

  @TableId(value = "serial_no", type = IdType.ASSIGN_ID)
  private String serialNo;

  /** 租户id */
  private String tenantId;

  /** 商户号 */
  private String merchantNo;

  /** 客户编号 */
  private String customerNo;

  /** 名称 */
  private String name;

  /** 类型： */
  private String type;

  /** 内容 */
  private String content;

  /** 状态： 0：禁用 1:启用 */
  private String status;

  /** 访问权限： 1-private 2-public */
  private String accessAuthority;

  /** 逻辑删除 0、未删除 1、已删除 */
  @TableLogic(value = "0", delval = "1")
  private Integer delFlag;

  /** 是否为默认商户或者用户copilot */
  private Boolean defaultFlag;

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

  public SensitiveWords() {}

  public SensitiveWords(
      String serialNo,
      String tenantId,
      String merchantNo,
      String customerNo,
      String name,
      String type,
      String fileUrl,
      String description) {
    Builder builder =
        newBuilder()
            .withSerialNo(getOrDefault(serialNo, BsinSnowflake.getId()))
            .withTenantId(getOrDefault(tenantId, "tenantId"))
            .withMerchantNo(getOrDefault(merchantNo, "merchantNo"))
            .withCustomerNo(getOrDefault(customerNo, "customerNo"))
            .withName(getOrDefault(name, "敏感词1"))
            .withType(getOrDefault(type, "1"))
            .withContent(getOrDefault(fileUrl, "敏感词"))
            .withDescription(getOrDefault(description, "description"));
    this.serialNo = builder.getSerialNo();
    this.tenantId = builder.getTenantId();
    this.merchantNo = builder.getMerchantNo();
    this.customerNo = builder.getCustomerNo();
    this.name = builder.getName();
    this.type = builder.getType();
    this.content = builder.getContent();
    this.description = builder.getDescription();
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
    private String content;

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

    public Builder withContent(String content) {
      this.content = content;
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

    public SensitiveWords build() {
      return new SensitiveWords(
          serialNo, tenantId, merchantNo, customerNo, name, type, content, description);
    }
  }
}
