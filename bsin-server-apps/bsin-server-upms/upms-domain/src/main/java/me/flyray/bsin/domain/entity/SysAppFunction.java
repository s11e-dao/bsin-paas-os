package me.flyray.bsin.domain.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;

import lombok.Data;

/**
 * 
 * @TableName sys_app_function
 */

@Data
@TableName(value ="sys_app_function")
public class SysAppFunction implements Serializable {
    /**
     * 
     */
    @TableId
    private String appFunctionId;

    /**
     * 功能名称
     */
    private String functionName;

    /**
     * 功能编号
     */
    private String functionCode;

    /**
     * 功能所属应用
     */
    private String appId;

    /**
     * 功能描述
     */
    private String remark;

    /**
     * 基础功能标识：0 非基础功能 1基础功能
     */
    private Boolean baseFlag;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

}