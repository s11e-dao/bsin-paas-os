package me.flyray.bsin.domain.request.customer;

import lombok.Data;
import me.flyray.bsin.domain.entity.customer.CustomerApiKey;

@Data
public class CustomerApiKeyDTO extends CustomerApiKey {
    public String uniqueKey;       // 验证码key

    public String validateCode;     // 验证码

    public Integer current;

    public Integer size;
}
