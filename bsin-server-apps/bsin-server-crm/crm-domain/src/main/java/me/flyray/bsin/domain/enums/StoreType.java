package me.flyray.bsin.domain.enums;

import com.fasterxml.jackson.annotation.JsonCreator;

/**
 * @author bolei
 * @date 2023/8/22
 *
 * @desc 类型：0：总店 1：非总店
 */
public enum StoreType {

    /**
     * 非总店
     */
    OTHER("0", "非总店"),

    /**
     * 总店
     */
    MAIN_STORE("1", "总店");

    private String code;

    private String desc;

    StoreType(String code, String desc) {
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
    public static StoreType getInstanceById(String id) {
        if (id == null) {
            return null;
        }
        for (StoreType status : values()) {
            if (id.equals(status.getCode())) {
                return status;
            }
        }
        return null;
    }

}
