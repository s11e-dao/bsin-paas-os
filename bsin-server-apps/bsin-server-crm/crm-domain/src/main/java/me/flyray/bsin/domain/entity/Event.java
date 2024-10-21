package me.flyray.bsin.domain.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 客户等级事件规则表
 * @TableName crm_event
 */

@Data
@TableName(value ="crm_event")
public class Event implements Serializable {
    /**
     * 序列号
     */
    @TableId
    private String serialNo;

    /**
     * 租户id
     */
    private String tenantId;

    /**
     * 商户编码
     */
    private String merchantNo;

    /**
     * 事件名称
     */
    private String eventName;

    /**
     * 事件编码
     */
    private String eventCode;

    /**
     * 1、平台级动作  2、商户级动作
     */
    private String eventLevel;

    /**
     * 备注
     */
    private String remark;

    /**
     * 逻辑删除 0、未删除 1、已删除
     */
    private Integer delFlag;

    /**
     * 修改时间
     */
    private Date updateTime;

    /**
     * 修改人
     */
    private String updateBy;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 创建人
     */
    private String createBy;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

}