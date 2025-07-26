package me.flyray.bsin.payment.channel.wxpay;

import lombok.Data;
import me.flyray.bsin.payment.enums.PayWayEnum;
import me.flyray.bsin.payment.channel.wxpay.response.UnifiedOrderRes;

/*
 * 支付方式： WX_LITE
 *
 */
@Data
public class WxLiteOrderRes extends UnifiedOrderRes {

  /** 预支付数据包 * */
  private String payInfo;

  @Override
  public String buildPayDataType() {
    return PayWayEnum.WX_LITE.getCode();
  }

  @Override
  public String buildPayData() {
    return payInfo;
  }
}
