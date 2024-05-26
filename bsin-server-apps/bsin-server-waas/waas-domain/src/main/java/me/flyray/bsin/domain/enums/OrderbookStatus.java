package me.flyray.bsin.domain.enums;

import com.fasterxml.jackson.annotation.JsonCreator;

/**
 * @author bolei
 * @date 2023/6/30 15:16
 * @desc 订单状态 1、待交易 2、部分交易 3、完成交易 4、已撤单 5、部分交易并撤单
 */
public enum OrderbookStatus {

    /**
     * 待交易
     */
    PENDING("1", "待交易"),
    /**
     * 部分交易
     */
    PARTIAL_COMPLETED("2", "部分交易"),
    /**
     * 完成交易
     */
    COMPLETED("3", "完成交易"),
    /**
     * 已撤单
     */
    CANCED("4", "已撤单"),
    /**
     * 部分交易并撤单
     */
    PARTIAL_COMPLETED_CANCED("5", "部分交易并撤单");

    private String code;

    private String desc;

    OrderbookStatus(String code, String desc) {
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
    public static OrderbookStatus getInstanceById(String id) {
        if (id == null) {
            return null;
        }
        for (OrderbookStatus status : values()) {
            if (id.equals(status.getCode())) {
                return status;
            }
        }
        return null;
    }

}
