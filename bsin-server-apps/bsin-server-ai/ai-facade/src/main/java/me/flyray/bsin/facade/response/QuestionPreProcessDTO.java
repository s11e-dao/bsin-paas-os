package me.flyray.bsin.facade.response;

import static dev.langchain4j.internal.Utils.getOrDefault;

import lombok.Builder;
import lombok.Data;
import me.flyray.bsin.utils.BsinSnowflake;

/**
 * @author leonard
 * @description
 * @createDate 2023/12/2023/12/28 /14/13
 */
@Data
// @Builder
public class QuestionPreProcessDTO {

  /** 是否包含敏感词 */
  private boolean isSensitiveWord;
  /** 是否包含敏感词 */
  private String sensitiveWordList;
  /** 是否公众号验证码 */
  private boolean getMpVerifyCode;
  /** 验证用户名 */
  private String mpVerifyCodeUsername;
  /** 获取微信登录二维码 */
  private boolean isGetWechatLoginQqCode;
  /** 处理前的问题 */
  private String preQuestion;
  /** 处理后的问题 */
  private String processedQuestion;

  //  public QuestionPreProcessDTO() {}
}
