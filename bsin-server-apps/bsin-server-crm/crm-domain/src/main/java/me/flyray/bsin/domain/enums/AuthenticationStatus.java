package me.flyray.bsin.domain.enums;

import com.fasterxml.jackson.annotation.JsonCreator;

/**
 * @author bolei
 * @date 2023/8/22
 *
 * @desc 商户认证状态   1: 待认证  2：认证成功  3：认证失败
 */
public enum AuthenticationStatus {

    /**
     * 待认证
     */
    TOBE_CERTIFIED("1", "待认证"),

    /**
     * 认证成功
     */
    CERTIFIED("2", "认证成功"),

    /**
     * 认证失败
     */
    CERTIFIED_FAILURE("3", "认证失败");


    private String code;

    private String desc;

    AuthenticationStatus(String code, String desc) {
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
    public static AuthenticationStatus getInstanceById(String id) {
        if (id == null) {
            return null;
        }
        for (AuthenticationStatus status : values()) {
            if (id.equals(status.getCode())) {
                return status;
            }
        }
        return null;
    }

}
