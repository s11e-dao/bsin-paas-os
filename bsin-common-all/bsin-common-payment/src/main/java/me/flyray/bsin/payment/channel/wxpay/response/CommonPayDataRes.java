package me.flyray.bsin.payment.channel.wxpay.response;

import lombok.Data;
import me.flyray.bsin.payment.enums.PayDataTypeEnum;
import org.apache.commons.lang3.StringUtils;

/*
 * 通用支付数据RS
 * 根据set的值，响应不同的payDataType
 *
 */
@Data
public class CommonPayDataRes extends UnifiedOrderRes {

  /** 跳转地址 * */
  private String payUrl;

  /** 二维码地址 * */
  private String codeUrl;

  /** 二维码图片地址 * */
  private String codeImgUrl;

  /** 表单内容 * */
  private String formContent;

  @Override
  public String buildPayDataType() {

    if (StringUtils.isNotEmpty(payUrl)) {
      return PayDataTypeEnum.PAY_URL.getCode();
    }

    if (StringUtils.isNotEmpty(codeUrl)) {
      return PayDataTypeEnum.CODE_URL.getCode();
    }

    if (StringUtils.isNotEmpty(codeImgUrl)) {
      return PayDataTypeEnum.CODE_IMG_URL.getCode();
    }

    if (StringUtils.isNotEmpty(formContent)) {
      return PayDataTypeEnum.FORM.getCode();
    }

    return PayDataTypeEnum.PAY_URL.getCode();
  }

  @Override
  public String buildPayData() {

    if (StringUtils.isNotEmpty(payUrl)) {
      return payUrl;
    }

    if (StringUtils.isNotEmpty(codeUrl)) {
      return codeUrl;
    }

    if (StringUtils.isNotEmpty(codeImgUrl)) {
      return codeImgUrl;
    }

    if (StringUtils.isNotEmpty(formContent)) {
      return formContent;
    }

    return "";
  }
}
