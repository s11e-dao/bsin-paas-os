package me.flyray.bsin.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 商户支付资料进件信息表
 * @TableName crm_merchant_pay_entry
 */

@Data
@TableName(value ="crm_merchant_pay_entry")
public class MerchantPayEntry implements Serializable {
    /**
     * ID
     */
    @TableId
    private String serialNo;

    /**
     * 租户
     */
    private String tenantId;

    /**
     * 商户号
     */
    private String merchantNo;

    /**
     * 店铺ID
     */
    private String storeNo;

    /**
     * 服务商ID
     */
    private String isvId;

    /**
     * 支付渠道 wxPay: 微信 aliPay:支付宝
     */
    private String channel;

    /**
     * 进件信息(JSON)
     */
    private String requestJson;

    /**
     * 申请单号
     */
    private String applymentId;

    /**
     * 业务申请编号
     */
    private String businessCode;

    /**
     * 进件状态
     */
    private String status;

    /**
     * 特约商户号
     */
    private String subMchid;

    /**
     * 签约链接
     */
    private String signUrl;

    /**
     * 回调结果(JSON)
     */
    private String responseJson;

    /**
     * 逻辑删除 0、未删除 1、已删除
     */
    private Integer delFlag;

    /**
     * 创建人
     */
    private String createBy;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 修改人
     */
    private String updateBy;

    /**
     * 修改时间
     */
    private Date updateTime;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

}