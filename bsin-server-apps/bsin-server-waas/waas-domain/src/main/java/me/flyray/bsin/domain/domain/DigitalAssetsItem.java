package me.flyray.bsin.domain.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;


import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;

import javax.validation.constraints.NotBlank;

import lombok.Data;
import me.flyray.bsin.validate.AddGroup;
import me.flyray.bsin.validate.EditGroup;
import me.flyray.bsin.validate.QueryGroup;

/**
 * 
 * @TableName da_digital_assets_item
 */

@Data
@TableName(value ="da_digital_assets_item")
public class DigitalAssetsItem implements Serializable {

    @TableId//(type = IdType.ASSIGN_ID)
    private String serialNo;
    private String tenantId;
    private String createBy;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date createTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date updateTime;
    @TableLogic(
            value = "0",
            delval = "1"
    )
    private String delFlag;

    /**
     * NFT资产名称
     */
    @NotBlank(message = "资产名称不能为空！" , groups = {AddGroup.class})
    private String assetsName;

    /**
     * NFT资产类型
     */
    private String assetsType;

    /**
     * 数字资产集合编号
     */
    private String digitalAssetsCollectionNo;


    /** 链类型
     * @see ChainType: conflux|polygon|ethereum|tron|bsc
     * */
    private String chainType;

    /** 链网络
     * @see ChainEnv
     * */
    private String chainEnv;


    /**
     * 描述
     */
    @NotBlank(message = "资产描述不能为空！" , groups = {AddGroup.class})
    private String description;

    /**
     * 多媒体类型： 1 图片  2 gif 3 视频 4 音频 5 json 6 文件夹
     */
    private String multimediaType;

    /**
     * 封面图片
     */
    private String coverImage;


    /**
     * 元数据url
     */
    private String metadataUrl;


    /**
     * 价格
     */
    private BigDecimal price;

    /**
     * 数量
     */
    private BigDecimal quantity;

    /**
     * 领取方式：1 免费领取/空投 2 购买  3 固定口令领取 4 随机口令 5 盲盒
     */
    private String obtainMethod;

    /**
     * 库存
     */
    private BigDecimal inventory;


    /**
     * 当前已经铸造的tokenId
     */
    private BigInteger currentMintTokenId;

    /**
     * 是否在售 0、是 1、否
     */
    private Integer onSell;

    /**
     * 商户号
     */
    private String merchantNo;

    /**
     * 1155协议默认上架有tokenId
     */
    private BigInteger tokenId;

    /**
     * 更新人
     */
    private String updateBy;

    @TableField(exist = false)
    private String merchantName;

    @TableField(exist = false)
    private String merchantLogo;

}