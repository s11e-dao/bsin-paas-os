package me.flyray.bsin.domain.response;

import lombok.Data;
import me.flyray.bsin.domain.entity.BlackWhiteListAddress;

@Data
public class BlackWhiteListAddressVO extends BlackWhiteListAddress {
    /**
     * 币种key
     */
    private String chainCoinKey;
    /**
     * 币种名称
     */
    private String chainCoinName;
    /**
     * 币种简称
     */
    private String shortName;
    /**
     * 币种
     */
    private String coin;
    /**
     * 链名
     */
    private String chainName;
    /**
     * 商户名称
     */
    private String merchantName;

}
