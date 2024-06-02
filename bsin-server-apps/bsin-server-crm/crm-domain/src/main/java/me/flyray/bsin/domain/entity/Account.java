package me.flyray.bsin.domain.entity;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import lombok.Data;

/**
 *
 * @TableName crm_customer_account
 */
@TableName(value ="crm_account")
@Data
public class Account implements Serializable {
    /**
     * 账户编号
     */
    @TableId
    private String serialNo;

    /**
     * 账户名称
     */
    private String name;

    /**
     * 客户编号
     */
    private String customerNo;

    /**
     * 账户类别
     */
    private String category;

    /**
     * 租户id
     */
    private String tenantId;

    /**
     * 累计金额
     */
    private BigDecimal cumulativeAmount;

    /**
     * 账户余额
     */
    private BigDecimal balance;

    /**
     * 小数点数
     */
    private Integer decimals;


    /**
     * 冻结金额
     */
    private BigDecimal freezeAmount;

    /**
     * 账户类型 0、个人账户 1、企业账户 2 租户(dao)账户
     */
    private String type;

    /**
     * 币种 用币种英文代替
     */
    private String ccy;

    /**
     * 账户状态 0、正常 1、冻结
     */
    private String status;

    /**
     * 余额校验码
     */
    private String checkCode;

    /**
     * 创建时间
     */
    private Date createTime;

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

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

    @TableField(exist = false)
    private BigDecimal anchoringValue;

}
