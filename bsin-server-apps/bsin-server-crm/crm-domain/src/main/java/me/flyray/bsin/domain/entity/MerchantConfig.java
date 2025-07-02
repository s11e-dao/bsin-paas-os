package me.flyray.bsin.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 商户配置表;
 * @TableName crm_merchant_config
 */
@Data
@TableName(value ="crm_merchant_config")
public class MerchantConfig implements Serializable {

    /**
     * 商户配置表id
     */
    private String serialNo;

    /**
     * 所属租户
     */
    private String tenantId;

    /**
     * 商户id
     */
    private String merchantNo;

    /**
     * 商户会员模型： 1平台会员 2商户会员 3店铺会员
     */
    private String memberModel;

    /**
     * 回调地址
     */
    private String callbackAddress;

    /**
     * 商户让利配置
     */
    private BigDecimal profitSharingRate;

    /**
     * 创建人
     */
    private String createBy;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新人
     */
    private String updateBy;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 逻辑删除;0、未删除 1、已删除
     */
    private Integer delFlag;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

}