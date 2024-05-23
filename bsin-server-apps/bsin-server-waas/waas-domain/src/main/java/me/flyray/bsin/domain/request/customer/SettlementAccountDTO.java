package me.flyray.bsin.domain.request.customer;

import lombok.Data;
import me.flyray.bsin.domain.entity.customer.SettlementAccount;

@Data
public class SettlementAccountDTO extends SettlementAccount {
    public String uniqueKey;       // 验证码key

    public String validateCode;     // 验证码

    public Integer current;

    public Integer size;
}
