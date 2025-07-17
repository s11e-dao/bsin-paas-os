package me.flyray.bsin.domain.enums;

import com.fasterxml.jackson.annotation.JsonCreator;

/**
 * 代理商类别：1、平台代理 2、分销代理
 */
public enum SysAgentCategory {

    /**
     * 租户平台代理
     */
    TENANT("1", "平台代理"),

    /**
     * 分销用户代理: 需要加入分销团队
     */
    DIS_AGENT("2", "分销代理");

    private String code;

    private String desc;

    SysAgentCategory(String code, String desc) {
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
    public static SysAgentCategory getInstanceById(String id) {
        if (id == null) {
            return null;
        }
        for (SysAgentCategory status : values()) {
            if (id.equals(status.getCode())) {
                return status;
            }
        }
        return null;
    }

}
