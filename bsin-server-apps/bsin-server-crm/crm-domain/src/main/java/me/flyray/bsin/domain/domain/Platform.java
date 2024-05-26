package me.flyray.bsin.domain.domain;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import me.flyray.bsin.entity.BaseEntity;
import me.flyray.bsin.validate.AddGroup;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * 平台（租户,例：unionCash）;
 * @TableName crm_platform
 */

@Data
@TableName(value ="crm_platform")
public class Platform extends BaseEntity implements Serializable {

    /**
     * 平台名称
     */
    @NotBlank(message = "平台名称不能为空" ,groups = AddGroup.class)
    private String platformName;

    /**
     * 类型;1、内部 2、外部
     */
    private Integer type;

    /**
     * 状态;1、正常 2、注销
     */
    private Integer status;
    /**
     * 支付密码
     */
    private String txPassword;
    /**
     * 支付密码状态 0、未设置 1、已设置 2、锁定
     */
    private Integer txPasswordStatus;
    /**
     * 谷歌验证器秘钥
     */
    private String googleSecretKey;
    /**
     * 备注
     */
    private String comment;

    /**
     * 公司注册全称
     */
    private String registrationName;

    /**
     * 公司注册国家代码
     */
    private String registrationCntrCode;

    /**
     * 公司工商注册号
     */
    private String registrationNumber;

    /**
     * 公司类型;公司类型；1、有限责任公司 2、股份有限公司 3、合伙企业 4、个体工商户 5、个人独资企业
     */
    private Integer companyType;

    /**
     * 公司法人名称
     */
    private String legalPersonName;

    /**
     * 公司法人证件类型;1、身份证
     */
    private Integer legalPersonIdType;

    /**
     * 公司法人证件号
     */
    private String legalPersonIdno;

    /**
     * 公司营业执照
     */
    private String businessLicenceImg;

    /**
     * 注册日期
     */
    private String registrationDate;

    /**
     * 有效期
     */
    private String validityRegistrationDate;

    /**
     * 公司地址详细信息
     */
    private String addressLine;

    /**
     * 公司网址
     */
    private String netAddress;

    /**
     * 公司地址邮政编码
     */
    private String addressIndex;

    /**
     * 企业经营范围
     */
    private String businessScope;

    /**
     * 企业邮箱
     */
    private String corpEmail;

    /**
     * 租户ID
     */
    @NotBlank(message = "租户ID不能为空！", groups = AddGroup.class)
    private String tenantId;
}
