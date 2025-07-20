package me.flyray.bsin.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;

/**
 * 合伙伙伴模型表
 * @TableName crm_sys_agent_model
 */
@TableName(value ="crm_sys_agent_model")
public class SysAgentModel implements Serializable {
    /**
     * 租户CODE
     */
    private String tenantId;

    /**
     * 模型类型:区域合伙、平台合伙模式
     */
    private String model;

    /**
     * 省级合伙人
     */
    private Integer provincialAgent;

    /**
     * 省级合伙人分润比例
     */
    private Integer provincialAgentRate;

    /**
     * 市级合伙人
     */
    private Integer cityAgent;

    /**
     * 市级合伙人分润比例
     */
    private Integer cityAgentRate;

    /**
     * 县级合伙人
     */
    private Integer countyAgent;

    /**
     * 县级合伙人分润比例
     */
    private Integer countyAgentRate;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

}