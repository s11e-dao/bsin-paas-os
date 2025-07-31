package me.flyray.bsin.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 商户分类业态表（业态+分类+标签）
 * @TableName crm_merchant_business_type
 */
@TableName(value ="crm_merchant_business_type")
@Data
public class MerchantBusinessType implements Serializable {
    /**
     * 
     */
    @TableId
    private String serialNo;

    /**
     * 业态名称：如美食
     */
    private String name;

    /**
     * 图标URL
     */
    private String icon;

    /**
     * 父级ID
     */
    private String parentNo;

    /**
     * 排序值，越小越靠前
     */
    private String sort;

    /**
     * 状态：1启用，0禁用
     */
    private String status;

    /**
     * 租户ID
     */
    private String tenantId;


    @TableField(exist = false)
    private static final long serialVersionUID = 1L;


}