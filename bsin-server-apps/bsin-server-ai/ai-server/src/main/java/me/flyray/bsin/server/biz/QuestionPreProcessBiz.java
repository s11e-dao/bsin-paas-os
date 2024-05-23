package me.flyray.bsin.server.biz;

import me.flyray.bsin.facade.response.QuestionPreProcessDTO;
import me.flyray.bsin.thirdauth.wx.builder.TextBuilder;
import me.flyray.bsin.wordFilter.SensitiveWordFilter;

import java.util.Map;

/**
 * @author leonard
 * @description : 所有 chat Question 的预处理，敏感词检测，命令解析...
 * @createDate 2024/01/2024/1/24 /15/36
 */
public class QuestionPreProcessBiz {

  /**
   * 是否是公众号验证码请求
   *
   * @return
   */
  public static QuestionPreProcessDTO preProcess(String question) {
    QuestionPreProcessDTO questionPreProcessDTO = new QuestionPreProcessDTO();
    // 1.敏感词过滤
    Map<String, Object> ret = SensitiveWordFilter.Filter(question);
    questionPreProcessDTO.setSensitiveWord((boolean) ret.get("isContain"));
    questionPreProcessDTO.setSensitiveWordList((String) ret.get("text"));

    // 2.公众号验证码--用户名
    questionPreProcessDTO.setMpVerifyCodeUsername(mpVerifyCodeQuery(question));

    // 2.公众号验证码--
    questionPreProcessDTO.setGetMpVerifyCode(question.equals("mpVerifyCode"));

    // 3.获取微信登录二维码
    questionPreProcessDTO.setGetWechatLoginQqCode(question.equals("wechatLogin"));

    // 3.token计费

    return questionPreProcessDTO;
  }

  /**
   * 是否是公众号验证码请求
   *
   * @return
   */
  public static String mpVerifyCodeQuery(String sourceText) {
    if (sourceText == null) {
      return null;
    } else if (sourceText.length() < 3) {
      return null;
    } else {
      String lastTwoChars = sourceText.substring(sourceText.length() - 2);
      if (lastTwoChars.equals("##")) {
        return sourceText.substring(0, sourceText.length() - 2);
      } else {
        return null;
      }
    }
  }

  /**
   * 是否是公众号验证码请求
   *
   * @return
   */
  public static boolean isGetWechatLoginQrCode(String sourceText) {
    if (sourceText == null) {
      return false;
    } else if (sourceText.length() < 3) {
      return false;
    } else {
      String lastTwoChars = sourceText.substring(sourceText.length() - 2);
      if (lastTwoChars.equals("**")) {

        if (sourceText.substring(0, sourceText.length() - 2).equals("wechat")) {
          return true;
        }
      } else {
        return false;
      }
    }
    return false;
  }
}
