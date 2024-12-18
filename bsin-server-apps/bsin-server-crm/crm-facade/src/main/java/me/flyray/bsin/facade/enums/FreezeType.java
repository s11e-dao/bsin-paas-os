package me.flyray.bsin.facade.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum FreezeType {

    /**
     * 冻结事件类型
     */
    PROPOSAL("1", "社区提案"),
    ORDER("2", "订单"),
    DIGITAL_ASSETS("3", "数字资产"),
    TASK("4", "任务");

    private String code;


    private String desc;
}
