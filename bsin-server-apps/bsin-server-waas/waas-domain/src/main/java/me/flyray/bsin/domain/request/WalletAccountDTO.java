package me.flyray.bsin.domain.request;

import lombok.Data;
import me.flyray.bsin.domain.entity.WalletAccount;

@Data
public class WalletAccountDTO extends WalletAccount {

    public Integer current;

    public Integer size;
}
