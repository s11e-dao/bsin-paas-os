package me.flyray.bsin.log.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum LoginTypeEnum {

    LOGIN(1,"登录"),
    LOGOUT(2,"登出"),
    ;
    private final int code;

    private final String desc;
}
