package me.flyray.bsin.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import me.flyray.bsin.entity.BaseEntity;
import me.flyray.bsin.enums.TransactionType;
import me.flyray.bsin.security.enums.BizRoleType;
import me.flyray.bsin.validate.AddGroup;
import org.springframework.format.annotation.DateTimeFormat;
import me.flyray.bsin.domain.enums.TransactionStatus;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 交易记录;
 * @TableName crm_transaction
 */
@Data
@TableName(value ="crm_transaction")
public class CrmTransaction extends BaseEntity implements Serializable {

    /**
     * @see TransactionType
     */
    private String transactionType;
    /**
     * 交易hash
     */
    private String txHash;
    /**
     * 合约地址
     */
    private String contractAddress;
    /**
     * 执行的合约方法
     */
    private String contractMethod;
    /**
     * 合约方法调用类型 1、非合约调用 2、合约调用
     */
    private Integer methodInvokeWay;
    /**
     * 交易状态; 1、等待 2、成功 3、失败
     * @see TransactionStatus
     */
    private String transactionStatus;

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
     * 用户角色类型，1.系统运营 2.平台租户 3.商户 4.代理商 5.客户 6.门店 99.无
     * @see BizRoleType
     */
    @NotBlank(message = "业务角色类型不能为空！", groups = AddGroup.class)
    private String bizRoleType;
    /**
     * 业务角色类型编号
     */
    @NotBlank(message = "业务角色序号不能为空！", groups = AddGroup.class)
    private String bizRoleTypeNo;
    /**
     * 租户
     */
    @NotBlank(message = "租户ID不能为空！", groups = AddGroup.class)
    private String tenantId;

}
