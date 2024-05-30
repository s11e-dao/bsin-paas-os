package me.flyray.bsin.domain.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.math.BigInteger;
import java.util.Date;
import lombok.Data;

/**
 * 会员通行证表
 * @TableName da_customer_pass_card
 */
@TableName(value ="waas_customer_pass_card")
@Data
public class CustomerPassCard implements Serializable {
    /**
     * 序列号
     */
    @TableId
    private String serialNo;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 租户
     */
    private String tenantId;

    /**
     * 客户号
     */
    private String customerNo;

    /**
     * 客户数字资产编号
     */
    private String digitalAssetsItemNo;

    /**
     * 会员卡资产NFT的Assets集合的封面
     */
    @TableField(exist = false)
    private String passCardAssetsCoverImage;

    /**
     * 会员卡资产NFT的Assets集合的名字
     */
    @TableField(exist = false)
    private String passCardAssetsName;

    /**
     * 会员卡资产NFT的name字段
     */
    @TableField(exist = false)
    private String passCardNftName;

    /**
     * 会员卡资产NFT的image
     */
    @TableField(exist = false)
    private String passCardNftImage;

    /**
     * 会员卡资产NFT的tokenUri
     */
    @TableField(exist = false)
    private String passCardNftTokenUri;

    /**
     * 所属商户下的劳动价值积分--bondingCurveBalance
     */
    @TableField(exist = false)
    private String passCardBalance;

    /**
     * 卡号
     */
    private BigInteger tokenId;

    /**
     * 商户号
     */
    private String merchantNo;

    /**
     * 备注
     */
    private String remark;

    /**
     * 状态
     */
    private Integer status;


    /**
     * TBA账户地址：会员卡是一个ERC6551协议的TBA账户，一张会员卡一个TBA账户
     */
    private String tbaAddress;


    /**
     * 数量
     */
    private Integer amount;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

    @TableField(exist = false)
    private String merchantName;

    @TableField(exist = false)
    private String merchantLogo;

}