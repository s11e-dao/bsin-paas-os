package me.flyray.bsin.enums;

import com.fasterxml.jackson.annotation.JsonCreator;

/**
 * @author bolei
 * @date 2023/8/19
 * @desc 客户类型 0、个人客户 1、商家客户 2、租户(dao)客户
 */
public enum CustomerType {

    /**
     * 个人客户
     */
    MEMBER("0", "个人客户"),

    /**
     * 商家客户
     */
    MERCHANT("1", "商家客户"),

    /**
     * 租户(dao)客户
     */
    TENANT("2", "租户(dao)客户");

    private String code;

    private String desc;

    CustomerType(String code, String desc) {
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
    public static CustomerType getInstanceById(String id) {
        if (id == null) {
            return null;
        }
        for (CustomerType status : values()) {
            if (id.equals(status.getCode())) {
                return status;
            }
        }
        return null;
    }

}
