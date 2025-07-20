package me.flyray.bsin.domain.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import me.flyray.bsin.validate.AddGroup;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 参与分佣设置表
 * @TableName crm_dis_brokerage_config
 */
@Data
@TableName(value ="crm_dis_commission_config")
public class DisCommissionConfig implements Serializable {
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
     * 合伙人分佣比例
     */
    private BigDecimal sysAgentRate;

    /**
     * 消费者返利比例
     */
    private BigDecimal customerRate;

    /**
     * 分销模型的分销者比例
     */
    private BigDecimal distributorRate;

    /**
     * 佣金兑换数字积分比例
     */
    private BigDecimal exchangeDigitalPointsRate;

    /**
     * 商户让利比例
     */
    private BigDecimal merchantProfitSharingRate;

    /**
     * 设置规则的商户
     */
    private String merchantNo;

//    @TableField(exist = false)
//    private static final long serialVersionUID = 1L;

}