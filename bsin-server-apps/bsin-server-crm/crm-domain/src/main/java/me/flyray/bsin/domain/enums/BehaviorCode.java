package me.flyray.bsin.domain.enums;

import com.fasterxml.jackson.annotation.JsonCreator;

/**
 * @author bolei
 * @date 2023/8/23
 * @desc
 */
public enum BehaviorCode {

    /**
     * 发资产
     */
    ISSUE("1", "发资产"),
    /**
     * 添加商户
     */
    ADD_MERCHANT("2", "添加商户");

    private String code;

    private String desc;

    BehaviorCode(String code, String desc) {
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
    public static BehaviorCode getInstanceById(String id) {
        if (id == null) {
            return null;
        }
        for (BehaviorCode status : values()) {
            if (id.equals(status.getCode())) {
                return status;
            }
        }
        return null;
    }

}
