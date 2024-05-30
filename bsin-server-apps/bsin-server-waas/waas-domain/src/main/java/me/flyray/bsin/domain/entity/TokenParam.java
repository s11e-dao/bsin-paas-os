package me.flyray.bsin.domain.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;

import lombok.Data;

/**
 * 数字积分参数配置
 * @TableName da_token_param
 */

@Data
@TableName(value ="da_token_param")
public class TokenParam implements Serializable {

    /**
     * ID
     */
    @TableId
    private Integer serialNo;

    /**
     * 租户ID
     */
    private String tenantId;

    /**
     * 商户号
     */
    private String merchantNo;

    /**
     * 数字资产集合
     */
    private String digitalAssetsCollectionNo;

    /**
     * 预留量
     */
    private BigDecimal reservedAmount;

    /**
     * 发行方总供应量
     */
    private BigDecimal totalSupply;

    /**
     * 预估捕获的总劳动价值
     */
    private BigDecimal captureTotalValue;

    /**
     * 释放方式：1、劳动价值释放， 2、购买释放， 3、周期释放
     */
    private String releaseMethod;

    /**
     * 单元释放的触发价值(特指捕获劳动价值的积分数量)，每累计达到释放一次
     */
    private BigDecimal unitReleaseTriggerValue;

    /**
     * 单元释放的token数量
     */
    private BigDecimal unitReleaseAmout;

    /**
     * 释放周期：单位天
     */
    private Integer releaseCycle;

    /**
     * 发行方式
     */
    private String issueMethod;

    /**
     * 发行方类型：1、平台 2、租户 3、商户
     */
    private String issuerType;

    /**
     * 状态：0 未启用 1 启用
     */
    private String status;

    /**
     * 锚定法币价值
     */
    private BigDecimal anchoringValue;

    /**
     * 流通量
     */
    private BigDecimal circulation;


    /**
     * 数字积分：联合曲线--> 数字积分=bc*exchangeRate
     */
    private BigDecimal exchangeRate;

    /**
     * name
     */
    private String name;
    /**
     * symbol
     */
    private String symbol;

    /**
     * 小数点
     */
    private Integer decimals;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

}