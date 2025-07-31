package me.flyray.bsin.domain.enums;

import com.fasterxml.jackson.annotation.JsonCreator;

/**
 * 合伙人类别：1、平台代理 2、分销代理
 */
public enum SysAgentLevel {

     /**
     * 省代理
     */
    PROVINCE("PROVINCE", "省代理"),

    /**
     * 市代理
     */
    CITY("CITY", "市代理"),

    /**
     * 县代理
     */
    COUNTY("COUNTY", "县代理"),

    /**
     * 地推人
     */
    GROUND_PROMOTION("GROUND-PROMOTION", "地推人");

    private String code;

    private String desc;

    SysAgentLevel(String code, String desc) {
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
    public static SysAgentLevel getInstanceById(String id) {
        if (id == null) {
            return null;
        }
        for (SysAgentLevel status : values()) {
            if (id.equals(status.getCode())) {
                return status;
            }
        }
        return null;
    }

}
