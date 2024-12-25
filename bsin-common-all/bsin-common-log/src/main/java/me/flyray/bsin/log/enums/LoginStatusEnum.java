package me.flyray.bsin.log.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum LoginStatusEnum {

    /**
     *
     */
    SUCCESS(1, "成功"),
    FAIL(0, "失败");
    private final Integer status;

    private final String desc;
}
