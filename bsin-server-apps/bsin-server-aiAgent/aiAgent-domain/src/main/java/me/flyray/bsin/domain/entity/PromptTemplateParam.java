package me.flyray.bsin.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import me.flyray.bsin.utils.BsinSnowflake;

import static dev.langchain4j.internal.Utils.getOrDefault;

/**
 * @TableName ai_prompt_template 参考：https://platform.openai.com/docs/api-reference/chat/create
 * messages=[ {"role": "system", "content": "You are a helpful assistant."}, {"role": "user",
 * "content": "Knock knock."}, {"role": "assistant", "content": "Who's there?"}, {"role": "user",
 * "content": "Orange."}, ],
 */
@Data
@TableName(value = "ai_prompt_template")
public class PromptTemplateParam {

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

  /** 提示词模版类型：0、客户 1、 系统提示词 */
  private String type;

  /** 系统提示词模版： eg. 你是{{systemRole}} */
  private String systemPromptTemplate;

  /** messages中的 system role eg: You are a helpful {{systemRole}}. */
  private String systemRole;

  /** messages中的 system role，只不过位置会被放置在问题前，拥有更强的引导作用。 eg: 你的回答需要满足以下要求: {{determiner}} */
  private String determiner;

  /**
   * messages中的 system role，主要是由【知识库搜索】模块生成，也可以由 HTTP 模块从外部引入。 eg: 你的回答可以参考以下内容: {{knowledgeBase}}
   */
  private String knowledgeBase;

  /**
   * messages中的 system
   * role，为打造永久聊天机器人，将历史聊天记录按照一定机制(超过token进行总结，命令触发总结)总结后存在向量数据库，根据question进行检索获取相关聊天记录。 eg:
   * 以下是你与用户过往的对话记录总结，仅供参考: {{chatHistorySummary}}
   */
  private String chatHistorySummary;

  // 对话总结提示词模版:  带总结聊天信息 -- {{chatBufferWindowList}}
  private String summaryPromptTemplate;

  /**
   * messages中的 system role，需要将最近K条历史聊天记录逐条加入到message中。 eg: { "role": "user", "content":
   * "谁赢得了2018年的FIFA世界杯？" }, { "role": "assistant", "content": "法国赢得了2018年的FIFA世界杯。" }, { "role":
   * "user", "content": "下一届FIFA世界杯什么时候举行？" }
   */
  private String chatBufferWindow;

  /** 用户问题 role为user，加入到最后一条记录 */
  private String question;

  /** 知识库封面图片 */
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

  /** 描述 */
  private String description;

  /** 创建时间 */
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
  private Date createTime;

  /** 更新时间 */
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
  private Date updateTime;

  public PromptTemplateParam() {}

  public PromptTemplateParam(
      String serialNo,
      String tenantId,
      String merchantNo,
      String customerNo,
      String name,
      String type,
      String systemPromptTemplate,
      String systemRole,
      String determiner,
      String knowledgeBase,
      String chatHistorySummary,
      String summaryPromptTemplate,
      String chatBufferWindow,
      String question,
      String description) {
    PromptTemplateParam.Builder builder =
        newBuilder()
            .withSerialNo(getOrDefault(serialNo, BsinSnowflake.getId()))
            .withTenantId(getOrDefault(tenantId, "tenantId"))
            .withMerchantNo(getOrDefault(merchantNo, "merchantNo"))
            .withCustomerNo(getOrDefault(customerNo, "customerNo"))
            .withName(getOrDefault(name, "知识库问答提示词模板"))
            .withType(getOrDefault(type, "1"))
            .withSystemRole(getOrDefault(systemRole, "火源社区AI机器人，名字叫火源兽"))
            .withSystemPromptTemplate(getOrDefault(systemPromptTemplate, "你是{{systemRole}}"))
            .withDeterminer(getOrDefault(determiner, "，使用提问的语言进行回答\n"))
            .withKnowledgeBase(
                getOrDefault(knowledgeBase, "你的回答尽可能参考下面的信息:\n" + "{{knowledgeBase}}\n"))
            .withChatHistorySummary(
                getOrDefault(
                    chatHistorySummary,
                    "以下是根据用户最新问题查找的你与用户过往的对话记录总结，仅供参考: \n" + "{{chatHistorySummary}}\n"))
            .withSummaryPromptTemplate(
                getOrDefault(
                    summaryPromptTemplate,
                    "请将以下内容逐步概括所提供的对话内容，并将新的概括添加到之前的概括中，形成新的概括。\n\n EXAMPLE\nCurrent summary:\nHuman询问AI对人工智能的看法。AI认为人工智能是一种积极的力量。\nNew lines of conversation:\nHuman：为什么你认为人工智能是一种积极的力量？\nAI：因为人工智能将帮助人类发挥他们的潜能。\n\nNew summary:\nHuman询问AI对人工智能的看法。AI认为人工智能是一种积极的力量，因为它将帮助人类发挥他们的潜能。\nEND OF EXAMPLE\nCurrent summary:\n{summary}\nNew lines of conversation:\n{new_lines}"))
            .withChatBufferWindow(getOrDefault(chatBufferWindow, " 如有必要，可以参考你们的聊天记录信息\n"))
            .withQuestion(getOrDefault(question, "{{question}}\n"))
            .withDescription(getOrDefault(description, "description"));
    this.serialNo = builder.getSerialNo();
    this.tenantId = builder.getTenantId();
    this.merchantNo = builder.getMerchantNo();
    this.customerNo = builder.getCustomerNo();
    this.name = builder.getName();
    this.type = builder.getType();
    this.systemRole = builder.getSystemRole();
    this.summaryPromptTemplate = builder.getSummaryPromptTemplate();
    this.systemPromptTemplate = builder.getSystemPromptTemplate();
    this.determiner = builder.getDeterminer();
    this.chatBufferWindow = builder.getChatBufferWindow();
    this.chatHistorySummary = builder.getChatHistorySummary();
    this.knowledgeBase = builder.getKnowledgeBase();
    this.question = builder.getQuestion();
    this.description = builder.getDescription();
  }

  public static PromptTemplateParam.Builder newBuilder() {
    return new PromptTemplateParam.Builder();
  }

  @Data
  public static class Builder {
    private String serialNo;
    private String tenantId;
    private String merchantNo;
    private String customerNo;
    private String name;
    private String type;
    private String systemRole;
    private String systemPromptTemplate;
    private String determiner;
    private String knowledgeBase;
    private String chatHistorySummary;
    private String summaryPromptTemplate;
    private String chatBufferWindow;
    private String question;
    private String description;

    protected Builder() {}

    public PromptTemplateParam.Builder withSerialNo(String serialNo) {
      this.serialNo = serialNo;
      return this;
    }

    public PromptTemplateParam.Builder withMerchantNo(String merchantNo) {
      this.merchantNo = merchantNo;
      return this;
    }

    public PromptTemplateParam.Builder withTenantId(String tenantId) {
      this.tenantId = tenantId;
      return this;
    }

    public PromptTemplateParam.Builder withCustomerNo(String customerNo) {
      this.customerNo = customerNo;
      return this;
    }

    public PromptTemplateParam.Builder withName(String name) {
      this.name = name;
      return this;
    }

    public PromptTemplateParam.Builder withType(String type) {
      this.type = type;
      return this;
    }

    public PromptTemplateParam.Builder withDescription(String description) {
      this.description = description;
      return this;
    }

    public PromptTemplateParam.Builder withSystemRole(String systemRole) {
      this.systemRole = systemRole;
      return this;
    }

    public PromptTemplateParam.Builder withSystemPromptTemplate(String systemPromptTemplate) {
      this.systemPromptTemplate = systemPromptTemplate;
      return this;
    }

    public PromptTemplateParam.Builder withDeterminer(String determiner) {
      this.determiner = determiner;
      return this;
    }

    public PromptTemplateParam.Builder withKnowledgeBase(String knowledgeBase) {
      this.knowledgeBase = knowledgeBase;
      return this;
    }

    public PromptTemplateParam.Builder withChatHistorySummary(String chatHistorySummary) {
      this.chatHistorySummary = chatHistorySummary;
      return this;
    }

    public PromptTemplateParam.Builder withSummaryPromptTemplate(String summaryPromptTemplaSte) {
      this.summaryPromptTemplate = summaryPromptTemplate;
      return this;
    }

    public PromptTemplateParam.Builder withChatBufferWindow(String chatBufferWindow) {
      this.chatBufferWindow = chatBufferWindow;
      return this;
    }

    public PromptTemplateParam.Builder withQuestion(String question) {
      this.question = question;
      return this;
    }

    public PromptTemplateParam build() {
      return new PromptTemplateParam(
          serialNo,
          tenantId,
          merchantNo,
          customerNo,
          name,
          type,
          systemPromptTemplate,
          systemRole,
          determiner,
          knowledgeBase,
          chatHistorySummary,
          summaryPromptTemplate,
          chatBufferWindow,
          question,
          description);
    }
  }
}
