package me.flyray.bsin.dubbo.enums;

import lombok.AllArgsConstructor;

/**
 * 请求日志枚举
 *
 */
@AllArgsConstructor
public enum RequestLogEnum {

    /**
     * info 基础信息 param 参数信息 full 全部
     */
    INFO,
    PARAM,
    FULL,
    PROD,
    ;

}
