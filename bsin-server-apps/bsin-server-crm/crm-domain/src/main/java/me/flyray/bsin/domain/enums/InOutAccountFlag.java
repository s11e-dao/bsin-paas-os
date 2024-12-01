package me.flyray.bsin.domain.enums;

import com.fasterxml.jackson.annotation.JsonCreator;

/**
 * 出入账标识
 * 0、出账 1、入账
 */
public enum InOutAccountFlag {
    /**
     * 出账
     */
    OUT_ACCOUNT(0, "出账"),
    /**
     * 入账
     */
    INT_ACCOUNT(1, "入账");

    private Integer code;

    private String desc;

    InOutAccountFlag(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public Integer getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

    /**
     * Json 枚举序列化
     */
    @JsonCreator
    public static InOutAccountFlag getInstanceById(String id) {
        if (id == null) {
            return null;
        }
        for (InOutAccountFlag status : values()) {
            if (id.equals(status.getCode())) {
                return status;
            }
        }
        return null;
    }
}
