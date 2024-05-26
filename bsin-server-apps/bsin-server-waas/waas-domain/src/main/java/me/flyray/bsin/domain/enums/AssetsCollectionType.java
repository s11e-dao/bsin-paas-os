package me.flyray.bsin.domain.enums;

import com.fasterxml.jackson.annotation.JsonCreator;

/**
 * @author bolei
 * @date 2023/6/29 15:43
 * @desc 品牌商户发行资产类型 1、数字徽章 2、PFP 3、积分 4、门票 5、pass卡
 */
public enum AssetsCollectionType {

    /**
     * 数字徽章 Digital badge
     */
    DIGITAL_BADGE("1", "数字徽章"),
    /**
     * PFP
     */
    PFP("2", "PFP"),
    /**
     * 数字积分 Digital integral
     */
    DIGITAL_POINT("3", "数字积分"),
    /**
     * 数字门票 Digital Tickets
     */
    DIGITAL_TICKETS("4", "数字门票"),
    /**
     * pass卡
     */
    PASS_CARD("5", "pass卡"),
    /**
     * 账户-联合曲线BC
     */
    ACCOUNT_BC("6", "账户-联合曲线BC"),
    /**
     * 满减
     */
    DISCOUNT("7", "满减"),
    /**
     * pass卡
     */
    AUTHORITY("8", "权限");

    private String code;

    private String desc;

    AssetsCollectionType(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    AssetsCollectionType(String code) {
        this.code = code;
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
    public static AssetsCollectionType getInstanceById(String id) {
        if (id == null) {
            return null;
        }
        for (AssetsCollectionType status : values()) {
            if (id.equals(status.getCode())) {
                return status;
            }
        }
        return null;
    }

}
