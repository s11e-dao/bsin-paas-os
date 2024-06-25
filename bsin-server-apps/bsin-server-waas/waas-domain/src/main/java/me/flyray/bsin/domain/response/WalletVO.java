package me.flyray.bsin.domain.response;

import lombok.Data;
import me.flyray.bsin.domain.entity.Wallet;
import me.flyray.bsin.domain.entity.WalletAccount;

import java.util.List;

@Data
public class WalletVO extends Wallet {

    public String customerNo;

    public String platformName;

    public List<WalletAccount> walletAccounts;

}
