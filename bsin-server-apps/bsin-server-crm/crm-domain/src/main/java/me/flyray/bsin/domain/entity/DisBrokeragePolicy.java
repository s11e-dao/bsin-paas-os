package me.flyray.bsin.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 
 * @TableName crm_dis_brokerage_policy
 */
@Data
@TableName(value ="crm_dis_brokerage_policy")
public class DisBrokeragePolicy implements Serializable {
    /**
     * 
     */
    @TableId
    private String serialNo;

    /**
     * 租户CODE
     */
    private String tenantId;

    /**
     * 触发分佣的事件(对应事件表的编码), 收款: PAY_SUCCESS, 发货: TRANSFER
     */
    private String triggerEventCode;

    /**
     * 触发事件后几天
     */
    private Integer triggerEventAfterDate;

    /**
     * 分佣政策名称
     */
    private String policyName;

    /**
     * 政策开始时间
     */
    private Date startTime;

    /**
     * 政策结束时间
     */
    private Date endTime;

    /**
     * 不进行分佣的资金: 运费: carriage, 自定义: custom
     */
    private String excludeFeeType;

    /**
     * 自定义扣除费用比例
     */
    private BigDecimal excludeCustomPer;

    /**
     * 备注
     */
    private String remark;

    /**
     * 是否启用 1,0
     */
    private Integer status;

    /**
     * 
     */
    private Integer delFlag;

    /**
     * 
     */
    private Date createTime;

    /**
     * 
     */
    private Date updateTime;

    /**
     * 创建用户
     * **/
    private String createBy;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

}