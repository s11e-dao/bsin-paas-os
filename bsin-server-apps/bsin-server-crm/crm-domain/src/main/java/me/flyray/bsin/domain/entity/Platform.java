package me.flyray.bsin.domain.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 
 * @TableName crm_platform
 */

@Data
@TableName(value ="crm_platform")
public class Platform implements Serializable {
    /**
     * 
     */
    @TableId
    private String serialNo;

    /**
     * 
     */
    private String tenantId;

    /**
     * 商户登录名称
     */
    private String username;

    /**
     * 企业名称
     */
    private String platformName;

    /**
     * 企业工商号
     */
    private String businessNo;

    /**
     * 法人姓名
     */
    private String legalPersonName;

    /**
     * 法人证件类型
     */
    private String legalPersonCredType;

    /**
     * 法人证件号
     */
    private String legalPersonCredNo;

    /**
     * 营业执照图片
     */
    private String businessLicenceImg;

    /**
     * 商户logo
     */
    private String logoUrl;

    /**
     * 联系电话
     */
    private String phone;

    /**
     * 公司网址
     */
    private String netAddress;

    /**
     * 企业地址
     */
    private String platformAddress;

    /**
     * 状态：0 正常 1 冻结 2 待审核
     */
    private Integer status;

    /**
     * 平台类型：1 个人 2 企业
     */
    private Integer type;

    /**
     * 
     */
    private String createBy;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 
     */
    private String updateBy;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 认证状态   1: 待认证  2：认证成功  3：认证失败
     */
    private String authenticationStatus;

    /**
     * 业态
     */
    private String businessType;

    /**
     * 经营范围
     */
    private String businessScope;

    /**
     * 删除标识
     */
    private String delFlag;

    /**
     * 平台描述
     */
    private String description;

    /**
     * 
     */
    private String txPassword;

    /**
     * 
     */
    private Integer txPasswordStatus;

    /**
     * 
     */
    private String googleSecretKey;

    /**
     * 生态价值分配模型
     */
    private String ecoValueAllocationModel;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

}