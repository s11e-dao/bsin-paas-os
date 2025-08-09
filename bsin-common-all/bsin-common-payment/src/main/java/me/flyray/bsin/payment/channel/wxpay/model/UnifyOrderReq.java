package me.flyray.bsin.payment.channel.wxpay.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Date;
import lombok.Data;
import org.hibernate.validator.constraints.Range;

/**
 * 统一支付请求对象 [[memory:5223135]]
 * 整合所有支付所需的字段，简化接口参数传递
 * 
 * @author leonard
 * @date 2025/01/15
 */
@Data
public class UnifyOrderReq {

    // ============ 基础信息 ============
    /** 租户号 */
    private String tenantId;

    /** 商户号 */
    private String merchantNo;

    /** 应用ID */
    private String bizRoleAppId;

    /** 商户名称 */
    private String merchantName;

    /** 商户支付模式类型 */
    @NotBlank(message = "商户支付模式类型不能为空")
    private String merchantMode;

    /** 店铺ID */
    private String storeNo;

    // ============ 订单信息 ============
    /** 支付订单号 */
    private String payOrderId;

    /** 商户订单号 */
    @NotBlank(message = "商户订单号不能为空")
    private String merchantOrderNo;

    /** 外部订单号（业务方订单号） */
    private String outSerialNo;

    // ============ 支付信息 ============
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

    // ============ 商品信息 ============
    /** 商品标题 */
    @NotBlank(message = "商品标题不能为空")
    private String subject;

    /** 商品描述信息 */
    @NotBlank(message = "商品描述信息不能为空")
    private String body;

    // ============ 用户信息 ============
    /** 客户端IP地址 */
    private String clientIp;

    /** 渠道用户标识,如微信openId,支付宝账号,账户编号,一卡通token */
    private String channelUserId;

    /** 渠道用户名称 */
    private String channelUserName;

    // ============ 通知信息 ============
    /** 异步通知地址 */
    private String notifyUrl;

    /** 跳转通知地址 */
    private String returnUrl;

    // ============ 订单配置 ============
    /** 订单失效时间, 单位：秒 */
    private Integer expiredTime;

    /** 订单失效时间（具体时间） */
    private Date expiredTimeDate;

    /** 特定渠道发起额外参数 */
    private String channelExtra;

    /** 交易是否需要密码 */
    private Boolean needPassword;

    /** 商户扩展参数(返回给商户的参数) */
    private String extParam;

    // ============ 分账信息 ============
    /** 分账模式： 0-该笔订单不允许分账, 1-支付成功按配置自动完成分账, 2-商户手动分账(解冻商户金额) */
    @Range(min = 0, max = 2, message = "分账模式设置值有误")
    private Integer divisionMode;

    /** 标志是否为 subMchAppId的对应 openId， 0-否， 1-是， 默认否 */
    private Integer isSubOpenId;

    // ============ 系统信息 ============
    /** 门店编号 */
    private String storeCode;

    /** 服务商Id */
    private String isvId;

    /** 收款配置类型：1-服务商 2-商户 3-店铺 */
    private String targetConfigType;

    /** 收款对象ID：商户ID、店铺ID或应用ID */
    private String targetId;

    // ============ 费用信息 ============
    /** 商户手续费费率快照 */
    private BigDecimal mchFeeRate;

    /** 商户手续费,单位元 */
    private BigDecimal mchFeeAmount;

    // ============ 状态信息 ============
    /** 支付状态: 0-订单生成, 1-支付中, 2-支付成功, 3-支付失败, 4-已撤销, 5-已退款, 6-订单关闭 */
    private Integer state;

    /** 向下游回调状态, 0-未发送, 1-已发送 */
    private Integer notifyState;

    // ============ 渠道信息 ============
    /** 渠道订单号 */
    private String channelOrderNo;

    /** 渠道商户信息（json） */
    private String channelMchParams;

    /** 支付通道配置编号 */
    private String payChannelConfigNo;

    // ============ 退款信息 ============
    /** 退款状态: 0-未发生实际退款, 1-部分退款, 2-全额退款 */
    private Integer refundState;

    /** 退款次数 */
    private Integer refundTimes;

    /** 退款总金额,单位元 */
    private BigDecimal refundAmount;

    /** 订单分账模式：0-该笔订单不允许分账, 1-支付成功按配置自动完成分账, 2-商户手动分账(解冻商户金额) */
    private String profitSharingMode;

    /** 分账类型： 0-商户分账, 1-商户分账(解冻商户金额) */
    private String profitSharingType;


    /** 分账金额,单位分 */
    private BigDecimal profitSharingAmount;

    /** 0-未发生分账, 1-等待分账任务处理, 2-分账处理中, 3-分账任务已结束(不体现状态) */
    private Integer divisionState;

    /** 最新分账时间 */
    private Date divisionLastTime;

    /** 渠道支付错误码 */
    private String errCode;

    /** 渠道支付错误描述 */
    private String errMsg;

    /** 订单支付成功时间 */
    private Date successTime;

    // ============ 备注信息 ============
    /** 订单备注 */
    private String remark;

    /** 订单创建时间 */
    private Date createTime;

    /** 订单创建人 */
    private String createBy;

    /** 客户ID */
    private String customerNo;
}
