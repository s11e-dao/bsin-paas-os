package me.flyray.bsin.domain.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.util.Date;

import lombok.Data;

/**
 * 
 * @TableName da_digital_assets_item_obtain_code
 */
@TableName(value ="da_digital_assets_item_obtain_code")
@Data
public class DigitalAssetsItemObtainCode implements Serializable {
    /**
     * 
     */
    @TableId
    private String serialNo;

    /**
     * NFT编号
     */
    private String assetsNo;

    /**
     * 领取口令
     */
    private String password;

    /**
     * 领取状态：1 待领取 2 已领取
     */
    private String status;

    /**
     * 
     */
    private Date createTime;

    /**
     * 
     */
    private Date updateTime;

    /**
     * 铸造编号
     */
    private String mintNo;

}