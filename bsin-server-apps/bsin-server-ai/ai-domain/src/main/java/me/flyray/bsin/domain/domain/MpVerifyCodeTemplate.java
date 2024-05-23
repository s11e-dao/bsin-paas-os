package me.flyray.bsin.domain.domain;

import lombok.Builder;
import lombok.Data;

/**
 * @author leonard
 * @description
 * @createDate 2024/01/2024/1/24 /02/00
 */
@Data
@Builder
public class MpVerifyCodeTemplate {
  /*
   * 模版内容：
   * 您好：您当前的验证码为：{{verifyCode.DATA}} 有效期：{{validityTime.DATA}}秒 若非本人操作，则您的账号有风险，请及时修改密码
   * 模版ID:
   * yJEnhCJOxkN8Z1FijiLHUVdvhp52uw-meYteB_479JU
   */
  private String verifyCode;
  private String validityTime;
}
