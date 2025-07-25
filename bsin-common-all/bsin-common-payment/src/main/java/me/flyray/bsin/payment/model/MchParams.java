package me.flyray.bsin.payment.model;

import lombok.Data;

@Data
public class MchParams {
  /** 应用ID */
  private String appid;

  /** 商户号 */
  private String mchid;

  /** 子商户号 */
  private String subMchid;

  /** 子商户应用ID */
  private String subAppid;
}
