package me.flyray.bsin.facade.enums;


import com.fasterxml.jackson.annotation.JsonCreator;

/**
 * @author ：bolei
 * @date ：Created in 2022/02/05 16:19
 * @description：用户类型  0、超级租户用户 1、普通租户管理员 2、租户商户用户 3、租户代理商用户
 * @modified By：
 */
public enum UserType {

    /**
     * 超级租户用户 super_tenant
     */
    SUPER_TENANT(0,"超级租户用户"),
    /**
     * 普通租户管理员
     */
    TENANT(1, "普通租户管理员"),
    /**
     * 租户商户用户
     */
    MERCHANT(2, "租户商户用户"),

    /**
     * 租户代理商用户
     */
    SYS_AGENT(3, "租户代理商用户");

    private Integer code;

    private String desc;

    UserType(Integer code, String desc) {
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
    public static UserType getInstanceById(Integer id) {
        if (id == null) {
            return null;
        }
        for (UserType status : values()) {
            if (id.equals(status.getCode())) {
                return status;
            }
        }
        return null;
    }
}
