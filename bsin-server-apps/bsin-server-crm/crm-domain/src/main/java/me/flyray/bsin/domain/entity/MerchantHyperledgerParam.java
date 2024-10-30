package me.flyray.bsin.domain.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 
 * @TableName community_hyperledger_setting
 */
@TableName(value ="crm_merchant_hyperledger_param")
@Data
public class MerchantHyperledgerParam implements Serializable {
    /**
     * 品牌商户编号
     */
    @TableId
    private String merchantNo;

    /**
     * 租户id
     */
    private String tenantId;

    /**
     * 单笔交易社区收益比例
     */
    private String benefitAllocationRatio;

    /**
     * 社区金库分配比例
     */
    private String treasuryAllocationRatio;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 修改人
     */
    private String updateBy;

    /**
     * 修改时间
     */
    private Date updateTime;

}