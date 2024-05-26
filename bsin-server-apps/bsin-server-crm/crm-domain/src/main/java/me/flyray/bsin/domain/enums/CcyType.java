package me.flyray.bsin.domain.enums;

import com.fasterxml.jackson.annotation.JsonCreator;

/**
 * @author bolei
 * @date 2023/8/22
 *
 * @desc
 */
public enum CcyType {

    /**
     * 曲线积分账户 bonding_curve_token
     */
    BC("bc", "曲线积分"),

    /**
     * 数字积分
     */
    DP("dp", "数字积分"),

    /**
     * 法币账户
     */
    CNY("cny", "火钻");


    private String code;

    private String desc;

    CcyType(String code, String desc) {
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
    public static CcyType getInstanceById(String id) {
        if (id == null) {
            return null;
        }
        for (CcyType status : values()) {
            if (id.equals(status.getCode())) {
                return status;
            }
        }
        return null;
    }

}
