package me.flyray.bsin.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import me.flyray.bsin.entity.BaseEntity;
import me.flyray.bsin.validate.AddGroup;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * 平台（租户,例：jiujiu）;
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
     * 平台登录名称
     */
    @NotBlank(message = "登录名称不能为空" ,groups = AddGroup.class)
    private String username;

    /**
     *  商户类型：1、个人客户 2、企业客户
     */
    private Integer type;

    /**
     *
     */
    private String category;

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
    private String description;

    /**
     * 公司工商注册号
     */
    private String businessNo;

    /**
     * 公司法人名称
     */
    private String legalPersonName;

    /**
     * 公司法人证件类型;1、身份证
     */
    private Integer legalPersonCredType;

    /**
     * 公司法人证件号
     */
    private String legalPersonCredNo;

    /**
     * 公司营业执照
     */
    private String businessLicenceImg;

    /**
     * 公司地址详细信息
     */
    private String platformAddress;

    /**
     * 公司网址
     */
    private String netAddress;

    /**
     * 企业经营范围
     */
    private String businessScope;

    /**
     * 租户ID
     */
    @NotBlank(message = "租户ID不能为空！", groups = AddGroup.class)
    private String tenantId;

}
