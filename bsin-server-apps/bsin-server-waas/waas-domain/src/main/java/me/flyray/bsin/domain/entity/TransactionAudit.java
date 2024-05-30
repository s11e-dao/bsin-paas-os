package me.flyray.bsin.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import me.flyray.bsin.entity.BaseEntity;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;

/**
 * 交易审核表;
 * @TableName crm_transaction_audit
 */
@Data
@TableName(value ="waas_transaction_audit")
public class TransactionAudit extends BaseEntity implements Serializable {
    /**
     * 审核类型;1、交易转出审核
     */
    private Integer auditYpe;

    /**
     * 审核状态;1、待审核 2、审核通过 3、审核拒绝
     */
    private Integer auditStatus;

    /**
     * 审核级别;1、低级风险 2、中级风险 3、高级风险
     */
    private Integer auditLevel;

    /**
     * 交易id
     */
    private String transactionNo;

    /**
     * 审核人（平台用户）
     */
    private Integer userId;

    /**
     * 审核人名称
     */
    private String username;

    /**
     * 审核时间
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private String auditTime;

    /**
     * 原因
     */
    private String reason;
    /**
     * 租户
     */
    private String tenantId;

}
