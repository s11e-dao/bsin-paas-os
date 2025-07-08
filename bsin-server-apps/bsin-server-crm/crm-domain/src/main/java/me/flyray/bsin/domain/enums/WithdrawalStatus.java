package me.flyray.bsin.domain.enums;

import com.fasterxml.jackson.annotation.JsonCreator;

/**
 * @author bsin
 * @date 2024/12/19
 * @desc 提现处理状态枚举
 */
public enum WithdrawalStatus {

    /**
     * 待处理
     */
    PENDING("0", "待处理"),

    /**
     * 处理中
     */
    PROCESSING("1", "处理中"),

    /**
     * 已完成
     */
    COMPLETED("2", "已完成"),

    /**
     * 已取消
     */
    CANCELLED("3", "已取消");

    private String code;

    private String desc;

    WithdrawalStatus(String code, String desc) {
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
    public static WithdrawalStatus getInstanceById(String id) {
        if (id == null) {
            return null;
        }
        for (WithdrawalStatus status : values()) {
            if (id.equals(status.getCode())) {
                return status;
            }
        }
        return null;
    }
} 