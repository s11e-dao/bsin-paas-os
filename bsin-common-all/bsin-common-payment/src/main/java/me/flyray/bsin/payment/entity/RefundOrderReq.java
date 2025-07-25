package me.flyray.bsin.payment.entity;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import lombok.Data;

/*
 * 创建退款订单请求参数对象
 *
 */
@Data
public class RefundOrderReq extends AbstractMchAppReq {

    /**
     * 商户订单号
     **/
    private String mchOrderNo;

    /**
     * 支付系统订单号
     **/
    private String payOrderId;

    /**
     * 商户系统生成的退款单号
     **/
    @NotBlank(message = "商户退款单号不能为空")
    private String mchRefundNo;

    /**
     * 退款金额， 单位：分
     **/
    @NotNull(message = "退款金额不能为空")
    private BigDecimal refundAmount;

    /**
     * 货币代码
     **/
    @NotBlank(message = "货币代码不能为空")
    private String currency;

    /**
     * 退款原因
     **/
    @NotBlank(message = "退款原因不能为空")
    private String refundReason;

    /**
     * 客户端IP地址
     **/
    private String clientIp;

    /**
     * 异步通知地址
     **/
    private String notifyUrl;

    /**
     * 特定渠道发起额外参数
     **/
    private String channelExtra;

    /**
     * 商户扩展参数
     **/
    private String extParam;

    /**
     * 收款配置类型 1-服务商 2-商户 3-店铺
     */
    private Integer infoType;

    /**
     * 商户、店铺、应用id
     */
    private String infoId;

    /**
     * 一卡通 ，操作的收银员id
     */
    private String channelUserId;

    /**
     * 一卡通 ，操作的收银员id对应的名称
     */
    private String channelUserName;
}
