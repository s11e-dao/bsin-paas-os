package me.flyray.bsin.facade.response;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;

import java.math.BigInteger;

import lombok.Builder;
import lombok.Data;
//import me.flyray.bsin.blockchain.enums.ChainType;
//import me.flyray.bsin.blockchain.enums.ChainEnv;

/**
 * @author bolei
 * @date 2023/7/21 13:37
 * @desc
 */

@Data
public class DigitalAssetsItemRes {

    /**
     * 资产编号
     */
    @TableId
    private String serialNo;

    /**
     * 租户ID
     */
    private BigInteger tenantId;

    /**
     * 资产商户号
     */
    private String merchantNo;

    /**
     * 客户头像
     */
    private String customerAvatar;

    /**
     * 客户号
     */
    private String customerNo;

    /**
     * 会员号
     */
    private String memberNo;

    private String multimediaType;

    /**
     * 封面图片
     */
    private String coverImage;

    /**
     * 资产Image
     */
    private String metadataImage;

    /**
     * 元数据url
     */
    private String metadataUrl;

    /**
     *  数字资产集合名称
     */
    private String assetsName;


    /**
     *  数字资产NFT名称
     */
    private String name;


    /**
     *  数字资产符号
     */
    private String symbol;

    /**
     *  数字资产类型
     */
    private String assetsType;

    private String digitalAssetsCollectionNo;

    /**
     *  tokenId
     */
    private BigInteger tokenId;

    /** 链类型
//     * @see ChainType: conflux|polygon|ethereum|tron|bsc
     * */
    private String chainType;

    /** 链网络
//     * @see ChainEnv
     * */
    private String chainEnv;



    /**
     * 数量
     */
    private String amount;

    /**
     * 价格
     */
    private String toAmount;

    /**
     * 挂单数量
     */
    private String fromAmount;

    /**
     * 与平台积分兑换比例
     */
    private String exchangeRate;

    private String username;

    private String avatar;

    private String merchantName;

    @TableField(exist = false)
    private String merchantLogo;

    private String tbaAddress;

}
