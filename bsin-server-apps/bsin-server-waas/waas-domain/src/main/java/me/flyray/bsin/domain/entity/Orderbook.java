package me.flyray.bsin.domain.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;

import lombok.Data;

/**
 * 
 * @TableName da_orderbook
 */
@TableName(value ="waas_orderbook")
@Data
public class Orderbook implements Serializable {
    /**
     * 订单编号
     */
    @TableId
    private String serialNo;

    /**
     * 租户ID
     */
    private String tenantId;

    /**
     * 资产商户号
     */
    private String merchantNo;

    /**
     * 来源客户号
     */
    private String fromCustomerNo;

    /**
     * 客户类型：1 个人  2商户
     */
    private String customerType;

    /**
     * 来源资产编号
     */
    private String fromDigitalAssetsNo;

    /**
     * 资产类型：1：NFT、2：FT
     */
    private String fromAssetsType;

    /**
     * 来源资产tokenId
     */
    private BigInteger fromTokenId;

    /**
     * 资产数量
     */
    private BigDecimal fromAmount;

    /**
     * 订单状态 1、待交易 2、部分交易 3、完成交易 4、已撤单 5、部分交易并撤单
     */
    private String status;

    /**
     * 目标资产编号
     */
    private String toDigitalAssetsNo;

    /**
     * 目标资产类型
     */
    private String toAssetsType;

    /**
     * 资产数量
     */
    private BigDecimal toAmount;

    /**
     * 汇率
     */
    private BigDecimal exchangeRate;

    /**
     * 已经兑换数量
     */
    private BigDecimal exchangedAmount;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

}