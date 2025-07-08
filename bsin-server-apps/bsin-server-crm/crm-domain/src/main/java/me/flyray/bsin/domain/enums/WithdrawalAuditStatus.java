package me.flyray.bsin.domain.enums;

import com.fasterxml.jackson.annotation.JsonCreator;

/**
 * @author bsin
 * @date 2024/12/19
 * @desc 提现审核状态枚举
 */
public enum WithdrawalAuditStatus {

    /**
     * 待审核
     */
    PENDING("0", "待审核"),

    /**
     * 审核通过
     */
    APPROVED("1", "审核通过"),

    /**
     * 审核拒绝
     */
    REJECTED("2", "审核拒绝");

    private String code;

    private String desc;

    WithdrawalAuditStatus(String code, String desc) {
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
    public static WithdrawalAuditStatus getInstanceById(String id) {
        if (id == null) {
            return null;
        }
        for (WithdrawalAuditStatus status : values()) {
            if (id.equals(status.getCode())) {
                return status;
            }
        }
        return null;
    }
} 