package me.flyray.bsin.enums;

import com.fasterxml.jackson.annotation.JsonCreator;

/**
 * s11edao 合约访问控制权限
 */
public enum S11eDaoContractAcl {

    /**
     * 1、REPLACE_WRAPPER
     */
    REPLACE_WRAPPER("REPLACE_WRAPPER", 0, "替换更新合约wrapper的权限"),

    /**
     * 2、SUBMIT_PROPOSAL
     */
    SUBMIT_PROPOSAL("SUBMIT_PROPOSAL", 1, "提交提案的权限"),

    /**
     * 3、UPDATE_DELEGATE_KEY
     */
    UPDATE_DELEGATE_KEY("UPDATE_DELEGATE_KEY", 3, "更新代表钥匙的权限"),

    /**
     * 4、SET_CONFIGURATION
     */
    SET_CONFIGURATION("SET_CONFIGURATION", 4, "操作配置的权限"),

    /**
     * 5、ADD_EXTENSION
     */
    ADD_EXTENSION("ADD_EXTENSION", 5, "添加扩展合约的权限"),

    /**
     * 6、REMOVE_EXTENSION
     */
    REMOVE_EXTENSION("REMOVE_EXTENSION", 6, "移除扩展合约的权限"),


    /**
     * 7、ADD_WRAPPER
     */
    ADD_WRAPPER("ADD_WRAPPER", 7, "添加装饰器业务合约的权限"),

    /**
     * 8、REMOVE_WRAPPER
     */
    REMOVE_WRAPPER("REMOVE_WRAPPER", 8, "移除装饰器业务合约的权限"),

    /**
     * 9、NEW_MEMBER
     */
    NEW_MEMBER("NEW_MEMBER", 9, "操作配置的权限"),

    /**
     * 10、JAIL_MEMBER
     */
    JAIL_MEMBER("JAIL_MEMBER", 10, "监禁成员的权限"),

    /**
     * 11、ADD_WRAPPER
     */
    CREATE_TASK("CREATE_TASK", 11, "创建任务的权限"),

    /**
     * 12、REMOVE_TASK
     */
    REMOVE_TASK("REMOVE_TASK", 12, "移除任务的权限"),

    /**
     * 20、FULL
     */
    FULL("FULL", 65535, "所有权限");

    private String name;

    private Integer code;


    private String desc;


    S11eDaoContractAcl(String name, Integer code, String desc) {
        this.name = name;
        this.code = code;
        this.desc = desc;
    }

    public String getName() {
        return name;
    }

    public Integer getCode() {
        return code;
    }


    public String getDesc() {
        return desc;
    }


    // 获取 flags 的 code 权限状态
    public boolean getFlag(Integer flags) {
        return ((flags >> this.code) % 2) == 1;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAddress(Integer code) {
        this.code = code;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    // 添加 flags 的 code 代表的权限
    public Integer setFalg(Integer flags) {
        if (!this.getFlag(flags)) {
            return (flags + 1 << this.code);
        } else {
            return flags;
        }
    }

    // 移除 flags 的 code 代表的权限
    public Integer clearFalg(Integer flags) {
        if (this.getFlag(flags)) {
            return (flags - 1 << this.code);
        } else {
            return flags;
        }
    }

    public Integer getFullFlag() {
        Integer ret = 0;
        for (S11eDaoContractAcl flag : values()) {
            ret += (1 << flag.getCode());
        }
        return ret;
    }


    /**
     * Json 枚举序列化
     */
    @JsonCreator
    public static S11eDaoContractAcl getInstanceByName(String name) {
        if (name == null) {
            return null;
        }
        for (S11eDaoContractAcl status : values()) {
            if (name.equals(status.getName())) {
                return status;
            }
        }
        return null;
    }
}
