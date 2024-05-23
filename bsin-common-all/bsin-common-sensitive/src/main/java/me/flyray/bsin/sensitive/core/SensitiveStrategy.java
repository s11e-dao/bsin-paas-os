package me.flyray.bsin.sensitive.core;

import cn.hutool.core.util.DesensitizedUtil;
import lombok.AllArgsConstructor;

import java.util.function.Function;

/**
 * 脱敏策略
 *
 */
@AllArgsConstructor
public enum SensitiveStrategy {

    /**
     * 身份证脱敏
     */
    ID_CARD(s -> DesensitizedUtil.idCardNum(s, 3, 4)),

    /**
     * 手机号脱敏
     */
    PHONE(DesensitizedUtil::mobilePhone),

    /**
     * 地址脱敏
     */
    ADDRESS(s -> DesensitizedUtil.address(s, 8)),

    /**
     * 邮箱脱敏
     */
    EMAIL(DesensitizedUtil::email),

    /**
     * 银行卡
     */
    BANK_CARD(DesensitizedUtil::bankCard),

    /**
     * 密码
     */
    PASSWORD(DesensitizedUtil::password),

    /**
     * 中文名
     */
    CHINESE_NAME(DesensitizedUtil::chineseName)
    ;

    private final Function<String, String> desensitizer;

    public Function<String, String> desensitizer() {
        return desensitizer;
    }
}
