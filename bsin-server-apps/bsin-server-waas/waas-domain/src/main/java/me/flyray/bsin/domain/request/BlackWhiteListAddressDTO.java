package me.flyray.bsin.domain.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import me.flyray.bsin.domain.entity.BlackWhiteListAddress;
import me.flyray.bsin.mybatis.utils.Pagination;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotNull;

@Data
public class BlackWhiteListAddressDTO extends BlackWhiteListAddress {
    public String uniqueKey;       // 验证码key

    public String validateCode;     // 验证码

    /**
     * 币种
     */
    private String coin;
    /**
     * 链名
     */
    private String chainName;

    @NotNull(message = "分页不能为空！")
    private Pagination pagination;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    public String startTime;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    public String  endTime;
}
