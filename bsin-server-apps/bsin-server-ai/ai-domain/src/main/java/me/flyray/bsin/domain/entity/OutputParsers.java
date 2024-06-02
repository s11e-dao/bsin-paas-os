package me.flyray.bsin.domain.entity;

import static dev.langchain4j.internal.Utils.getOrDefault;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.util.Date;
import lombok.Data;
import me.flyray.bsin.utils.BsinSnowflake;

/**
 * @TableName ai_output_parsers
 */
@Data
@TableName(value = "ai_output_parsers")
public class OutputParsers {

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

  /** 解析模型类型：1、json 2、StructuredOutputParser */
  private String type;

  /** 解析的字段列表 */
  private String responseSchema;

  /** 知识库封面图片 */
  private String coverImage;

  /** 状态： 0：禁用 1:启用 */
  private String status;

  /** 访问权限： 1-private 2-public */
  private String accessAuthority;

  /** 逻辑删除 0、未删除 1、已删除 */
  @TableLogic(value = "0", delval = "1")
  private Integer delFlag;

  /** 是否可编辑|删除 */
  private Boolean editable;

  /** 创建人 */
  private String createBy;

  /** 描述 */
  private String description;

  /** 创建时间 */
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
  private Date createTime;

  /** 更新时间 */
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
  private Date updateTime;

  public OutputParsers(
      String serialNo,
      String tenantId,
      String merchantNo,
      String customerNo,
      String name,
      String type,
      String responseSchema,
      String description) {
    OutputParsers.Builder builder =
        newBuilder()
            .withSerialNo(getOrDefault(serialNo, BsinSnowflake.getId()))
            .withTenantId(getOrDefault(tenantId, "tenantId"))
            .withMerchantNo(getOrDefault(merchantNo, "merchantNo"))
            .withCustomerNo(getOrDefault(customerNo, "customerNo"))
            .withName(getOrDefault(name, "结构化输出解释器"))
            .withType(getOrDefault(type, "1"))
            .withResponseSchema(getOrDefault(responseSchema, ""))
            .withDescription(getOrDefault(description, "description"));
    this.serialNo = builder.getSerialNo();
    this.tenantId = builder.getTenantId();
    this.merchantNo = builder.getMerchantNo();
    this.customerNo = builder.getCustomerNo();
    this.name = builder.getName();
    this.type = builder.getType();
    this.responseSchema = builder.getResponseSchema();
    this.description = builder.getDescription();
  }

  public static OutputParsers.Builder newBuilder() {
    return new OutputParsers.Builder();
  }

  @Data
  public static class Builder {
    private String serialNo;
    private String tenantId;
    private String merchantNo;
    private String customerNo;
    private String name;
    private String type;
    private String responseSchema;
    private String description;

    protected Builder() {}

    public OutputParsers.Builder withSerialNo(String serialNo) {
      this.serialNo = serialNo;
      return this;
    }

    public OutputParsers.Builder withMerchantNo(String merchantNo) {
      this.merchantNo = merchantNo;
      return this;
    }

    public OutputParsers.Builder withTenantId(String tenantId) {
      this.tenantId = tenantId;
      return this;
    }

    public OutputParsers.Builder withCustomerNo(String customerNo) {
      this.customerNo = customerNo;
      return this;
    }

    public OutputParsers.Builder withName(String name) {
      this.name = name;
      return this;
    }

    public OutputParsers.Builder withType(String type) {
      this.type = type;
      return this;
    }

    public OutputParsers.Builder withDescription(String description) {
      this.description = description;
      return this;
    }

    public OutputParsers.Builder withResponseSchema(String responseSchema) {
      this.responseSchema = responseSchema;
      return this;
    }

    public OutputParsers build() {
      return new OutputParsers(
          serialNo, tenantId, merchantNo, customerNo, name, type, responseSchema, description);
    }
  }
}
