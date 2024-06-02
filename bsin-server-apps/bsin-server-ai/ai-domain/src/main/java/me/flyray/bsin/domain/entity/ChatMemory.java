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
 * @TableName ai_chat_memory
 */
@Data
@TableName(value = "ai_chat_memory")
public class ChatMemory {

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

  /** 提示词模版类型：提示词模版类型：1、RAG(知识库问答的提示词模版) 2、Copilot(个人助手的提示词模板) */
  private String type;

  private String information;

  /** 知识库封面图片 */
  private String coverImage;

  /** 状态： 0：禁用 1:启用 */
  private String status;

  /** 访问权限： 1-private 2-public */
  private String accessAuthority;

  /** 逻辑删除 0、未删除 1、已删除 */
  @TableLogic(value = "0", delval = "1")
  private Integer delFlag;

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

  public ChatMemory(
      String serialNo,
      String tenantId,
      String merchantNo,
      String customerNo,
      String name,
      String type,
      String information,
      String description) {
    ChatMemory.Builder builder =
        newBuilder()
            .withSerialNo(getOrDefault(serialNo, BsinSnowflake.getId()))
            .withTenantId(getOrDefault(tenantId, "tenantId"))
            .withMerchantNo(getOrDefault(merchantNo, "merchantNo"))
            .withCustomerNo(getOrDefault(customerNo, "customerNo"))
            .withName(getOrDefault(name, "知识库问答提示词模板"))
            .withType(getOrDefault(type, "1"))
            .withInformation(
                getOrDefault(
                    information,
                    "<指令>根据已知信息，简洁和专业的来回答问题。如果无法从中得到答案，请说\\'根据已知信息无法回答该问题\\'，不允许在答案中添加编造成分，答案请使用中文。 </指令> <已知信息>{{ context }}</已知信息>、<问题>{{ question }}</问题>"))
            .withDescription(getOrDefault(description, "description"));
    this.serialNo = builder.getSerialNo();
    this.tenantId = builder.getTenantId();
    this.merchantNo = builder.getMerchantNo();
    this.customerNo = builder.getCustomerNo();
    this.name = builder.getName();
    this.type = builder.getType();
    this.information = builder.getInformation();
    this.description = builder.getDescription();
  }

  public static ChatMemory.Builder newBuilder() {
    return new ChatMemory.Builder();
  }

  @Data
  public static class Builder {
    private String serialNo;
    private String tenantId;
    private String merchantNo;
    private String customerNo;
    private String name;
    private String type;
    private String information;
    private String description;

    protected Builder() {}

    public ChatMemory.Builder withSerialNo(String serialNo) {
      this.serialNo = serialNo;
      return this;
    }

    public ChatMemory.Builder withMerchantNo(String merchantNo) {
      this.merchantNo = merchantNo;
      return this;
    }

    public ChatMemory.Builder withTenantId(String tenantId) {
      this.tenantId = tenantId;
      return this;
    }

    public ChatMemory.Builder withCustomerNo(String customerNo) {
      this.customerNo = customerNo;
      return this;
    }

    public ChatMemory.Builder withName(String name) {
      this.name = name;
      return this;
    }

    public ChatMemory.Builder withType(String type) {
      this.type = type;
      return this;
    }

    public ChatMemory.Builder withDescription(String description) {
      this.description = description;
      return this;
    }

    public ChatMemory.Builder withInformation(String information) {
      this.information = information;
      return this;
    }

    public ChatMemory build() {
      return new ChatMemory(
          serialNo, tenantId, merchantNo, customerNo, name, type, information, description);
    }
  }
}
