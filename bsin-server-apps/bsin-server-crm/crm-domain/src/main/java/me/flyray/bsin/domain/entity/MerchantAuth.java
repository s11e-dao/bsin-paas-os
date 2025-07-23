package me.flyray.bsin.domain.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 商户认证信息表
 * @TableName crm_merchant_auth
 */

@Data
@TableName(value ="crm_merchant_auth")
public class MerchantAuth implements Serializable {
    /**
     * 主键流水号
     */
    @TableId
    private String serialNo;

    /**
     * 租户ID
     */
    private String tenantId;

    /**
     * 商户号
     */
    private String merchantNo;

    /**
     * 应用ID: 商户认证信息对应的业务应用ID
     */
    private String bizRoleAppId;

    /**
     * 商户登录名
     */
    private String username;

    /**
     * 登录密码(加密存储)
     */
    private String password;

    /**
     * 企业名称
     */
    private String merchantName;

    /**
     * 基础信息认证状态
     */
    private String baseInfoAuthStatus;

    /**
     * 法人姓名
     */
    private String legalPersonName;

    /**
     * 法人证件类型(ID_CARD/PASSPORT)
     */
    private String legalPersonCredType;

    /**
     * 法人证件国徽面
     */
    private String legalPersonIdCardNational;

    /**
     * 法人证件正面
     */
    private String legalPersonIdCardCopy;

    /**
     * 法人证件号码
     */
    private String legalPersonCredNo;

    /**
     * 法人信息认证状态
     */
    private String legalPersonInfoAuthStatus;

    /**
     * 营业执照URL
     */
    private String businessLicenceImg;

    /**
     * 统一社会信用代码
     */
    private String businessNo;

    /**
     * 经营范围
     */
    private String businessScope;

    /**
     * 所属业态
     */
    private String businessType;

    /**
     * 营业信息认证状态
     */
    private String businessInfoAuthStatus;

    /**
     * 商户LOGO URL
     */
    private String logoUrl;

    /**
     * 联系手机号
     */
    private String contactPhone;

    /**
     * 联系人电话
     */
    private String contactName;

    /**
     * 企业官网
     */
    private String website;

    /**
     * 注册地址
     */
    private String merchantAddress;

    /**
     * 状态:0=正常,1=冻结,2=待审核,3=驳回,4=拒绝
     */
    private Object status;

    /**
     * 商户类别:1=普通商户,2=WEB3商户
     */
    private Object category;

    /**
     * 类型:1=企业,2=个人,99=平台直属
     */
    private Object merchantType;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 认证状态:1=待认证, 2=待完善 ,3=成功 4 =失败
     */
    private Object authStatus;

    /**
     * 商户描述
     */
    private String description;

    /**
     * 版本号（乐观锁）
     */
    private Integer version;

    /**
     * 删除标记:0=正常,1=删除
     */
    @TableLogic(
            value = "0",
            delval = "1"
    )
    private String delFlag;

    /**
     * 备注信息
     */
    private String remark;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

}