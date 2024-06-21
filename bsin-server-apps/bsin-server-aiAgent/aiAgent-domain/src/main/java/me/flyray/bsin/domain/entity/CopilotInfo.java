package me.flyray.bsin.domain.entity;

import static dev.langchain4j.internal.Utils.getOrDefault;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.io.Serializable;
import java.util.Date;

import lombok.Data;
import me.flyray.bsin.utils.BsinSnowflake;

/**
 * @TableName ai_copilot
 */
@Data
@TableName(value = "ai_copilot")
public class CopilotInfo implements Serializable {

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

  /** 性别 */
  private String sex;

  /** copilot类型：1、品牌官 2、数字分身 3、通用copilot */
  private String type;

  private String llmNo;

  private String promptTemplateNo;

  // 对话总结提示词模版:  带总结聊天信息 -- {{chatBufferWindowList}}
  private String summaryPromptTemplate;

  private String knowledgeBaseNo;

  /** 智能体头像 */
  private String avatar;

  /** 状态： 0：禁用 1:启用 */
  private String status;

  /** 访问权限： 1-private 2-public */
  private String accessAuthority;

  /** 是否为默认商户或者用户copilot */
  private Boolean defaultFlag;

  /** 是否可编辑|删除 */
  private Boolean editable;

  /** 逻辑删除 0、未删除 1、已删除 */
  @TableLogic(value = "0", delval = "1")
  private Integer delFlag;

  /** 创建人 */
  private String createBy;

  /** 模型描述 */
  private String description;

  /** 流式返回 */
  //  @TableField(exist = false)
  private boolean streaming;

  /** 创建时间 */
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
  private Date createTime;

  /** 更新时间 */
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
  private Date updateTime;

  public CopilotInfo() {}

  public CopilotInfo(
      String serialNo,
      String tenantId,
      String merchantNo,
      String customerNo,
      String name,
      String sex,
      String type,
      String avatar,
      String llmNo,
      String promptTemplateNo,
      String summaryPromptTemplate,
      String knowledgeBaseNo,
      String description) {
    CopilotInfo.Builder builder =
        newBuilder()
            .withSerialNo(getOrDefault(serialNo, BsinSnowflake.getId()))
            .withTenantId(getOrDefault(tenantId, "tenantId"))
            .withMerchantNo(getOrDefault(merchantNo, "merchantNo"))
            .withCustomerNo(getOrDefault(customerNo, "customerNo"))
            .withName(getOrDefault(name, "火源兽"))
            .withType(getOrDefault(type, "1"))
            .withSex(getOrDefault(sex, "男"))
            .withAvatar(getOrDefault(avatar, ""))
            .withLlmNo(getOrDefault(llmNo, "127.0.0.1"))
            .withPromptTemplateNo(getOrDefault(promptTemplateNo, "promptTemplateNo"))
            .withSummaryPromptTemplate(
                getOrDefault(
                    summaryPromptTemplate,
                    "请将以下内容逐步概括所提供的对话内容，并将新的概括添加到之前的概括中，形成新的概括。\n\n EXAMPLE\nCurrent summary:\nHuman询问AI对人工智能的看法。AI认为人工智能是一种积极的力量。\nNew lines of conversation:\nHuman：为什么你认为人工智能是一种积极的力量？\nAI：因为人工智能将帮助人类发挥他们的潜能。\n\nNew summary:\nHuman询问AI对人工智能的看法。AI认为人工智能是一种积极的力量，因为它将帮助人类发挥他们的潜能。\nEND OF EXAMPLE\nCurrent summary:\n{summary}\nNew lines of conversation:\n{new_lines}"))
            .withKnowledgeBaseNo(getOrDefault(knowledgeBaseNo, "knowledgeBaseNo"))
            .withDescription(getOrDefault(description, "description"));
    this.serialNo = builder.getSerialNo();
    this.tenantId = builder.getTenantId();
    this.merchantNo = builder.getMerchantNo();
    this.customerNo = builder.getCustomerNo();
    this.name = builder.getName();
    this.type = builder.getType();
    this.avatar = builder.getAvatar();
    this.knowledgeBaseNo = builder.getKnowledgeBaseNo();
    this.description = builder.getDescription();
    this.llmNo = builder.getLlmNo();
    this.promptTemplateNo = builder.getPromptTemplateNo();
    this.summaryPromptTemplate = builder.getSummaryPromptTemplate();
    this.sex = builder.getSex();
  }

  public static CopilotInfo.Builder newBuilder() {
    return new CopilotInfo.Builder();
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
    private String avatar;
    private String llmNo;
    private String promptTemplateNo;
    private String summaryPromptTemplate;
    private String knowledgeBaseNo;
    private String embeddingModelNo;
    private String sex;

    protected Builder() {}

    public CopilotInfo.Builder withSex(String sex) {
      this.sex = sex;
      return this;
    }

    public CopilotInfo.Builder withSerialNo(String serialNo) {
      this.serialNo = serialNo;
      return this;
    }

    public CopilotInfo.Builder withMerchantNo(String merchantNo) {
      this.merchantNo = merchantNo;
      return this;
    }

    public CopilotInfo.Builder withTenantId(String tenantId) {
      this.tenantId = tenantId;
      return this;
    }

    public CopilotInfo.Builder withCustomerNo(String customerNo) {
      this.customerNo = customerNo;
      return this;
    }

    public CopilotInfo.Builder withName(String name) {
      this.name = name;
      return this;
    }

    public CopilotInfo.Builder withAvatar(String avatar) {
      this.avatar = avatar;
      return this;
    }

    public CopilotInfo.Builder withLlmNo(String llmNo) {
      this.llmNo = llmNo;
      return this;
    }

    public CopilotInfo.Builder withEmbeddingModelNo(String embeddingModelNo) {
      this.embeddingModelNo = embeddingModelNo;
      return this;
    }

    public CopilotInfo.Builder withPromptTemplateNo(String promptTemplateNo) {
      this.promptTemplateNo = promptTemplateNo;
      return this;
    }

    public CopilotInfo.Builder withSummaryPromptTemplate(String summaryPromptTemplate) {
      this.summaryPromptTemplate = summaryPromptTemplate;
      return this;
    }

    public CopilotInfo.Builder withKnowledgeBaseNo(String knowledgeBaseNo) {
      this.knowledgeBaseNo = knowledgeBaseNo;
      return this;
    }

    public CopilotInfo.Builder withType(String type) {
      this.type = type;
      return this;
    }

    public CopilotInfo.Builder withDescription(String description) {
      this.description = description;
      return this;
    }

    public CopilotInfo build() {
      return new CopilotInfo(
          serialNo,
          tenantId,
          merchantNo,
          customerNo,
          name,
          sex,
          type,
          avatar,
          llmNo,
          promptTemplateNo,
          summaryPromptTemplate,
          knowledgeBaseNo,
          description);
    }
  }
}
