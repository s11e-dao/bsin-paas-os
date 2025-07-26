package me.flyray.bsin.payment.channel.wxpay;

import lombok.Data;
import me.flyray.bsin.payment.enums.PayDataTypeEnum;
import me.flyray.bsin.payment.channel.wxpay.response.UnifiedOrderRes;

/*
 * 支付方式： WX_BAR
 *
 */
@Data
public class WxBarOrderRes extends UnifiedOrderRes {

  @Override
  public String buildPayDataType() {
    return PayDataTypeEnum.NONE.getCode();
  }

  @Override
  public String buildPayData() {
    return "";
  }
}
