package me.flyray.bsin.payment.model;

import lombok.Data;

/*
 * 微信官方支付 配置参数
 *
 */
@Data
public class WxPayIsvSubMchParams extends IsvSubMchParams {

  /** 子商户ID * */
  private String subMchId;

  /** 子账户appID * */
  private String subMchAppId;
}
