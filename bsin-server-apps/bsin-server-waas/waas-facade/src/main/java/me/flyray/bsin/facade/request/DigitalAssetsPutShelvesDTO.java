package me.flyray.bsin.facade.request;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import lombok.Data;

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * @author bolei
 * @date 2023/8/8 15:38
 * @desc
 */

@Data
public class DigitalAssetsPutShelvesDTO {

    private String tenantId;

    private String preview;

    @NotBlank(message = "资产名称不能为空！")
    private String assetsName;

    /**
     * @see AssetsCollectionType
     */
    @NotBlank(message = "资产类型不能为空！")
    private String assetsType;


    /**
     * 资产封面图片地址
     */
    private String coverImage;

    /**
     * metadata地址
     */
    private String metadataUrl;


    private BigInteger tokenId;

    private String multimediaType;

    private String digitalAssetsCollectionNo;

    @NotNull(message = "上架数量不能为空！")
    private BigDecimal putOnQuantity;

    private Integer price;

    /**
     * 领取方式：1 免费领取/空投 2 购买  3 固定口令领取 4 随机口令 5 盲盒
     */
    @NotBlank(message = "领取方式不能为空！")
    private String obtainMethod;

    private String password;

    @NotBlank(message = "资产描述不能为空！")
    private String description;

    private String attributes;

}
