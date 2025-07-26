package me.flyray.bsin.payment.channel.wxpay.response;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class OrderCallBackRes implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 支付订单号、转账订单号、退款订单号
     */
    private String tradeNo;


    /**
     * 订单号
     */
    @NotBlank(message = "订单号不能为空")
    private String orderNo;


    /**
     * 金额(支付、转账、退款、分账)
     */
    @NotNull(message = "金额不能为空")
    private BigDecimal amount;


    /**
     *  订单状态
     */
    @NotNull(message = "订单状态不能为空")
    private Integer state;

    /**
     * 商户扩展参数(返回给商户的参数
     **/
    private String extParam;

    /**
     * 渠道返回错误代码
     **/
    private String errCode;

    /**
     * 渠道返回错误信息
     **/
    private String errMsg;
}
