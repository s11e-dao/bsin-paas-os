package me.flyray.bsin.domain.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;

import lombok.Data;

/**
 * 营销任务参与条件配置表
 * @TableName crm_condition_relationship
 */

@Data
@TableName(value ="crm_condition_relation")
public class ConditionRelation implements Serializable {
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
     * 条件类型：1 资产类别；2 成员等级；3 账户
     */
    private String type;

    /**
     * 类型编号
     */
    private String typeNo;

    /**
     * 条件分类：1、会员等级 2 数字资产 3 任务 4 活动
     */
    private String category;

    /**
     * 条件分类编号
     */
    private String categoryNo;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

}