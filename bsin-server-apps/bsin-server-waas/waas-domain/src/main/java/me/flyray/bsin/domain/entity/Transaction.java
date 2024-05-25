package me.flyray.bsin.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import me.flyray.bsin.entity.BaseEntity;
import me.flyray.bsin.validate.AddGroup;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 交易记录;
 * @TableName crm_transaction
 */
@Data
@TableName(value ="waas_transaction")
public class Transaction extends BaseEntity implements Serializable {
    /**
     * 交易类型;1、转入 2、转出 3、资金归集
     */
    private Integer transactionType;
    /**
     * 交易hash
     */
    private String txHash;
    /**
     * 合约地址
     */
    private String contractAddress;
    /**
     * 合约方法
     */
    private String contractMethod;
    /**
     * 合约方法调用类型 1、非合约调用 2、合约调用
     */
    private Integer methodInvokeWay;
    /**
     * 交易状态; 1、等待 2、成功 3、失败
     */
    private Integer transactionStatus;

    /**
     * 源地址
     */
    private String fromAddress;

    /**
     * 交易金额
     */
    private BigDecimal txAmount;

    /**
     * 实际消费gas费
     */
    private BigDecimal gasFee;

    /**
     * 手续费
     */
    private BigDecimal fee;

    /**
     * 目标地址
     */
    private String toAddress;

    /**
     * 备注
     */
    private String comment;

    /**
     * 交易完成时间
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private String completedTime;
    /**
     * 商户业务唯一标识
     */
    private String outSerialNo;
    /**
     * 业务角色类型;1、平台 2、商户 3、代理商 4、用户
     */
    @NotBlank(message = "业务角色类型不能为空！", groups = AddGroup.class)
    private Integer bizRoleType;
    /**
     * 业务角色序号
     */
    @NotBlank(message = "业务角色序号不能为空！", groups = AddGroup.class)
    private String bizRoleNo;
    /**
     * 租户
     */
    @NotBlank(message = "租户ID不能为空！", groups = AddGroup.class)
    private String tenantId;
}
