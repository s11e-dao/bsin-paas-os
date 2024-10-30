package me.flyray.bsin.domain.enums;

import com.fasterxml.jackson.annotation.JsonCreator;

/**
 * @author bolei
 * @date 2023/10/18
 * @desc 1 资产类别；2 成员等级；3 账户
 */
public enum ConditionType {

    /**
     * 资产类别
     */
    ASSETS("1","资产类别"),

    /**
     * 成员等级
     */
    MEMBER_GRADE("2","成员等级"),

    /**
     * 账户
     */
    ACCOUNT("3","账户");

    // 状态码
    private String code;
    // 状态信息
    private String desc;

    ConditionType(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public String getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

    /**
     * Json 枚举序列化
     */
    @JsonCreator
    public static ConditionType getInstanceById(String id) {
        if (id == null) {
            return null;
        }
        for (ConditionType status : values()) {
            if (id.equals(status.getCode())) {
                return status;
            }
        }
        return null;
    }

}
