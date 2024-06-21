package me.flyray.bsin.domain.response;

import lombok.Data;
import me.flyray.bsin.domain.entity.Transaction;

@Data
public class TransactionVO extends Transaction {
    /**
     * 币种
     */
    public String coin;

    /**
     * 主链
     */
    public String chainName;

}
