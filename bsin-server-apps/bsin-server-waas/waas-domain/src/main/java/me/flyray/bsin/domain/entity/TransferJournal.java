package me.flyray.bsin.domain.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.Date;

import lombok.Data;

/**
 * 
 * @TableName da_transfer_journal
 */
@TableName(value ="waas_transfer_journal")
@Data
public class TransferJournal implements Serializable {
    /**
     * transfer编号
     */
    @TableId
    private String serialNo;

    /**
     * 数字资产编号
     */
    private String digitalAssetsCollectionNo;

    /**
     * 链上唯一标识
     */
    private BigInteger tokenId;

    /**
     * 数量
     */
    private BigInteger amount;

    /**
     * mint出的nft图片地址
     */
    private String metadataImage;

    /**
     * metadata地址
     */
    private String metadataUrl;

    /**
     * 交易哈希
     */
    private String txHash;

    /**
     * 租户ID
     */
    private String tenantId;

    /**
     * 资产商户编号
     */
    private String merchantNo;

    /**
     * 
     */
    private Date createTime;

    /**
     * 转出客户号
     */
    private String fromCustomerNo;

    /**
     * 源地址
     */
    private String fromAddress;

    /**
     * 目标地址
     */
    private String toAddress;

    /**
     * 接收人姓名
     */
    private String toName;

    /**
     * 铸造人手机号
     */
    private String toPhone;

    /**
     * 接受客户号
     */
    private String toCustomerNo;

    /**
     * 链网络环境
     */
    private String chainEnv;


    /**
     * 链网络
     */
    private String chainType;

    /**
     * 资产类型
     */
    private String assetsType;

}