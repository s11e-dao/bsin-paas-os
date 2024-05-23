package me.flyray.bsin.blockchain.enums;

import com.fasterxml.jackson.annotation.JsonCreator;

/**
 * 合约类型
 */
public enum ContractProtocolStandards {

    /**
     * ERC20标准
     */
    ERC20("ERC20", "ERC20标准"),

    /**
     * ERC721标准
     */
    ERC721("ERC721", "ERC721标准"),

    /**
     * ERC1155标准
     */
    ERC1155("ERC1155", "ERC1155标准"),


    /**
     * ERC1155_1124标准
     */
    ERC1155_1124("ERC1155_1124", "ERC1155_1124标准"),


    /**
     * ERC721_1125标准
     */
    ERC721_1125("ERC721_1125", "ERC721_1125标准"),


    /**
     * ERC3525标准
     */
    ERC3525("ERC3525", "ERC3525标准"),

    /**
     * ERC6551标准
     */
    ERC6551("ERC6551", "ERC6551标准"),


    /**
     * Daobook ERC20标准
     */
    ERC20TokenContinuousExtension("ERC20TokenContinuousExtension", "Daobook ERC20标准"),

    /**
     * Daobook ERC721标准
     */
    ERC721TokenExtension("ERC721TokenExtension", "Daobook ERC721标准"),

    /**
     * Daobook ERC1155标准
     */
    ERC1155TokenExtension("ERC1155TokenExtension", "Daobook ERC1155标准"),


    /**
     * Daobook ERC3525标准
     */
    ERC3525TokenExtension("ERC3525TokenExtension", "Daobook ERC3525标准");

    private String code;

    private String desc;

    ContractProtocolStandards(String code, String desc) {
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
    public static ContractProtocolStandards getInstanceById(String id) {
        if (id == null) {
            return null;
        }
        for (ContractProtocolStandards status : values()) {
            if (id.equals(status.getCode())) {
                return status;
            }
        }
        return null;
    }


}
