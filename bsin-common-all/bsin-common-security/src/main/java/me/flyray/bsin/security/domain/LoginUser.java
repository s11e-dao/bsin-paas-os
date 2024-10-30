package me.flyray.bsin.security.domain;

import java.io.Serializable;

import lombok.Data;
import me.flyray.bsin.security.enums.BizRoleType;
import me.flyray.bsin.security.enums.TenantMemberModel;

/**
 * @author HLW
 **/
@Data
public class LoginUser implements Serializable {


    private static final long serialVersionUID = 1L;

    /**
     * 管理平台用户ID
     */
    private String userId;


    /**
     * 管理平台用户名
     */
    private String username;


    /**
     * C端客户ID
     */
    private String customerNo;

    /**
     * C端客户类型： 个人客户 企业客户
     */
    private String customerType;

    /**
     * 手机号
     */
    private String phone;


    /**
     * 租户ID
     */
    private String tenantId;

    /**
     * platform: 接入平台的会员模型：1 租户直属会员 2 商户直属会员
     * @see TenantMemberModel
     */
    private String memberModel;

    /**
     * 租户ID
     */
    private String orgId;

    /**
     * 应用ID
     */
    private String appId;

    /**
     * 租户直属商户ID
     */
    private String tenantMerchantNo;

    /**
     * 商户ID
     */
    private String merchantNo;

    /**
     * 商户总店ID
     */
    private String merchantStoreNo;

    /**
     * 店铺ID
     */
    private String storeNo;

    /**
     * 创建者
     */
    private String createBy;


    /**
     * 更新者
     */
    private String updateBy;


    /**
     * token
     */
    private String token;

    /**
     * 登录用户ip
     */
    private String ip;

    /**
     * 企业微信的企业标识（Corporate ID）
     */
    private String corpId;

    /**
     * 企业微信的企业名称
     */
    private String corpName;

    /**
     * 用户类型
     */
    private String userType;

    /**
     * 用户业务角色类型
     * @see BizRoleType
     */
    private String bizRoleType;

    /**
     * 用户业务角色类型编号
     */
    private String bizRoleTypeNo;

    /**
     * 用户key值
     */
    private String userKey;

}
