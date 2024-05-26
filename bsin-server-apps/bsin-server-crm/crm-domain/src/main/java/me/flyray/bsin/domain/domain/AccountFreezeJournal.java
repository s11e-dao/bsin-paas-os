package me.flyray.bsin.domain.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 冻结流水表
 * </p>
 *
 * @author leonard
 * @since 2023-10-19
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("crm_account_freeze_journal")
public class AccountFreezeJournal implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 序列号
     */
    @TableId(value = "serial_no", type = IdType.ASSIGN_ID)
    private String serialNo;

    /**
     * 租户号
     */
    private String tenantId;

    /**
     * 商户号
     */
    private String merchantNo;

    /**
     * 客户号
     */
    @TableField(exist = false)
    private String customerNo;


    /**
     * 客户或者商户的账户编号
     */
    private String customerAccountNo;

    /**
     * 冻结事件类型：（提案、订单等类型)
     */
    private String type;

    /**
     * 冻结的事件类型编号（提案、订单等编号）
     */
    private String typeNo;

    /**
     * 冻结金额
     */
    private BigDecimal freezeAmount;

    /**
     * 冻结状态：（1：已冻结  2：部分解冻  3：已解冻）
     */
    private String status;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 修改时间
     */
    private Date updateTime;



}
