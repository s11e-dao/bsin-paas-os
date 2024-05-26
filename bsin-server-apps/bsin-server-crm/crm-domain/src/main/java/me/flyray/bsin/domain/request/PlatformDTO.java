package me.flyray.bsin.domain.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import me.flyray.bsin.domain.domain.Platform;
import me.flyray.bsin.validate.AddGroup;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotBlank;

@Data
public class PlatformDTO extends Platform {
    /**
     * 租户Code
     */
    @NotBlank(message = "租户编码不能为空" ,groups = AddGroup.class)
    public String tenantCode;

    /**
     * 用户名
     */
    public String username;
    /**
     * 登录密码
     */
    @NotBlank(message = "登录密码" ,groups = AddGroup.class)
    public String password;


    public Integer current;

    public Integer size;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    public String startTime;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    public String  endTime;
}
