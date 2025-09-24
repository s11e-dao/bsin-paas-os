package me.flyray.bsin.domain.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 事件模型表：存储事件对应的奖励规则配置
 * @TableName brms_event_model
 */

@Data
@TableName(value ="brms_event_model")
public class BsinEventModel implements Serializable {
    /**
     * 主键，模型唯一标识
     */
    @TableId
    private String serialNo;

    /**
     * 关联事件编码
     */
    private String eventCode;

    /**
     * 租户ID，支持多租户
     */
    private String tenantId;

    /**
     * 模型名称
     */
    private String modelName;

    /**
     * 模型类型：3-规则模型（奖励规则）
     */
    private String modelType;

    /**
     * 规则配置（JSON格式）
     */
    private Object modelConfig;

    /**
     * 优先级，支持同一事件多个规则，数字越大优先级越高
     */
    private Integer priority;

    /**
     * 状态：0-禁用 1-启用
     */
    private Integer status;

    /**
     * 生效开始时间
     */
    private Date startTime;

    /**
     * 生效结束时间
     */
    private Date endTime;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 创建人
     */
    private String createBy;

    /**
     * 更新人
     */
    private String updateBy;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;


}