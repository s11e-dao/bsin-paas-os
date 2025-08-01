package me.flyray.bsin.facade.enums;


import com.fasterxml.jackson.annotation.JsonCreator;

/**
 * @author ：bolei
 * @date ：Created in 2022/02/05 16:19
 * @description：租户、机构应用类型 对应 tenant_app org_app中的type
 * @modified By：
 */
public enum TenantOrgAppType {

    /**
     * 默认应用
     */
    DEF_AUTH("0","默认应用"),
    /**
     * 新增授权
     */
    ADD("1", "新增应用"),
    /**
     * 其他授权
     */
    AUTH("2", "其他授权应用");

    private String code;

    private String desc;

    TenantOrgAppType(String code, String desc) {
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
    public static TenantOrgAppType getInstanceById(Integer id) {
        if (id == null) {
            return null;
        }
        for (TenantOrgAppType status : values()) {
            if (id.equals(status.getCode())) {
                return status;
            }
        }
        return null;
    }
}
