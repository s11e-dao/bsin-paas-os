package me.flyray.bsin.domain.enums;

import com.fasterxml.jackson.annotation.JsonCreator;

/**
 * @author bolei
 * @date 2023/8/22
 *
 * @desc 商户|平台|合伙人|门店|会员状态：0 正常 1 冻结 2 待审核
 */
public enum BizRoleStatus {

    /**
     * 正常
     */
    NORMAL("0", "正常"),

    /**
     * 冻结
     */
    FROZEN("1", "冻结"),

    /**
     * 待审核
     */
    PENDING("2", "待审核"),

    /**
     * 审核中
     */
    REVIEWING("3", "审核中"),

    /**
     * 驳回
     */
    REJECTED("4", "驳回"),

    /**
     * 禁用
     */
    DISABLED("6", "禁用"),

    ;


    private String code;

    private String desc;

    BizRoleStatus(String code, String desc) {
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
    public static BizRoleStatus getInstanceById(String id) {
        if (id == null) {
            return null;
        }
        for (BizRoleStatus status : values()) {
            if (id.equals(status.getCode())) {
                return status;
            }
        }
        return null;
    }

}
