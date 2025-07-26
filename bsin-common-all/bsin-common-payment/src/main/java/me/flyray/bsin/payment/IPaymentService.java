package me.flyray.bsin.payment;

import me.flyray.bsin.payment.common.AbstractRes;
import me.flyray.bsin.payment.channel.wxpay.model.AbstractPayOrder;
import me.flyray.bsin.payment.channel.wxpay.model.UnifiedOrderReq;

/*
 * 调起上游渠道侧支付接口
 *
 */
public interface IPaymentService {

  /** 获取到接口code */
  String getIfCode();

  /** 调起支付接口，并响应数据； 内部处理普通商户和服务商模式 */
  AbstractRes pay(UnifiedOrderReq bizRQ, AbstractPayOrder abstractPayOrder) throws Exception;

}
