package me.flyray.bsin.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import me.flyray.bsin.domain.enums.BusinessModel;
import me.flyray.bsin.domain.enums.StoreType;

import java.io.Serializable;
import java.util.Date;

/**
 * 
 * @TableName crm_store
 */
@TableName(value ="crm_store")
@Data
public class Store implements Serializable {
    /**
     * 店铺ID
     */
    @TableId(value = "serial_no", type = IdType.ASSIGN_ID)
    private String serialNo;

    /**
     * 店铺名称
     */
    private String storeName;

    /**
     * 登录密码
     */
    private String password;

    /**
     * 店铺地址
     */
    private String address;

    /**
     * 经度
     */
    private String longitude;

    /**
     * 纬度
     */
    private String latitude;

    /**
     * 营业时间
     */
    private String businessHours;

    /**
     * 店铺介绍
     */
    private String description;

    /**
     * 店铺logo
     */
    private String logo;

    /**
     * 所属商户编号
     */
    private String merchantNo;

    /**
     * 租户id
     */
    private String tenantId;

    /**
     * 类型：0：总店 1：非总店
     * @see StoreType
     */
    private String type;

    /**
     * 门店所属区域边码
     */
    private String regionCode;

    /**
     * 等级序列号（关联crm_grade）
     */
    private String gradeNo;

    /**
     * 经营模式  1、直营，2、加盟
     * @see BusinessModel
     */
    private String businessModel;

    /**
     * 逻辑删除 0、未删除 1、已删除
     */
    @TableLogic(
            value = "0",
            delval = "1"
    )
    private Integer delFlag;

    private Date createTime;



}