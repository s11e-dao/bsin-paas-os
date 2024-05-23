package me.flyray.bsin.enums;

import com.fasterxml.jackson.annotation.JsonCreator;

/**
 * 应用类型的租户
 */
public enum TenantAppType {

    /**
     * jiujiu系统租户
     */
    BSIN_JIUJIU("bsin-jiujiu", "jiujiu系统租户"),

    /**
     * daobook系统租户
     */
    BSIN_DAOBOOK("bsin-daobook", "daobook系统租户"),

    /**
     * bsin-paas默认租户标识类型
     */
    BSIN_PAAS("bsin-paas", "bsinpaas默认租户");

    private String code;

    private String desc;

    TenantAppType(String code, String desc) {
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
    public static TenantAppType getInstanceById(String id) {
        if (id == null) {
            return null;
        }
        for (TenantAppType status : values()) {
            if (id.equals(status.getCode())) {
                return status;
            }
        }
        return null;
    }

}
