package me.flyray.bsin.domain.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;

import lombok.Data;

/**
 * 
 * @TableName sys_product_app
 */

@Data
@TableName(value ="sys_product_app")
public class SysProductApp implements Serializable {

    /**
     * 产品ID
     */
    @TableId
    private String id;

    /**
     * 产品ID
     */
    private String productId;

    /**
     * 应用ID
     */
    private String appId;

    /**
     * 是否是基础应用，一个产品有一个基础应用
     */
    private Boolean baseFlag;

    /**
     * 业务角色类型
     */
    private String bizRoleType;

}