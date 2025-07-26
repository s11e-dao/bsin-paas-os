package me.flyray.bsin.payment.channel.wxpay.response;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class PayChannelRes implements Serializable {

    private static final long serialVersionUID = 1L;

    private String payChannel;

    private String payChannelName;

    private String payWayCode;

    private String payWayName;
}
