package me.flyray.bsin.domain.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import lombok.Data;

/**
 *
 * @TableName crm_customer_account_journal
 */

@Data
@TableName(value ="crm_account_journal")
public class AccountJournal implements Serializable {
    /**
     * 账户流水编号
     */
    @TableId
    private String serialNo;

    /**
     * 租户id
     */
    private String tenantId;

    /**
     * 商户id
     */
    private String merchantNo;

    /**
     * 客户编号
     */
    private String bizRoleTypeNo;

    /**
     * 账户编号
     */
    private String accountNo;

    /**
     * 账户类型 0、个人账户 1、企业账户
     */
    private String accountType;

    /**
     * 业务类型 0、支付 1、退款 2、出售 3、充值 4、转账 5、提现
     * @see TransactionType
     */
    private Integer orderType;

    /**
     * 订单号
     */
    private String orderNo;

    /**
     * 出账入账标志 0、出账 1、入账
     */
    private Integer inOutFlag;

    /**
     * 金额
     */
    private BigDecimal amount;

    /**
     * 币种
     */
    private String ccy;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 备注
     */
    private String remark;


    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

}
