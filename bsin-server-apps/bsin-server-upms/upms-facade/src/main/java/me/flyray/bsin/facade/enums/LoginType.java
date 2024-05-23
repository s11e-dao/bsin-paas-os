package me.flyray.bsin.facade.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum LoginType {

    COMMON_USER(1, "普通登录"),
    SUPER_ADMIN_USER(2, "管理员登录"),
    ;

    private Integer code;
    private String desc;

    public String getDesc() {
        return desc;
    }

    public Integer getCode() {
        return code;
    }


}
