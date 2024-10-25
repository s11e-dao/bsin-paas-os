package me.flyray.bsin.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 
 * @TableName crm_dis_brokerage_goods
 */
@Data
@TableName(value ="crm_dis_brokerage_goods")
public class DisBrokerageGoods implements Serializable {
    /**
     * 
     */
    @TableId
    private Integer serialNo;

    /**
     * 商品类型
     */
    private String goodsCategoryNo;

    /**
     * 商户ID
     */
    private String merchantNo;

    /**
     * 分佣规则ID
     */
    private String brokerageRuleNo;

    /**
     * 
     */
    private Integer delFlag;

    /**
     * 
     */
    private Date createTime;

    /**
     * 
     */
    private Date updateTime;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

}