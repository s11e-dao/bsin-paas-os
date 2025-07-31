package me.flyray.bsin.payment.channel.wxpay.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import lombok.Data;
import org.hibernate.validator.constraints.Range;

/*
 * 创建订单请求参数对象
 * 聚合支付接口（统一下单）
 *
 */
@Data
public class UnifiedOrderReq extends AbstractMchAppReq {

  /** 商户订单号 */
  @NotBlank(message = "商户订单号不能为空")
  private String merchantOrderNo;

  /** 支付通道 如： alipay wxPay等 */
  @NotBlank(message = "支付通道不能为空")
  private String payChannel;


  /** 支付方式 如： WX_JSAPI,alipay_wap等 */
  @NotBlank(message = "支付方式不能为空")
  private String payWay;

  /** 支付金额， 单位：元 */
  @NotNull(message = "支付金额不能为空")
  private BigDecimal payAmount;

  /** 货币代码 */
  @NotBlank(message = "货币代码不能为空")
  private String currency;

  /** 客户端IP地址 */
  private String clientIp;

  /** 商品标题 */
  @NotBlank(message = "商品标题不能为空")
  private String subject;

  /** 商品描述信息 */
  @NotBlank(message = "商品描述信息不能为空")
  private String body;

  /** 异步通知地址 */
  private String notifyUrl;

  /** 跳转通知地址 */
  private String returnUrl;

  /** 订单失效时间, 单位：秒 */
  private Integer expiredTime;

  /** 渠道用户标识,如微信openId,支付宝账号,账户编号,一卡通token */
  private String channelUserId;

  /** 渠道用户名称 */
  private String channelUserName;

  /** 特定渠道发起额外参数 */
  private String channelExtra;

  /** 交易是否需要密码 */
  private Boolean needPassword;

  /** 商户扩展参数(返回给商户的参数 */
  private String extParam;

  /** 分账模式： 0-该笔订单不允许分账, 1-支付成功按配置自动完成分账, 2-商户手动分账(解冻商户金额) */
  @Range(min = 0, max = 2, message = "分账模式设置值有误")
  private Integer divisionMode;

  /** 标志是否为 subMchAppId的对应 openId， 0-否， 1-是， 默认否 */
  private Integer isSubOpenId;

  /** 门店编号 */
  private String storeCode;

  /** 服务商Id */
  private String isvId;

  /** 收款配置类型：1-服务商 2-商户 3-店铺 */
//  @NotNull(message = "收款配置类型不可为空")
  private String targetConfigType;

  /** 收款对象ID：商户ID、店铺ID或应用ID */
//  @NotNull(message = "收款对象ID不可为空")
  private String targetId;

}
