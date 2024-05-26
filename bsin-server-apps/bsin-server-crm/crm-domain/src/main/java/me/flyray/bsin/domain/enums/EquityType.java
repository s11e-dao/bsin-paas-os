package me.flyray.bsin.domain.enums;

import com.fasterxml.jackson.annotation.JsonCreator;

/**
 * @author bolei
 * @date 2023/10/18
 * @desc 资产类型：1:数字徽章 2:PFP  3：账户-数字token(DP) 4:数字门票 5：Paas卡
 * 6：账户-联合曲线(BC)  ) 7：满减 8：权限 9：会员等级
 */
public enum EquityType {

    /**
     * 数字徽章 badge
     */
    BADGE("1","数字徽章"),

    /**
     * PFP
     */
    PFP("2","PFP"),

    /**
     * 数字token
     */
    DP("3","数字token"),

    /**
     * 数字门票
     */
    DT("4","数字门票"),

    /**
     * Paas卡
     */
    PASS_CARD("5","Paas卡"),

    /**
     * 联合曲线
     */
    BC("6","联合曲线"),

    /**
     * 联合曲线
     */
    GRADE("9","等级");

    // 状态码
    private String code;
    // 状态信息
    private String desc;

    EquityType(String code, String desc) {
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
    public static EquityType getInstanceById(String id) {
        if (id == null) {
            return null;
        }
        for (EquityType status : values()) {
            if (id.equals(status.getCode())) {
                return status;
            }
        }
        return null;
    }

}
