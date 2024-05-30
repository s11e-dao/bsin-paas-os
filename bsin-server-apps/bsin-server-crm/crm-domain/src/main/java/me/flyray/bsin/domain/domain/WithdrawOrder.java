package me.flyray.bsin.domain.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.util.Date;

import lombok.Data;

/**
 * 
 * @TableName market_withdraw_order
 */

@Data
@TableName(value ="crm_withdraw_order")
public class WithdrawOrder implements Serializable {
    /**
     * 提现记录编号
     */
    @TableId
    private String serialNo;

    /**
     * 外部订单号
     */
    private String outOdrerNo;

    /**
     * 外部订单号
     */
    private String txHash;

    /**
     * 客户编号
     */
    private String customerNo;

    /**
     * 备注
     */
    private String remark;

    /**
     * 交易状态 0、提现中 1、提现成功 2、提现失败
     */
    private String status;

    /**
     * 金额
     */
    private Integer amount;

    /**
     * 币种
     */
    private String ccy;

    /**
     * 创建时间/提现时间
     */
    private Date createTime;

    /**
     * 审核时间
     */
    private Date auditTime;

    /**
     * 商户号
     */
    private String merchantNo;

    /**
     * 租户ID
     */
    private String tenantId;

    /**
     * 收款人账号
     */
    private String payeeAccount;

    /**
     * 收款人账户类型（1、支付宝 2、微信 3、钱包地址）
     */
    private String payeeAccountType;

    /**
     * 提现客户类型：1、个人 2、商户 3、平台
     */
    private String payeeType;

    /**
     * 收款人姓名
     */
    private String payeeName;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

}