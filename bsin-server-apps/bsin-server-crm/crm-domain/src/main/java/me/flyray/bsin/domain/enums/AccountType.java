package me.flyray.bsin.domain.enums;

/**
 * 账户类型 0、个人账户 1、企业账户 2 租户(dao)账户
 */
public enum AccountType {

    /**
     * 成员账户
     */
    MEMBER("0","个人"),
    /**
     * 商户账户
     */
    MERCHANT("1","商户"),

    /**
     * 租户客户账户
     */
    TENANT("2","租户"),

    /**
     * 平台账户
     */
    PLATFORM("3","平台");

    // 状态码
    private String code;
    // 状态信息
    private String message;

    AccountType(String code, String message) {
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
