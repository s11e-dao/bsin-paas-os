package me.flyray.bsin.domain.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * 
 * @TableName crm_event_model
 */

@Data
@TableName(value ="brms_event_model")
public class EventModel implements Serializable {
    /**
     * 
     */
    @TableId
    private String serialNo;

    /**
     * 
     */
    private String tenantId;

    /**
     * 事件编码
     */
    private String eventCode;

    /**
     * 事件模型
     */
    private String modelNo;

    /**
     * 1、流程模型 2、表单模型 3、规则模型 4、推理模型
     */
    private String modelType;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

}