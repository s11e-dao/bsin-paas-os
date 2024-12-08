package me.flyray.bsin.domain.enums;

import com.fasterxml.jackson.annotation.JsonCreator;

/**
 * @author bolei
 * @date 2023/8/22
 *
 * @desc 状态： 0：禁用 1:正常 2:待支付
 */
public enum MemberStatus {

    /**
     * 禁用
     */
    FORBIDDEN("0", "禁用"),

    /**
     * 待支付
     */
    NORMAL("2", "正常"),
    /**
     * 待支付
     */
    TOBE_PAID("2", "待支付"),

    /**
     * 待审核
     */
    TOBE_CERTIFIED("3", "待审核");


    private String code;

    private String desc;

    MemberStatus(String code, String desc) {
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
    public static MemberStatus getInstanceById(String id) {
        if (id == null) {
            return null;
        }
        for (MemberStatus status : values()) {
            if (id.equals(status.getCode())) {
                return status;
            }
        }
        return null;
    }

}
