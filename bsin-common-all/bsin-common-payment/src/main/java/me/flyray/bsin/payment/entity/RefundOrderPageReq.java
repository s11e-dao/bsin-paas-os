package me.flyray.bsin.payment.entity;

import java.io.Serializable;
import lombok.Data;

@Data
public class RefundOrderPageReq implements Serializable {

  /** 租户 */
  private String tenantId;

  /** 分页信息 */
//  private BsinResultEntity.Pagination pagination;

  /** 商户号 */
  private String merchantId;

  /** 店铺ID */
  private String storeId;
}
