package me.flyray.bsin.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * 分销模型表
 * @TableName crm_dis_model
 */
@Data
@TableName(value ="crm_dis_model")
public class DisModel implements Serializable {
    /**
     * 
     */
    @TableId
    private Integer serialNo;

    /**
     * 租户CODE
     */
    private String tenantId;

    /**
     * 模型类型:一级分销:leave1, 二级分销:leave2, 链路2+1: leave2_1
     */
    private String model;

    /**
     * 退出团队机制
     */
    private String quitCurrentLimit;

    /**
     * 一级推荐奖励
     */
    private Integer firstInviteBrokerage;

    /**
     * 二级推荐奖励
     */
    private Integer secondInviteBrokerage;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

}