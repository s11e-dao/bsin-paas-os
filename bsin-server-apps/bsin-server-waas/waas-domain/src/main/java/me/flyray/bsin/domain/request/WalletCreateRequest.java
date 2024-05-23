package me.flyray.bsin.domain.request;

import lombok.Data;
import me.flyray.bsin.domain.entity.Wallet;

import java.util.List;

@Data
public class WalletCreateRequest extends Wallet {

    /**
     * 商户ID
     */
    private String merchantId;

}
