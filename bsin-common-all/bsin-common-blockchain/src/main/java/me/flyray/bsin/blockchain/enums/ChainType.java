package me.flyray.bsin.blockchain.enums;

import com.fasterxml.jackson.annotation.JsonCreator;

/**
 * 区块链网络类型
 */
public enum ChainType {

    /**
     * conflux网络
     */
    CONFLUX("conflux", "conflux网络"),
    /**
     * polygon网络
     */
    POLYGON("polygon", "polygon网络"),
    /**
     * Ethereum
     */
    ETHEREUM("ethereum", "以太坊"),
    /**
     * tron
     */
    TRON("tron", "波场"),
    /**
     * 币安智能链
     */
    BSC("bsc", "币安智能链"),
    /**
     * EVM兼容
     */
    EVM("evm", "EVM兼容");

    private String code;

    private String desc;

    ChainType(String code, String desc) {
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
    public static ChainType getInstanceById(Integer id) {
        if (id == null) {
            return null;
        }
        for (ChainType status : values()) {
            if (id.equals(status.getCode())) {
                return status;
            }
        }
        return null;
    }

}
