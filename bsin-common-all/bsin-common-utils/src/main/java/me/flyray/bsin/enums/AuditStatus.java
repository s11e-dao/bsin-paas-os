package me.flyray.bsin.enums;

import com.fasterxml.jackson.annotation.JsonCreator;

/**
 * @author bolei
 * @date 2023/8/21
 * @desc
 */
public enum AuditStatus {

    /**
     * 通过 pass
     */
    PASS("0", "通过"),

    /**
     * 驳回 拒绝 Reject
     */
    REJECT("1", "拒绝"),

    /**
     * 终止 terminate
     */
    TERMINATE("2", "终止");

    private String code;

    private String desc;

    AuditStatus(String code, String desc) {
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
    public static AuditStatus getInstanceById(Integer id) {
        if (id == null) {
            return null;
        }
        for (AuditStatus status : values()) {
            if (id.equals(status.getCode())) {
                return status;
            }
        }
        return null;
    }

}
