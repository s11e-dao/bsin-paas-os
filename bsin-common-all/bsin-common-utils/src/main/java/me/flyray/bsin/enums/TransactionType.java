package me.flyray.bsin.enums;

public enum TransactionType {

    NONE(100,"无"),

    /**
     * 订单类型
     */
    PAY(1,"支付"),
    RECHARGE(2,"充值"),
    TRANSFER(3,"转账"),
    WITHDRAW(4,"提现"),
    REFUND(5,"退款"),
    SETTLEMENT(7,"结算");


    // 状态码
    private Integer code;
    // 状态信息
    private String message;

    TransactionType(Integer code, String message) {
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


