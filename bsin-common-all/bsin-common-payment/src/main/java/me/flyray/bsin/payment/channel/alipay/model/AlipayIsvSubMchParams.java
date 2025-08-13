package me.flyray.bsin.payment.channel.alipay.model;

import lombok.Data;
import me.flyray.bsin.payment.channel.wxpay.model.IsvSubMchParams;

/*
 * 支付宝 特约商户参数定义
 *
 */
@Data
public class AlipayIsvSubMchParams extends IsvSubMchParams {

  private String appAuthToken;
}
