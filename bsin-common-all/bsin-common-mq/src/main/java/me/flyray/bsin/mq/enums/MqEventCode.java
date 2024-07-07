package me.flyray.bsin.mq.enums;


import com.fasterxml.jackson.annotation.JsonCreator;

/**
 * @author bolei
 * @date 2023/6/29 15:43
 * @desc 队列事件编码
 */

public enum MqEventCode {

    /**
     * 创建钱包
     */
    CREATE_MPC_WALLET("createMpcWallet", "创建钱包"),

    /**
     * gas加油通知
     */
    GET_GAS_NOTIFY("getGasNotify", "gas加油"),

    /**
     * 资金归集通知
     */
    CASH_CONCENTRATION_NOTIFY("cashConcentrationNotify", "资金归集"),
    ;

    private String code;

    private String desc;

    MqEventCode(String code, String desc) {
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
    public static MqEventCode getInstanceById(String id) {
        if (id == null) {
            return null;
        }
        for (MqEventCode status : values()) {
            if (id.equals(status.getCode())) {
                return status;
            }
        }
        return null;
    }

}
