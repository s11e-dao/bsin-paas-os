package me.flyray.bsin.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;

import lombok.Data;

/**
 * 
 * @TableName da_digital assets
 */
@TableName(value ="da_digital_assets_collection")
@Data
public class DigitalAssetsCollection implements Serializable {

    @TableId(
            type = IdType.ASSIGN_ID
    )
    private String serialNo;
    private String tenantId;
    private String createBy;
    @JsonFormat(
            pattern = "yyyy-MM-dd HH:mm:ss",
            timezone = "GMT+8"
    )
    private Date createTime;

    @TableLogic(
            value = "0",
            delval = "1"
    )
    private String delFlag;

    /**
     * 品牌商户号
     */
    private String merchantNo;

    /**
     * 资产集合名称
     */
    private String name;

    /**
     * 资产符号
     */
    private String symbol;

    /**
     * 总供应量
     */
    private BigDecimal totalSupply;

    /**
     * 小数位数
     */
    private Integer decimals;

    /**
     * 市场流通状态 0、未流通 1、流通
     */
    private String status;

    /**
     * 合约地址
     */
    private String contractAddress;

    /**
     * 图片是否相同标识
     */
    private String metadataImageSameFlag;

    /**
     * 集合类型
     */
    private String collectionType;


    /**
     * 库存
     */
    private BigDecimal inventory;


    /**
     * 初始供应量
     */
    private BigDecimal initialSupply;

    /**
     * 元数据文件ipfs目录
     */
    private String metadataFilePathNo;

    /**
     * 元数据模板编号
     */
    private String metadataTemplateNo;

    /**
     * 是否是基于联合取消铸造：0 否 1是
     */
    private String bondingCurveFlag;

    /**
     * 合约协议
     */
    private String contractProtocolNo;

    /**
     * 链网络环境
     */
    private String chainEnv;

    /**
     * 链
     */
    private String chainType;

    /**
     * 是否被赞助 0  否 1是
     */
    private String sponsorFlag;

}