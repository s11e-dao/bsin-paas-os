package me.flyray.bsin.domain.enums;

import com.fasterxml.jackson.annotation.JsonCreator;

/**
 * @author leonard
 * @date 2023/11/13 15:43
 * @desc 合约分类： 1、Core 2、Factory 3、Extension 4、Wrapper  5、Proxy  5、Other
 */
public enum ContracrCategory {

    /**
     * 核心合约
     */
    CORE("1", "Core"),
    /**
     * 工厂合约
     */
    FACTORY("2", "Factory"),
    /**
     * 插件合约
     */
    EXTENSION("3", "Extension"),
    /**
     * 代理合约
     */
    PROXY("4", "Proxy"),
    /**
     * 其他分类
     */
    OTHER("5", "Other");

    private String code;

    private String desc;

    ContracrCategory(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    ContracrCategory(String code) {
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
    public static ContracrCategory getInstanceById(String id) {
        if (id == null) {
            return null;
        }
        for (ContracrCategory status : values()) {
            if (id.equals(status.getCode())) {
                return status;
            }
        }
        return null;
    }

}
