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
 * 模型参数Json优化
 */
@Data
@TableName(value ="crm_dis_model")
public class DisModel implements Serializable {


    /**
     * 租户CODE
     */
    private String tenantId;

    /**
     * 商户号
     */
    private String merchantNo;

    /**
     * 模型类型:一级分销:level1, 二级分销:level2, 链路2+1: level2_1
     */
    private String model;

    /**
     * 退出团队机制
     */
    private Integer quitCurrentLimit;

    /**
     * 一级推荐奖励
     */
    private Integer firstInviteBrokerage;

    /**
     * 二级推荐奖励
     */
    private Integer secondInviteBrokerage;


}