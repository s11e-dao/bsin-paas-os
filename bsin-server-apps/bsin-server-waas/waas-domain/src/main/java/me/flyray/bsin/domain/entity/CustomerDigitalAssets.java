package me.flyray.bsin.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
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
 * @TableName da_customer_digital_assets
 */
@TableName(value ="waas_customer_digital_assets")
@Data
public class CustomerDigitalAssets implements Serializable {

    /**
     * ID
     */
    @TableId
    private String serialNo;

    /**
     * 客户编号
     */
    private String customerNo;

    /**
     * 客户数字资产编号
     */
    private String digitalAssetsItemNo;

    /**
     * 资产编号
     */
    private BigInteger tokenId;

    /**
     * 持有数量
     */
    private BigDecimal amount;

    /**
     * 租户id
     */
    private String tenantId;


    /**
     * 商户No
     */
    private String merchantNo;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;


    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

}