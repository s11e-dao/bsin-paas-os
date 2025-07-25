package me.flyray.bsin.payment.response.payway.wx;

import lombok.Data;
import me.flyray.bsin.payment.enums.PayDataTypeEnum;
import me.flyray.bsin.payment.response.UnifiedOrderRes;

/*
 * 支付方式： WX_APP
 *
 */
@Data
public class WxAppOrderRes extends UnifiedOrderRes {

  /** 预支付数据包 * */
  private String payInfo;

  @Override
  public String buildPayDataType() {
    return PayDataTypeEnum.WX_APP.getCode();
  }

  @Override
  public String buildPayData() {
    return payInfo;
  }
}
