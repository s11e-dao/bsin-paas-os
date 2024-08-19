package me.flyray.bsin.facade.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author HLW
 **/
@Getter
@AllArgsConstructor
public enum UserStatusEnum {


    ON(0, "在职"),
    OFF(1, "离职");
    ;
    private final Integer status;

    private final String desc;

}
