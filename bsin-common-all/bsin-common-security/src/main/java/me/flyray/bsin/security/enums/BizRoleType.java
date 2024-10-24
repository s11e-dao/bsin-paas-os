package me.flyray.bsin.security.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
/**
 * @author
 * @description 用户角色类型，1.运营平台 2.租户平台 3.租户商户 4.代理商 5.租户客户 6.门店 99.无
 * @createDate 2024/10/2024/10/13 /23/59
 */
public enum BizRoleType {

    /**
     * 系统运营
     */
    SYS("1", "系统运营"),

    /**
     * 平台租户
     */
    TENANT("2", "平台租户"),

    /**
     * 平台租户
     */
    MERCHANT("3", "租户商户"),

    /**
     * 系统代理商
     */
    SYS_AGENT("4", "系统代理商"),

    /**
     * 客户
     */
    CUSTOMER("5", "租户客户"),

    /**
     * 门店
     */
    STORE("6", "门店"),

    /** 无 */
    NONE_PLATFOR("99", "无");

    private String code;

    private String desc;

    BizRoleType(String code, String desc) {
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
    public static BizRoleType getInstanceById(Integer id) {
        if (id == null) {
            return null;
        }
        for (BizRoleType status : values()) {
            if (id.equals(status.getCode())) {
                return status;
            }
        }
        return null;
    }

}
