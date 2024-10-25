package me.flyray.bsin.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * 
 * @TableName crm_dis_policy_merchant
 */
@Data
@TableName(value ="crm_dis_policy_merchant")
public class DisPolicyMerchant implements Serializable {
    /**
     * 
     */
    @TableId
    private String serialNo;

    /**
     * 分佣政策ID
     */
    private String brokeragePolicyNo;

    /**
     * 租户id
     */
    private String tenantId;

    /**
     * 商户ID
     */
    private String merchantNo;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

}