package me.flyray.bsin.payment.channel.wxpay.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.math.BigDecimal;

/**
 * 统一退款请求对象
 * 参照UnifyOrderReq的设计模式
 * 
 * @author leonard
 * @date 2024-12-19
 */
@Data
public class RefundUnifyReq extends AbstractMchAppReq {

    /**
     * 原商户订单号（对应原始交易的outSerialNo）
     */
//    @NotBlank(message = "原商户订单号不能为空")
    private String outTradeNo;

    /**
     * 商户退款单号（系统生成的退款序列号）
     */
//    @NotBlank(message = "商户退款单号不能为空")
    private String outRefundNo;

    /**
     * 微信订单号（如果有的话优先使用）
     */
    private String transactionId;

    /**
     * 退款金额，单位：元
     */
    @NotNull(message = "退款金额不能为空")
    private BigDecimal refundAmount;

    /**
     * 原订单总金额，单位：元
     */
//    @NotNull(message = "原订单金额不能为空")
    private BigDecimal totalAmount;

    /**
     * 退款原因
     */
    @NotBlank(message = "退款原因不能为空")
    private String refundReason;

    /**
     * 货币类型，默认CNY
     */
    private String currency = "CNY";

    /**
     * 退款结果通知地址
     */
    private String notifyUrl;

    /**
     * 支付渠道（WXPAY、ALIPAY等）
     */
//    @NotBlank(message = "支付渠道不能为空")
    private String payChannel;

    /**
     * 支付方式（WX_LITE、WX_H5、WX_APP等）
     */
    private String payWay;

    /**
     * 商户模式：1-普通商户 2-特约商户
     */
    private String merchantPayMode;

    /**
     * 支付渠道配置编号
     */
    private String payChannelConfigNo;

    /**
     * 业务角色应用ID
     */
    private String bizRoleAppId;

    /**
     * 业务角色类型编号（商户号）
     */
    private String bizRoleTypeNo;

    /**
     * 客户端IP地址
     */
    private String clientIp;

    /**
     * 扩展参数
     */
    private String extParam;

    /**
     * 特定渠道发起额外参数
     */
    private String channelExtra;

    /**
     * 租户ID
     */
    private String tenantId;

    // ===== 以下字段从 RefundOrderReq 合并而来 =====

    /**
     * 商户订单号（兼容老字段）
     */
    private String mchOrderNo;

    /**
     * 支付系统订单号（兼容老字段）
     */
    private String payOrderId;

    /**
     * 商户系统生成的退款单号（兼容老字段）
     */
    private String mchRefundNo;

    /**
     * 收款配置类型：1-服务商 2-商户 3-店铺
     */
    private Integer targetConfigType;

    /**
     * 收款对象ID：商户ID、店铺ID或应用ID
     */
    private String targetId;

    /**
     * 一卡通，操作的收银员id
     */
    private String channelUserId;

    /**
     * 一卡通，操作的收银员id对应的名称
     */
    private String channelUserName;
}