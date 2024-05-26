package me.flyray.bsin.domain.domain;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.util.Date;

import lombok.Data;

/**
 * 
 * @TableName da_orderbook_match_serial
 */
@TableName(value ="da_orderbook_match_journal")
@Data
public class OrderbookMatchJournal implements Serializable {
    /**
     * 订单编号
     */
    @TableId
    private String serialNo;

    /**
     * 订单号
     */
    private String orderbookNo;

    /**
     * 订单状态 0、失败 1、成功 
     */
    private Integer status;

    /**
     * 资产数量
     */
    private String amount;

    /**
     * 兑换的客户号
     */
    private String toCustomerNo;

    /**
     * 兑换的客户类型：1 个人客户 2商户客户
     */
    private String toCustomerType;

    /**
     * 创建时间
     */
    private Date createTime;

}