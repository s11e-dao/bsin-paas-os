package me.flyray.bsin.domain.enums;

public enum AccountEnum {

    /**
     *
     */
    NORMAL("0","正常"),
    FREEZE("1","冻结"),

    BILLING("0","出账"),
    RECORDED("1","入账");

    // 状态码
    private String code;
    // 状态信息
    private String message;

    AccountEnum(String code, String message) {
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
