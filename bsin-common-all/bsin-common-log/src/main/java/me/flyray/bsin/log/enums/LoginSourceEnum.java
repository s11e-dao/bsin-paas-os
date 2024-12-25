package me.flyray.bsin.log.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum LoginSourceEnum {



    PC("pc","pc"),
    H5("h5","h5"),
    WXAPP("wxapp","微信小程序"),
    ;
    private final String source;



    private final String desc;
}
