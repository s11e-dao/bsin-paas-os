package me.flyray.bsin.payment.channel.wxpay.model;

import lombok.Data;
import me.flyray.bsin.payment.common.AbstractReq;

/*
 *
 * 支付通用RQ, 包含mchNo和appId 必填项
 *
 */
@Data
public class AbstractMchAppReq extends AbstractReq {

  /** 租户号 */
  private String tenantId;

  /** 商户号 */
  private String merchantNo;
  
  /**
   * 应用ID
   */
  private String bizRoleAppId;

  /** 商户名称 */
  private String merchantName;

  /**
   * 商户支付模式类型
   *
   * @see me.flyray.bsin.payment.enums.PayMerchantModeEnum
   */
  private String merchantMode;

  /** 店铺ID */
  private String storeNo;

  /**
   * 支付渠道编号
   */
  private String payChannel;

}
