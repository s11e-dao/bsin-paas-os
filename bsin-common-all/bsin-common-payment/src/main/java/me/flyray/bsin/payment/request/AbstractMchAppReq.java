package me.flyray.bsin.payment.request;

import lombok.Data;
import me.flyray.bsin.payment.common.AbstractReq;

/*
 *
 * 通用RQ, 包含mchNo和appId 必填项
 *
 */
@Data
public class AbstractMchAppReq extends AbstractReq {

  /** 租户号 */
  private String tenantId;

  /** 商户号 */
  private String merchantId;

  /** 商户名称 */
  private String merchantName;

  /** 商户类型 */
  private Integer merchantType;

  /** 店铺ID */
  private String storeId;
}
