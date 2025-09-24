package me.flyray.bsin.domain.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 事件表：存储系统中所有可触发奖励的事件定义
 * @TableName brms_event
 */

@Data
@TableName(value ="brms_event")
public class BsinEvent implements Serializable {
    /**
     * 主键，事件唯一标识
     */
    @TableId
    private String serialNo;

    /**
     * 租户ID，支持多租户
     */
    private String tenantId;

    /**
     * 事件名称
     */
    private String eventName;

    /**
     * 事件编码，业务系统调用标识
     */
    private String eventCode;

    /**
     * 事件级别：1-平台级 2-商户级
     */
    private String eventLevel;

    /**
     * 事件类型：USER-用户事件 ORDER-订单事件 SYSTEM-系统事件
     */
    private String eventType;

    /**
     * 事件描述
     */
    private String description;

    /**
     * 状态：0-禁用 1-启用
     */
    private Integer status;

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