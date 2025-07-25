package me.flyray.bsin.payment.common;

import java.io.Serial;
import java.io.Serializable;
import lombok.Data;

/*
* 支付接口抽象Request 参数
*

*/
@Data
public abstract class AbstractReq implements Serializable {

  @Serial private static final long serialVersionUID = 1L;

  /** 版本号 */
  protected String version;

  /** 签名类型 */
  protected String signType;

  /** 签名值 */
  protected String sign;

  /** 接口请求时间 */
  protected String reqTime;
}
