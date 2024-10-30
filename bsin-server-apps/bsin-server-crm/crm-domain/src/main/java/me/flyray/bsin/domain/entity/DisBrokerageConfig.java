package me.flyray.bsin.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 参与分佣设置表
 * @TableName crm_dis_brokerage_config
 */
@Data
@TableName(value ="crm_dis_brokerage_config")
public class DisBrokerageConfig implements Serializable {
    /**
     * 
     */

    private String serialNo;

    /**
     * 设置规则的租户
     */
    @TableId
    private String tenantId;

    /**
     * 运营平台分佣比例
     */
    private BigDecimal superTenantRate;

    /**
     * 租户平台分佣比例
     */
    private BigDecimal tenantRate;

    /**
     * 代理商分佣比例
     */
    private BigDecimal sysAgentRate;

    /**
     * 消费者返利比例
     */
    private BigDecimal customerRate;

    /**
     * 设置规则的商户
     */
    private String merchantNo;

//    @TableField(exist = false)
//    private static final long serialVersionUID = 1L;

}