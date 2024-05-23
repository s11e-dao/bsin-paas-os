package me.flyray.bsin.enums;

import com.fasterxml.jackson.annotation.JsonCreator;

/**
 * 合约类型
 */
public enum S11eDaoContractType {


    /**
     * Test
     */
    TEST("Test", "测试合约"),

    /**
     * Address
     */
    ADDRESS("Address", "装饰器业务合约"),


    /**
     * Template
     */
    TEMPLATE("Template", "合约模板"),

    /**
     * Trade
     */
    TRADE("Trade", "交易合约");

    private String code;

    private String desc;

    S11eDaoContractType(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public String getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }


    /**
     * Json 枚举序列化
     */
    @JsonCreator
    public static S11eDaoContractType getInstanceById(Integer id) {
        if (id == null) {
            return null;
        }
        for (S11eDaoContractType status : values()) {
            if (id.equals(status.getCode())) {
                return status;
            }
        }
        return null;
    }


}
