package me.flyray.bsin.payment.enums;

import com.fasterxml.jackson.annotation.JsonCreator;

/**
 * @author leonard
 * @date 2024/11/06 0:02
 * @desc 支付方式代码
 */
public enum PayWayEnum {

  WX_H5("WX_H5", "微信H5"),
  WX_JSAPI("WX_JSAPI", "微信公众号"),
  WX_LITE("WX_LITE", "微信小程序"),
  WX_NATIVE("WX_NATIVE", "微信扫码"),
  WX_BAR("WX_BAR", "微信条码"),
  WX_APP("WX_APP", "微信APP"),
  WX("WX", "微信支付"),
  ALI("ALI", "支付宝支付"),
  ALI_APP("ALI_APP", "支付宝App"),
  ALI_BAR("ALI_BAR", "支付宝条码"),
  ALI_JSAPI("WX_JSAPI", "支付宝生活号"),
  ALI_LITE("ALI_LITE", "支付宝小程序"),
  ALI_PC("ALI_PC", "支付宝PC网站"),
  ALI_QR("ALI_QR", "支付宝二维码"),
  ALI_WAP("ALI_WAP", "支付宝WAP"),

  XLALILITE("XLALILITE", "信联支付宝支付"),
  YSF_BAR("YSF_BAR", "云闪付条码"),
  YSF_JSAPI("YSF_JSAPI", "云闪付jsapi"),
  YSF_LITE("YSF_LITE", "云闪付小程序"),
  UP_QR("UP_QR", "银联二维码(主扫)"),
  UP_BAR("UP_BAR", "银联二维码(被扫)"),
  UP_APP("UP_APP", "银联App支付"),
  QR_CASHIER("QR_CASHIER", "聚合"),
  QQ_PAY("QQ_PAY", "钱包"),
  PP_PC("PP_PC", "PayPal支付"),
  ICBC_APP("ICBC_APP", "工行APP支付");

  private String code;

  private String desc;

  PayWayEnum(String code, String desc) {
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
  public static PayWayEnum getInstanceById(String id) {
    if (id == null) {
      return null;
    }
    for (PayWayEnum status : values()) {
      if (id.equals(status.getCode())) {
        return status;
      }
    }
    return null;
  }
}
