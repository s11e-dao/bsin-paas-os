package me.flyray.bsin.domain.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 不同业务角色的帐单表
 * @TableName crm_biz_role_bill
 */

@Data
@TableName(value ="crm_biz_role_bill")
public class BizRoleBill implements Serializable {
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
     * 交易场景：线上支付、线下扫码、转账、红包、AA收款等
     */
    private String billType;

    /**
     * 订单信息
     */
    private String billTypeNo;

    /**
     * 支付渠道：wechat_pay,alipay,bank_card,balance等
     */
    private String payChannel;

    /**
     * 优惠券信息
     */
    private Object discountInfo;

    /**
     * 优惠信息
     */
    private Date createTime;

    /**
     * 收支方向：income-收入, expense-支出
     */
    private String direction;

    /**
     * 付款方类型：业务角色类型
     */
    private String payerType;

    /**
     * 付款方ID
     */
    private String payerNo;

    /**
     * 收款方类型：业务角色类型
     */
    private String payeeType;

    /**
     * 收款方ID
     */
    private String payeeNo;

    /**
     * 交易金额
     */
    private String amount;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

}