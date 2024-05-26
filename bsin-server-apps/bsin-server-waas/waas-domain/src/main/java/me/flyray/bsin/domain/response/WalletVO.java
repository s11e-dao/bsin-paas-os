package me.flyray.bsin.domain.response;

import lombok.Data;
import me.flyray.bsin.domain.domain.Wallet;

@Data
public class WalletVO extends Wallet {

    public String customerNo;

    public String platformName;
}
