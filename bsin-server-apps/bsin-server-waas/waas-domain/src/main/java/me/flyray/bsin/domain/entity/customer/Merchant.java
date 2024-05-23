package me.flyray.bsin.domain.entity.customer;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import me.flyray.bsin.entity.BaseEntity;
import me.flyray.bsin.validate.AddGroup;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * 商户;
 * @TableName crm_merchant
 */
@Data
@TableName(value ="crm_merchant")
public class Merchant extends BaseEntity implements Serializable {
    /**
     * 商户名称
     */
    @NotBlank(message = "商户名称不能为空！", groups = AddGroup.class)
    private String merchantName;

    /**
     * 类型;1.平台直属商户 2.代理商商户 3.普通商户
     */
    @NotBlank(message = "商户类型不能为空！", groups = AddGroup.class)
    private Integer type;

    /**
     * 状态;1.正常 2.注销
     */
    private Integer status;

    /**
     * 步骤;0、初始步骤 1.企业信息已提交 2.证件信息已提交 3.钱包账户已开通
     */
    private Integer step;

    /**
     * 文件上传时间
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private String uploadFileTime;

    /**
     * 审核状态;0.资料待完善 1.等待审核 2.审核通过 3.审核拒绝 4.再次提审
     */
    private Integer auditStatus;

    /**
     * 审核时间
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private String auditTime;

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
     * 公司类型
     */
    private Integer companyType;

    /**
     * 公司法人名称
     */
    private String legalPersonName;

    /**
     * 公司法人证件类型
     */
    private Integer legalPersonIdType;

    /**
     * 公司法人身份证号
     */
    private String legalPersonIdno;

    /**
     * 公司营业执照
     */
    private String businessLicenceImg;

    /**
     * 注册日期
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private String registrationDate;

    /**
     * 有效期
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd")
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
    private String busunessScope;

    /**
     * 企业邮箱
     */
    private String corpEmail;

    /**
     * 公司类型;1:欧盟区；2:其他国家；3中国大陆
     */
    private Integer corpType;

    /**
     * 租户id
     */
    private String tenantId;
}