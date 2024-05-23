package me.flyray.bsin.blockchain.enums;

import com.fasterxml.jackson.annotation.JsonCreator;

/**
 * 区块链网络环境
 */
public enum ChainEnv {

    /**
     * 链主网
     */
    MAIN("main", "主网"),
    /**
     * 链测试网
     */
    TEST("test", "测试网络");

    private String code;

    private String desc;

    ChainEnv(String code, String desc) {
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
    public static ChainEnv getInstanceById(String id) {
        if (id == null) {
            return null;
        }
        for (ChainEnv status : values()) {
            if (id.equals(status.getCode())) {
                return status;
            }
        }
        return null;
    }

}
