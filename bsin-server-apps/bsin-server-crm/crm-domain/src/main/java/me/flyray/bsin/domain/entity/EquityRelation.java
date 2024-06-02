package me.flyray.bsin.domain.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;

import lombok.Data;

/**
 * 客户等级权益
 * @TableName crm_equity_relationship
 */

@Data
@TableName(value ="crm_equity_relation")
public class EquityRelation implements Serializable {
    /**
     * 序列号
     */
    @TableId
    private String serialNo;

    /**
     * 租户
     */
    private String tenantId;

    /**
     * 商户编码
     */
    private String merchantNo;

    /**
     * 权益分类：1、会员等级 2 数字资产 3 任务 4 活动
     */
    private String category;

    /**
     * 权益分类编号
     */
    private String categoryNo;

    /**
     * 权益类型（1-权限，2-利益）
     */
    private Integer type;

    /**
     * 关联权益类型编号（事件或收益）
     */
    private String typeNo;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

}