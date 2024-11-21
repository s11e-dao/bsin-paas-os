package me.flyray.bsin.domain.enums;

import com.fasterxml.jackson.annotation.JsonCreator;

/**
 * 分销模型
 * 模型类型:一级分销: level1, 二级分销: level2, 链动2+1: level2_1
 */
public enum DisModelEnum {

    /**
     * Distributor（分销员）
     */
    DIS_LEVEL1("level1", "一级分销"),
    /**
     * 二级分销
     */
    DIS_LEVEL2("level2", "二级分销"),

    /**
     * 链动2+1
     */
    DIS_LEVEL21("level2_1", "链动2+1"),

    ;

    private String code;

    private String desc;

    DisModelEnum(String code, String desc) {
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
    public static DisModelEnum getInstanceById(String id) {
        if (id == null) {
            return null;
        }
        for (DisModelEnum status : values()) {
            if (id.equals(status.getCode())) {
                return status;
            }
        }
        return null;
    }
}
