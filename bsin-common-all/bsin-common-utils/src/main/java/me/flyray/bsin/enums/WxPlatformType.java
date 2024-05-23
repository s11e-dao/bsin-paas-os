package me.flyray.bsin.enums;

import com.fasterxml.jackson.annotation.JsonCreator;

/**
 * 微信平台类别：mp(公众号服务订阅号)、miniapp(小程序)、 cp(企业号|企业微信)、pay(微信支付)、open(微信开放平台)
 */
public enum WxPlatformType {

    /**
     * cp(企业号|企业微信)
     */
    CP("cp", "企业号|企业微信"),

    /**
     * mp(公众号服务订阅号)
     */
    MP("mp", "公众号服务订阅号"),

    /**
     * miniapp(小程序)
     */
    MINIAPP("miniapp", "小程序"),

    /**
     * cp(企业号|企业微信)
     */
    PAY("pay", "pay(微信支付)"),

    /**
     * open(微信开放平台)
     */
    OPEN("open", "微信开放平台");

    private String type;

    private String desc;

    WxPlatformType(String code, String desc) {
        this.type = code;
        this.desc = desc;
    }

    public String getType() {
        return type;
    }

    public String getDesc() {
        return desc;
    }

    /**
     * Json 枚举序列化
     */
    @JsonCreator
    public static WxPlatformType getInstanceByType(String id) {
        if (id == null) {
            return null;
        }
        for (WxPlatformType status : values()) {
            if (id.equals(status.getType())) {
                return status;
            }
        }
        return null;
    }

}
