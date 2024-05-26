package me.flyray.bsin.domain.domain;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import lombok.Data;

/**
 * 客户等级事件规则表
 * @TableName crm_event_rule
 */
@TableName(value ="crm_event_rule")
@Data
public class EventRule implements Serializable {
    /**
     * 序列号
     */
    @TableId
    private String serialNo;

    /**
     * 创建人
     */
    private String createBy;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 修改人
     */
    private String updateBy;

    /**
     * 修改时间
     */
    private Date updateTime;

    /**
     * 逻辑删除 0、未删除 1、已删除
     */
    @TableLogic(
            value = "0",
            delval = "1"
    )
    private Integer delFlag;

    /**
     * 事件名称
     */
    private String name;

    /**
     * 资产类型（ 1：虚拟账户 2：数字资产）
     */
    private Integer assetType;

    /**
     * 数量
     */
    private BigDecimal amount;

    /**
     * 备注
     */
    private String remark;

    /**
     * 商户编码
     */
    private String merchantNo;

    /**
     * 租户id
     */
    private String tenantId;

    /**
     * 资产类型编号
     */
    private Integer assetTypeNo;

    /**
     * 事件编码
     */
    private String eventCode;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

}