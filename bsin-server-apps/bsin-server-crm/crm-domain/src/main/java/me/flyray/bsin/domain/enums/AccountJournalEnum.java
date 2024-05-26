package me.flyray.bsin.domain.enums;

public enum AccountJournalEnum {

    NONE(100,"无"),

    /**
     * 订单类型
     */
    PAY(0,"支付"),
    REFUND(1,"退款"),
    SELL(2,"出售"),
    RECHARGE(3,"充值"),
    TRANSFER(4,"转账"),
    WITHDRAW(5,"提现"),
    CONSUMING(6,"消费"),

    /**
     * 出账入账标志
     */
    INT_ACCOUNT(0,"入账"),
    OUT_ACCOUNT(1,"出账");

    // 状态码
    private Integer code;
    // 状态信息
    private String message;

    AccountJournalEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}


