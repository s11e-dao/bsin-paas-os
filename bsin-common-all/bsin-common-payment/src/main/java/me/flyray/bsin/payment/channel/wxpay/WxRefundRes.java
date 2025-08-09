package me.flyray.bsin.payment.channel.wxpay;

import lombok.Data;
import me.flyray.bsin.payment.common.AbstractRes;

/**
 * 微信退款响应对象
 * 参照WxLiteOrderRes的设计模式
 * 
 * @author leonard
 * @date 2024-12-19
 */
@Data
public class WxRefundRes extends AbstractRes {

    /**
     * 微信退款单号
     */
    private String refundId;

    /**
     * 商户退款单号
     */
    private String outRefundNo;

    /**
     * 微信订单号
     */
    private String transactionId;

    /**
     * 商户订单号
     */
    private String outTradeNo;

    /**
     * 退款渠道
     */
    private String channel;

    /**
     * 退款入账账户
     */
    private String userReceivedAccount;

    /**
     * 退款成功时间
     */
    private String successTime;

    /**
     * 退款创建时间
     */
    private String createTime;

    /**
     * 退款状态
     * SUCCESS：退款成功
     * CLOSED：退款关闭
     * PROCESSING：退款处理中
     * ABNORMAL：退款异常
     */
    private String status;

    /**
     * 退款资金来源
     */
    private String fundsAccount;

    /**
     * 金额信息
     */
    private Amount amount;

    /**
     * 优惠退款信息
     */
    private String promotionDetail;

    @Data
    public static class Amount {
        /**
         * 订单金额，单位：分
         */
        private Integer total;

        /**
         * 退款金额，单位：分
         */
        private Integer refund;

        /**
         * 用户支付金额，单位：分
         */
        private Integer payerTotal;

        /**
         * 用户退款金额，单位：分
         */
        private Integer payerRefund;

        /**
         * 应结退款金额，单位：分
         */
        private Integer settlementRefund;

        /**
         * 应结订单金额，单位：分
         */
        private Integer settlementTotal;

        /**
         * 优惠退款金额，单位：分
         */
        private Integer discountRefund;

        /**
         * 货币类型
         */
        private String currency;
    }
}