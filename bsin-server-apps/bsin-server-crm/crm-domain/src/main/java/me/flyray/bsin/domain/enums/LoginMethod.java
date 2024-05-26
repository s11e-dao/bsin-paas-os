package me.flyray.bsin.domain.enums;

import com.fasterxml.jackson.annotation.JsonCreator;

/**
 * @author bolei
 * @date 2023/7/3 13:38
 * @desc
 */
public enum LoginMethod {

    /**
     * 手机验证码登录
     */
    PHONE("1", "手机验证码登录"),
    /**
     * 微信小程序登录
     */
    MINAPP("2", "微信小程序登录");

    private String code;

    private String desc;

    LoginMethod(String code, String desc) {
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
    public static LoginMethod getInstanceById(String id) {
        if (id == null) {
            return null;
        }
        for (LoginMethod status : values()) {
            if (id.equals(status.getCode())) {
                return status;
            }
        }
        return null;
    }
}
