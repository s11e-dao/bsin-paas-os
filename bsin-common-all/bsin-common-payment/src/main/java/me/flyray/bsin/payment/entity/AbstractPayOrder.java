package me.flyray.bsin.payment.entity;

import com.baomidou.mybatisplus.annotation.*;
import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import lombok.Data;
import me.flyray.bsin.payment.enums.PayMerchantModeEnum;

/** 支付订单表 */
@Data
public class AbstractPayOrder implements Serializable {

  @Serial private static final long serialVersionUID = 1L;

  /** 租户 */
  private String tenantId;

  /** 支付订单号 */
  private String payOrderId;

  /** 商户号 */
  private String merchantId;

  /** 店铺ID */
  private String storeId;

  /** 商户名称 */
  private String merchantName;

  /**
   * 支付商戶模式:
   *
   * @see PayMerchantModeEnum
   */
  private String merchantMode;

  /** 商户订单号 */
  private String merchantOrderNo;

  /** 支付接口代码 */
  private String ifCode;

  /** 支付方式代码 */
  private String wayCode;

  /** 支付金额,单位元 */
  private BigDecimal amount;

  /** 商户手续费费率快照 */
  private BigDecimal mchFeeRate;

  /** 商户手续费,单位元 */
  private BigDecimal mchFeeAmount;

  /** 三位货币代码,人民币:cny */
  private String currency;

  /** 支付状态: 0-订单生成, 1-支付中, 2-支付成功, 3-支付失败, 4-已撤销, 5-已退款, 6-订单关闭 */
  private Integer state;

  /** 向下游回调状态, 0-未发送, 1-已发送 */
  private Integer notifyState;

  /** 客户端IP */
  private String clientIp;

  /** 商品标题 */
  private String subject;

  /** 商品描述信息 */
  private String body;

  /** 特定渠道发起额外参数 */
  private String channelExtra;

  /** 渠道用户标识,如微信openId,支付宝账号 */
  private String channelUser;

  /** 渠道订单号 */
  private String channelOrderNo;

  /** 退款状态: 0-未发生实际退款, 1-部分退款, 2-全额退款 */
  private Integer refundState;

  /** 退款次数 */
  private Integer refundTimes;

  /** 退款总金额,单位元 */
  private BigDecimal refundAmount;

  /** 订单分账模式：0-该笔订单不允许分账, 1-支付成功按配置自动完成分账, 2-商户手动分账(解冻商户金额) */
  private Integer profitSharingMode;

  /** 0-未发生分账, 1-等待分账任务处理, 2-分账处理中, 3-分账任务已结束(不体现状态) */
  private Integer divisionState;

  /** 最新分账时间 */
  private Date divisionLastTime;

  /** 渠道支付错误码 */
  private String errCode;

  /** 渠道支付错误描述 */
  private String errMsg;

  /** 商户扩展参数 */
  private String extParam;

  /** 异步通知地址 */
  private String notifyUrl;

  /** 页面跳转地址 */
  private String returnUrl;

  /** 订单失效时间 */
  private Date expiredTime;

  /** 订单支付成功时间 */
  private Date successTime;

  /** 微信服务商ID */
  private String isvId;

  /** 收款配置类型 1-服务商 2-商户 3-店铺 4-应用 */
  private Integer infoType;

  /** 服务商号（服务商支付参数）或应用AppId（商户支付参数） */
  private String infoId;

  /** 渠道商户信息（json） */
  private String channelMchParams;
}
