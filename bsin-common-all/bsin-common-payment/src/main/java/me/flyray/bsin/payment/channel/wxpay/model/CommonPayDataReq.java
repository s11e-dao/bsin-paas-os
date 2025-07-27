package me.flyray.bsin.payment.channel.wxpay.model;

import lombok.Data;

/*
* 通用支付数据RQ
*
*/
@Data
public class CommonPayDataReq extends UnifiedOrderReq {

    /** 请求参数： 支付数据包类型 **/
    private String payDataType;

}
