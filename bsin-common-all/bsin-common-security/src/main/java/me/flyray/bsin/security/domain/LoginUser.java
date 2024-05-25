package me.flyray.bsin.security.domain;

import java.io.Serializable;

import lombok.Data;

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
     * 用户ID
     */
    private String customerNo;


    /**
     * 手机号
     */
    private String phone;


    /**
     * 租户ID
     */
    private String tenantId;


    /**
     * 应用ID
     */
    private String appId;


    /**
     * 商户ID
     */
    private String merchantNo;


    /**
     * 用户类型
     */
    private String customerType;

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
     */
    private Integer bizRoleType;

    /**
     * 用户业务角色类型编号
     */
    private String bizRoleTypeNo;

    /**
     * 用户key值
     */
    private String userKey;

}
