package me.flyray.bsin.domain.enums;

import com.fasterxml.jackson.annotation.JsonCreator;

/**
 * @author leonard
 * @date 2023/11/17 15:43
 * @desc 合约名称： 1、S11eCore 2、S11eDao 3、S11eProfile 4、S11eDaoFactory  5、S11eProfileFactory  6、ERC6551Account 7、ERC6551Registry
 */
public enum ProtocolName {

    /**
     * S11eCore
     */
    S11E_CORE("S11eCore", "S11eCore"),
    /**
     * S11eDao
     */
    S11E_DAO("S11eDao", "S11eDao"),
    /**
     * S11eProfile
     */
    S11E_PROFILE("S11eProfile", "S11eProfile"),
    /**
     * S11eDaoFactory
     */
    S11E_DAO_FACTORY("S11eDaoFactory", "S11eDaoFactory"),
    /**
     * S11eProfileFactory
     */
    S11E_PROFILE_FACTORY("S11eProfileFactory", "S11eProfileFactory"),
    /**
     * ERC6551Account
     */
    ERC6551_ACCOUNT("ERC6551Account", "ERC6551Account"),
    /**
     * ERC6551Registry
     */
    ERC6551_REGISTRY("ERC6551Registry", "ERC6551Registry");

    private String code;

    private String desc;

    ProtocolName(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    ProtocolName(String code) {
        this.code = code;
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
    public static ProtocolName getInstanceById(String id) {
        if (id == null) {
            return null;
        }
        for (ProtocolName status : values()) {
            if (id.equals(status.getCode())) {
                return status;
            }
        }
        return null;
    }

}
