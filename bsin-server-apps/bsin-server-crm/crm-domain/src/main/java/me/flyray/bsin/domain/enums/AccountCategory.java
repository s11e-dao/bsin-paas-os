package me.flyray.bsin.domain.enums;

import com.fasterxml.jackson.annotation.JsonCreator;

/**
 * @author bolei
 * @date 2023/8/9
 * @desc 商户账户类别 amountCategory
 * 账户类别： 1:余额账户 2:累计收入 3:累计支出 4:在途账户
 */
public enum AccountCategory {


    /**
     * 余额账户, 各种币种的账户余额
     */
    BALANCE("1", "余额账户"),


    /**
     * 累计收入
     */
    ACCUMULATED_INCOME("2", "累计收入"),

    /**
     * 累计支出
     */
    ACCUMULATED_EXPENDITURE("3", "累计支出"),

    /**
     * 在途（In transit）账户: 属于中间账户
     */
    IN_TRANSIT("4", "在途账户"),

    /**
     * 待结算账户 Pending settlement
     */
    PENDING_SETTLEMENT("5", "待结算账户"),

    /**
     * 待分佣账户 Pending Brokerage
     */
    PENDING_BROKERAGE("6", "待分佣账户"),

    /**
     * Community 社区总收入账户
     */
    COMMUNITY_ACCUMULATED_INCOME_TOKEN("99", "社区总收入账户");

    private String code;

    private String desc;

    AccountCategory(String code, String desc) {
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
    public static AccountCategory getInstanceById(String id) {
        if (id == null) {
            return null;
        }
        for (AccountCategory status : values()) {
            if (id.equals(status.getCode())) {
                return status;
            }
        }
        return null;
    }
}
