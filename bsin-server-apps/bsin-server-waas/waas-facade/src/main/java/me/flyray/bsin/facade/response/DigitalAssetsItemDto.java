package me.flyray.bsin.facade.response;
import com.baomidou.mybatisplus.annotation.TableField;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import lombok.Data;

/**
 * 
 * 合并 da_digital_assets_collection 和 da_digital_assets_item
 */

@Data
public class DigitalAssetsItemDto implements Serializable {

    @TableId
    private String serialNo;

    /**
     * 租户号
     */
    private String tenantId;

    /**
     * 商户号
     */
    private String merchantNo;


    @TableLogic(
            value = "0",
            delval = "1"
    )
    private String delFlag;

    /**
     * NFT资产名称
     */
    private String assetsName;

    /**
     * NFT资产类型
     */
    private String assetsType;

    /**
     * 数字资产集合编号
     */
    private String digitalAssetsCollectionNo;


    /**
     * 数字资产集合合约地址
     */
    private String contractAddress;



    /** 链类型
//     * @see ChainType: conflux|polygon|ethereum|tron|bsc
     * */
    private String chainType;

    /** 链网络
//     * @see ChainEnv
     * */
    private String chainEnv;


    /**
     * 描述
     */
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
     * 价格
     */
    private BigDecimal price;

    /**
     * 数量
     */
    private BigInteger quantity;

    /**
     * 领取方式：1 免费领取/空投 2 购买  3 固定口令领取 4 随机口令 5 盲盒
     */
    private String obtainMethod;

    /**
     * 库存
     */
    private BigInteger inventory;

    /**
     * 是否在售 0、是 1、否
     */
    private Integer onSell;


    /**
     * 1155协议默认上架有tokenId
     */
    private Integer tokenId;

    /**
     * 更新人
     */
    private String updateBy;

    /**
     * 创建人
     */
    private String createBy;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 商户名字
     */
    private String merchantName;

}