package me.flyray.bsin.payment.channel.wxpay.model;

import java.io.Serializable;
import lombok.Data;

@Data
public class RefundOrderPageReq implements Serializable {

  /** 租户 */
  private String tenantId;

  /** 分页信息 */
//  private BsinResultEntity.Pagination pagination;

  /** 商户号 */
  private String merchantNo;

  /** 店铺ID */
  private String storeNo;
}
