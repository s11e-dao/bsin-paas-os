package me.flyray.bsin.payment.channel.alipay.model;

import com.alibaba.fastjson2.JSON;
import lombok.Data;
import me.flyray.bsin.payment.channel.wxpay.model.NormalMchParams;
import me.flyray.bsin.utils.StringUtils;

/*
 * 支付宝 普通商户参数定义
 *
 */
@Data
public class AlipayNormalMchParams extends NormalMchParams {

  /** 是否沙箱环境 */
  private Integer sandbox;

  /** appId */
  private String appId;

  /** privateKey */
  private String privateKey;

  /** alipayPublicKey */
  private String alipayPublicKey;

  /** 签名方式 * */
  private String signType;

  /** 是否使用证书方式 * */
  private Integer useCert;

  /** app 证书 * */
  private String appPublicCert;

  /** 支付宝公钥证书（.crt格式） * */
  private String alipayPublicCert;

  /** 支付宝根证书 * */
  private String alipayRootCert;

  @Override
  public String deSenData() {

    AlipayNormalMchParams mchParams = this;
    if (StringUtils.isNotBlank(this.privateKey)) {
      mchParams.setPrivateKey(StringUtils.str2Star(this.privateKey, 4, 4, 6));
    }
    if (StringUtils.isNotBlank(this.alipayPublicKey)) {
      mchParams.setAlipayPublicKey(StringUtils.str2Star(this.alipayPublicKey, 6, 6, 6));
    }
    return JSON.toJSONString(mchParams);
  }
}
