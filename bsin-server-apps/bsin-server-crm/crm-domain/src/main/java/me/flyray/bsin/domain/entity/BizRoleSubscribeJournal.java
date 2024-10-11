package me.flyray.bsin.domain.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.util.Date;

import lombok.Data;

/**
 * 
 * @TableName 租户和或商户订阅系统应用或功能
 */

@Data
@TableName(value ="crm_biz_role_subscribe_journal")
public class BizRoleSubscribeJournal implements Serializable {
    /**
     * 
     */
    @TableId
    private String serialNo;

    /**
     * 
     */
    private String merchantNo;

    /**
     * 支付状态
     */
    private String payStatus;

    /**
     * 支付金额
     */
    private String amout;

    /**
     * 租户的产品ID
     */
    private String productId;

    /**
     * 订阅的应用ID
     */
    private String appId;

    /**
     * 订阅的应用功能ID
     */
    private String appFunctionId;

    /**
     * 
     */
    private Date createTime;

    /**
     * 
     */
    private Date startTime;

    /**
     * 
     */
    private Date endTime;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

}