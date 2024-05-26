package me.flyray.bsin.facade.request;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
//import me.flyray.bsin.domain.enums.AssetsCollectionType;

import lombok.Data;

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * @author bolei
 * @date 2023/6/29 19:42
 * @desc
 */

@Data
public class DigitalAssetsIssueReqDTO {

    private String tenantId;

    /**
     * 品牌商户号
     */
    private String merchantNo;

    /**
     * 部署人
     */
    private String createBy;

    /**
     * 资产名称
     */
    @NotBlank(message = "资产名称不能为空！")
    private String name;

    /**
     * 资产符号
     */
    @NotBlank(message = "资产符号不能为空！")
    private String symbol;

    /**
     * 发行上限
     */
    private String cap;

    /**
     * 总供应量
     */
    @NotNull(message = "资产总供应量不能为空！")
    private BigInteger totalSupply;


    /**
     * 初始供应量
     */
    private BigDecimal initialSupply;

    /**
     * 保留小数位数
     */
    private Integer decimals;


    /**
     * 数字资产集合类型
     * @see AssetsCollectionType
     */
    private String assetsCollectionType;



    /**
     * 市场流通状态 0、未流通 1、流通
     */
    private Integer status;

    /**
     * 合约地址
     */
    private String contractAddress;

    /**
     * 图片是否相同标识
     */
    private String metadataImageSameFlag;

    /**
     * 集合封面图片
     */
    private String coverImage;

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

    /**
     * metadata baseURI
     */
//    @NotBlank(message = "metadata baseURI不能为空！")
    private String baseURI;

    /**
     * 合约模板bytecode
     */
    private String protocolBytecode;

    /**
     * 合约模板abi字符
     */
    private String protocolAbi;

    /**
     * 部署私钥
     */
    private String privateKey;

    /**
     * 部署owner地址
     */
    private String ownerAddress;


    /**
     *  协议编号
     */
    @NotBlank(message = "资产协议编号不能为空！")
    private String protocolCode;

    /**
     *  协议类型：
     *  @see AssetsCollectionType
     */
    private String protocolType;

    /**
     *  协议类型
     */
    private String protocolStandards;


    /**
     *  资产描述
     */
    private String description;

}
