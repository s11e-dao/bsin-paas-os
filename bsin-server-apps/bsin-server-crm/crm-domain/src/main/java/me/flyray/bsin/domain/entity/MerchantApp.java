package me.flyray.bsin.domain.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.util.Date;

import javax.validation.constraints.NotBlank;

import lombok.Data;

@Data
@TableName(value ="crm_merchant_app")
public class MerchantApp implements Serializable {

    /**
     * 序号
     */
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

    /**
     * 应用名称
     */
    @NotBlank(message = "app名称不能为空！")
    private String appName;

    /**
     * 应用ID：自动生成
     */
    private String appId;

    /**
     * 1: 应用 2：接口
     */
    private String appType;

    /**
     * 应用密钥：自动生成
     */
    private String appSecret;

    /**
     * 通知地址
     */
    private String notifyUrl;

    /**
     * 应用状态: 0 待审核 1 审核通过
     */
    private String status;

    /**
     * 应用描述
     */
    @NotBlank
    private String appDescription;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 修改时间
     */
    private Date updateTime;

    /**
     * 逻辑删除
     */
    @TableLogic(
            value = "0",
            delval = "1"
    )
    private Integer delFlag;

}