package me.flyray.bsin.payment.enums;

import com.fasterxml.jackson.annotation.JsonCreator;

/**
 * @author leonard
 * @date 2024/11/06 0:02
 * @desc 支付方式代码
 */
public enum PayDataTypeEnum {
  PAY_URL("payurl", "跳转链接的方式:redirectUrl"),
  FORM("form", "表单提交"),
  WX_APP("wxapp", "微信app参数"),
  ALI_APP("aliapp", "支付宝app参数"),
  YSF_APP("ysfapp", "云闪付app参数"),
  CODE_URL("codeUrl", "二维码URL"),
  CODE_IMG_URL("codeImgUrl", "二维码图片显示URL"),
  NONE("none", "无参数"),
  QR_CONTENT("qrContent", "二维码实际内容"),
  ;
  private String code;

  private String desc;

  PayDataTypeEnum(String code, String desc) {
    this.code = code;
    this.desc = desc;
  }

  public String getCode() {
    return code;
  }

  public String getDesc() {
    return desc;
  }

  /** Json 枚举序列化 */
  @JsonCreator
  public static PayDataTypeEnum getInstanceById(String id) {
    if (id == null) {
      return null;
    }
    for (PayDataTypeEnum status : values()) {
      if (id.equals(status.getCode())) {
        return status;
      }
    }
    return null;
  }
}
