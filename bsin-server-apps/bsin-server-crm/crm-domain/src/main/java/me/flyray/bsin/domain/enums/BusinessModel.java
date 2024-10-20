package me.flyray.bsin.domain.enums;

import com.fasterxml.jackson.annotation.JsonCreator;

/**
 * @author bolei
 * @date 2023/8/22
 *
 * @desc 经营模式  1、直营，2、加盟
 */
public enum BusinessModel {

    /**
     * 总店
     */
    FRANCHISE("1", "直营"),

    /**
     * 非总店
     */
    DIRECT_SALE("2", "加盟");


    private String code;

    private String desc;

    BusinessModel(String code, String desc) {
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
    public static BusinessModel getInstanceById(String id) {
        if (id == null) {
            return null;
        }
        for (BusinessModel status : values()) {
            if (id.equals(status.getCode())) {
                return status;
            }
        }
        return null;
    }

}
