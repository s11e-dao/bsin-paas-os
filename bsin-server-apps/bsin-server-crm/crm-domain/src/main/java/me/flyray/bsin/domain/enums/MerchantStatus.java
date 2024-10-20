package me.flyray.bsin.domain.enums;

import com.fasterxml.jackson.annotation.JsonCreator;

/**
 * @author bolei
 * @date 2023/8/22
 *
 * @desc 商户状态：0 正常 1 冻结 2 待审核
 */
public enum MerchantStatus {

    /**
     * 正常
     */
    NORMAL("0", "正常"),

    /**
     * 冻结
     */
    FREEZED("2", "冻结"),

    /**
     * 待审核
     */
    TOBE_CERTIFIED("3", "待审核");


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
