package me.flyray.bsin.domain.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 
 * @TableName crm_dis_brokerage_rule
 */
@Data
@TableName(value ="crm_dis_commission_rule")
public class DisCommissionRule implements Serializable {
    /**
     * 商品分佣比例ID
     */
    @TableId
    private String serialNo;

    /**
     * 
     */
    private String tenantId;

    /**
     * 分佣政策ID
     */
    private String brokeragePolicyNo;

    /**
     * 一级分佣比例
     */
    private Integer firstSalePer;

    /**
     * 二级分佣比例
     */
    private Integer secondSalePer;

    /**
     * 商品类型
     */
    private String goodsCategoryNo;

    /**
     * 商户ID
     */
    private String merchantNo;

    /**
     * 
     */
    private Integer delFlag;

    /**
     * 
     */
    private Date createTime;

    /**
     * 
     */
    private Date updateTime;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

}