package me.flyray.bsin.domain.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;

import lombok.Data;

/**
 * 
 * @TableName da_token_release_journal
 */

@Data
@TableName(value ="waas_token_release_journal")
public class TokenReleaseJournal implements Serializable {
    /**
     * 
     */
    @TableId
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
    private String customerNo;

    /**
     * 分配数量
     */
    private BigDecimal amout;

    /**
     * 分配时间
     */
    private Date createTime;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

}