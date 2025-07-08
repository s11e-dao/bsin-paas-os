package me.flyray.bsin.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import me.flyray.bsin.domain.enums.WithdrawalAuditStatus;
import me.flyray.bsin.domain.enums.WithdrawalStatus;

import java.io.Serializable;
import java.util.Date;

/**
 * 
 * @TableName crm_withdrawal_record
 */

@Data
@TableName(value ="crm_withdrawal_record")
public class WithdrawalRecord implements Serializable {

    /**
     * 
     */
    @TableId
    private String serialNo;

    /**
     * 租户ID
     */
    private String tenantId;

    /**
     * 业务角色类型
     */
    private String bizRoleType;

    /**
     * 业务角色类型编号
     */
    private String bizRoleTypeNo;

    /**
     * 提现账号
     */
    private String accountNo;

    /**
     * 提现方式：1-银行卡，2-支付宝，3-微信，4-其他
     */
    private String withdrawalType;

    /**
     * 提现金额
     */
    private String amount;

    /**
     * 手续费
     */
    private String fee;

    /**
     * 结算账户编号
     */
    private String settlementAccountNo;

    /**
     * 结算账户信息快照
     */
    private Object settlementAccountJson;

    /**
     * 申请时间
     */
    private Date createTime;

    /**
     * 审核状态：0-待审核，1-审核通过，2-审核拒绝
     * @see WithdrawalAuditStatus
     */
    private String auditStatus;

    /**
     * 审核时间
     */
    private Date auditTime;

    /**
     * 处理状态：0-待处理，1-处理中，2-已完成，3-已取消
     * @see WithdrawalStatus
     */
    private String status;

    /**
     * 备注
     */
    private String remark;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;


}