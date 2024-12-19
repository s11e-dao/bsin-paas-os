package me.flyray.bsin.domain.enums;

import com.fasterxml.jackson.annotation.JsonCreator;

/**
 * @author bolei
 * @date 2023/8/22
 *
 * @desc 商户|平台|代理商状态：0 正常 1 冻结 2 待审核
 */
public enum MerchantStatus {

    /**
     * 正常
     */
    NORMAL("0", "正常"),

    /**
     * 冻结
     */
    FREEZED("1", "冻结"),

    /**
     * 待审核
     */
    TOBE_CERTIFIED("2", "待审核"),

    /**
     * rebut 驳回
     */
    REBUT("3", "驳回"),

    /**
     * reject 拒绝
     */
    REJECT("4", "拒绝"),

    ;


    private String code;

    private String desc;

    MerchantStatus(String code, String desc) {
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
    public static MerchantStatus getInstanceById(String id) {
        if (id == null) {
            return null;
        }
        for (MerchantStatus status : values()) {
            if (id.equals(status.getCode())) {
                return status;
            }
        }
        return null;
    }

}
