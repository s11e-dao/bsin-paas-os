package me.flyray.bsin.domain.enums;

/**
 * 结算账户类型 1、微信 2、支付宝 3 银行卡 4 链地址
 */
public enum SettlementAccountType {

    /**
     * 微信
     */
    WECHAT("1","微信"),
    /**
     * 支付宝
     */
    ALIPAY("2","支付宝"),

    /**
     * 银行卡
     */
    BANK("3","银行卡"),


    /**
     * 链地址
     */
    CHAIN("4","链地址");

    // 状态码
    private String code;
    // 状态信息
    private String message;

    SettlementAccountType(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}
