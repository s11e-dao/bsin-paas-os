package me.flyray.bsin.domain.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * 合伙伙伴模型表
 * @TableName crm_sys_agent_model
 */

@Data
@TableName(value ="crm_sys_agent_model")
public class SysAgentModel implements Serializable {
    /**
     * 租户CODE
     */
    private String tenantId;

    /**
     * 模型类型:一级分销: level1, 二级分销: level2, 链路2+1: level2_1
     */
    private String model;

    /**
     * 省级合伙人分润比例
     */
    private Integer provincialAgentRate;

    /**
     * 市级合伙人分润比例
     */
    private Integer cityAgentRate;

    /**
     * 县级合伙人分润比例
     */
    private Integer countyAgentRate;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

    
}