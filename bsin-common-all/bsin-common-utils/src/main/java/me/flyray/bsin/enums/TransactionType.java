package me.flyray.bsin.enums;

import com.fasterxml.jackson.annotation.JsonCreator;

/**
 * 1、支付 2、充值 3、转账 4、提现 5、退款 6、结算 7、收入 8、赎回
 */
public enum TransactionType {

    NONE("100","无"),

    /**
     * 订单类型
     */
    PAY("1","支付"),
    RECHARGE("2","充值"),
    TRANSFER("3","转账"),
    WITHDRAW("4","提现"),
    REFUND("5","退款"),
    SETTLEMENT("6","结算"),

    INCOME("7","收入"),

    REDEEM("8","赎回");

    // 状态码
    private String code;
    // 状态信息
    private String message;

    TransactionType(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    @JsonCreator
    public static TransactionType getInstanceById(String id) {
        if (id == null) {
            return null;
        }
        for (TransactionType status : values()) {
            if (id.equals(status.getCode())) {
                return status;
            }
        }
        return null;
    }

}


