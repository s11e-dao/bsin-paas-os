package me.flyray.bsin.domain.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;

import lombok.Data;
import me.flyray.bsin.domain.enums.FileType;

/**
 *
 * @TableName da_mint_journal
 */
@TableName(value ="waas_mint_journal")
@Data
public class MintJournal implements Serializable {
    /**
     * mint编号
     */
    @TableId
    private String serialNo;

    /**
     * 数字资产集合编号
     */
    private String digitalAssetsCollectionNo;

    /**
     * 数字资产编号
     */
    private String digitalAssetsItemNo;

    /**
     * 链上唯一标识
     */
    private BigInteger tokenId;

    /**
     * 商户号
     */
    private String merchantNo;

    /**
     * 链上唯一标识
     */
    private BigDecimal amount;

    /**
     * 多媒体类型： 1:图片  2:gif 3:视频 4:音频 5:json 6:文件夹
     * @see FileType
     */
    private String multimediaType;

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
     * 铸造客户号
     */
    private String toCustomerNo;

    /**
     *
     */
    private Date createTime;

    /**
     * 铸造人手机号
     */
    private String toPhone;

    /**
     * 铸造人姓名
     */
    private String toMinterName;

    /**
     * 铸造人地址
     */
    private String toAddress;

    /**
     * 链网络环境
     */
    private String chainEnv;


    /**
     * 链网络
     */
    private String chainType;


    /**
     * NFT名称
     */
    private String itemName;

    /**
     * 资产类型
     */
    private String assetsType;
}