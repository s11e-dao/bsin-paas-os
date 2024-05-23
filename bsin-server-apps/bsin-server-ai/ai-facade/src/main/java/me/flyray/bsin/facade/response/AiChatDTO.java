package me.flyray.bsin.facade.response;

import lombok.Data;
import me.flyray.bsin.utils.BsinSnowflake;

import static dev.langchain4j.internal.Utils.getOrDefault;

/**
 * @author leonard
 * @description
 * @createDate 2023/12/2023/12/28 /14/13
 */
@Data
public class AiChatDTO {

  /** chat ID */
  private String serialNo;

  /** 租户ID */
  private String tenantId;

  /** 商户ID */
  private String merchantNo;

  /** 客户ID */
  private String fromNo;

  /** copilot ID */
  private String toNo;

  /** 对话类型： 1： 品牌官 2： 个人数字分身 3： 知识库问答 */
  private String type;

  private String question;

  private String answer;

  public AiChatDTO() {}

  public AiChatDTO(
      String serialNo,
      String tenantId,
      String merchantNo,
      String fromNo,
      String type,
      String toNo,
      String question,
      String answer) {
    Builder builder =
        newBuilder()
            .withSerialNo(getOrDefault(serialNo, BsinSnowflake.getId()))
            .withTenantId(getOrDefault(tenantId, ""))
            .withMerchantNo(getOrDefault(merchantNo, ""))
            .withFromNo(getOrDefault(fromNo, ""))
            .withType(getOrDefault(type, "1"))
            .withToNo(getOrDefault(toNo, ""))
            .withQuestion(getOrDefault(question, ""))
            .withAnswer(getOrDefault(answer, ""));
    this.serialNo = builder.getSerialNo();
    this.tenantId = builder.getTenantId();
    this.merchantNo = builder.getMerchantNo();
    this.fromNo = builder.getFromNo();
    this.type = builder.getType();
    this.toNo = builder.getToNo();
    this.question = builder.getQuestion();
    this.answer = builder.getAnswer();
  }

  public static Builder newBuilder() {
    return new Builder();
  }

  @Data
  public static class Builder {
    private String serialNo;
    private String tenantId;
    private String merchantNo;
    private String fromNo;
    private String type;
    private String toNo;
    private String question;
    private String answer;

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

    public Builder withFromNo(String fromNo) {
      this.fromNo = fromNo;
      return this;
    }

    public Builder withType(String type) {
      this.type = type;
      return this;
    }

    public Builder withToNo(String toNo) {
      this.toNo = toNo;
      return this;
    }

    public Builder withQuestion(String question) {
      this.question = question;
      return this;
    }

    public Builder withAnswer(String answer) {
      this.answer = answer;
      return this;
    }

    public AiChatDTO build() {
      return new AiChatDTO(serialNo, tenantId, merchantNo, fromNo, type, toNo, question, answer);
    }
  }
}
