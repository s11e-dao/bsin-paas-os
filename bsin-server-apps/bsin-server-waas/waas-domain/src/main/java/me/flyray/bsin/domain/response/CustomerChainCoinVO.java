package me.flyray.bsin.domain.response;

import lombok.Data;
import me.flyray.bsin.domain.entity.CustomerChainCoin;

@Data
public class CustomerChainCoinVO extends CustomerChainCoin {
    // 链上货币key
    private String chainCoinKey;
    // 币种名称
    private String chainCoinName;
    // 币种简称
    private String shortName;
    // 币种
    private String coin;
    // 链名
    private String chainName;
}
