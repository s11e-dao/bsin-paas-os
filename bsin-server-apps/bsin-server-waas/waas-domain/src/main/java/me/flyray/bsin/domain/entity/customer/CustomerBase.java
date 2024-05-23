package me.flyray.bsin.domain.entity.customer;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import me.flyray.bsin.entity.BaseEntity;

import java.io.Serializable;

/**
 * 
 * @TableName crm_customer_base
 */
@Data
@TableName(value ="crm_customer_base")
public class CustomerBase extends BaseEntity implements Serializable {

    /**
     * 0、手机号 1、邮箱 2、QQ 3、微信4、用户名 5、微博  第三方登录获取
     */
    private String authMethod;

    /**
     * 凭据，第三方标识
     */
    private String credential;

    /**
     * 登录用户名：注册设置
     */
    private String username;

    /**
     * 登录密码：注册设置
     */
    private String password;

    /**
     * 真实姓名：实名认证设置
     */
    private String realName;

    /**
     * 身份证号：实名认证设置
     */
    private String idNumber;

    /**
     * 昵称：小程序设置
     */
    private String nickname;

    /**
     * 支付密码：实名认证设置
     */
    private String txPassword;

    /**
     * 支付密码状态 0、未设置 1、已经设置 2、锁定
     */
    private String txPasswordStatus;

    /**
     * 手机号：实名认证设置|小程序设置
     */
    private String phone;

    /**
     * 邮箱：小程序设置
     */
    private String email;

    /**
     * 头像：小程序设置
     */
    private String avatar;

    /**
     * 客户类型 0、个人客户 1、租户商家客户 2、租户(dao)客户 3、顶级平台商家客户
     */
    private String type;

    /**
     * 是否是白名单 0、否 1、是
     */
    private Boolean vipFlag;

    /**
     * 实名认证标识 0：未认证 1：已认证
     */
    private Boolean certificationStatus;

    /**
     * 链上钱包地址:实名认证后生成
     */
    private String walletAddress;

    /**
     * 链上钱包密码:实名认证设置
     */
    private String walletPrivateKey;

    /**
     * evm预留链上钱包地址:实名认证后生成
     */
    private String evmWalletAddress;

    /**
     * evm预留链上钱包密码:实名认证设置
     */
    private String evmWalletPrivateKey;

    /**
     * 介绍
     */
    private String description;

    /**
     * 生日
     */
    private String birthday;

    /**
     * 性别：1：男 2：女
     */
    private String sex;

    /**
     * 简介
     */
    private String info;

    /**
     * 邀请码
     */
    private String inviteCode;

    /**
     * 谷歌验证器token
     */
    private String googleToken;

    /**
     * Profile合约地址:创建数字分身时设置
     */
    private String profileAddress;

    /**
     * evm预留链上Profile合约地址:创建数字分身时设置
     */
    private String evmProfileAddress;

    /**
     * 创建profile时创建的数字分身(用户)|品牌馆(商户)ID
     */
    private String copilotNo;

    /**
     * 租户id
     */
    private String tenantId;
}