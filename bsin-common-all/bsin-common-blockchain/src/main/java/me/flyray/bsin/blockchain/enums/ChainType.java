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
    public static ChainType getInstanceById(String id) {
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
    
    /**
     * 根据链名称获取链类型
     */
    public static ChainType fromChainName(String chainName) {
        if (chainName == null) {
            return null;
        }
        for (ChainType chainType : values()) {
            if (chainType.getCode().equalsIgnoreCase(chainName)) {
                return chainType;
            }
        }
        return null;
    }
    
    /**
     * 检查是否为 EVM 兼容链
     */
    public boolean isEVMCompatible() {
        return this == ETHEREUM || this == BSC || this == POLYGON || this == EVM;
    }
    
    /**
     * 检查是否为测试网络
     */
    public boolean isTestnet() {
        return this.getCode().toLowerCase().contains("test");
    }

}
