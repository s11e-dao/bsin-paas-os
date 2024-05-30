package me.flyray.bsin.domain.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import me.flyray.bsin.domain.entity.CustomerChainCoin;
import org.springframework.format.annotation.DateTimeFormat;

@Data
public class CustomerChainCoinDTO extends CustomerChainCoin {
    public String uniqueKey;       // 验证码key

    public String validateCode;     // 验证码

    public Integer current;

    public Integer size;

    public String coin;

    public String chainCoinKey;

    public String chainCoinName;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    public String startTime;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    public String  endTime;
}
