package me.flyray.bsin.domain.enums;

import com.fasterxml.jackson.annotation.JsonCreator;

/**
 * 分销商类型 1(老板), 0(分销员)
 */
public enum DisAgentType {

    /**
     * Distributor（分销员）
     */
    DISTRIBUTOR("0", "分销员"),
    /**
     * 老板
     */
    BOSS("1", "老板");

    private String code;

    private String desc;

    DisAgentType(String code, String desc) {
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
    public static DisAgentType getInstanceById(String id) {
        if (id == null) {
            return null;
        }
        for (DisAgentType status : values()) {
            if (id.equals(status.getCode())) {
                return status;
            }
        }
        return null;
    }

}
