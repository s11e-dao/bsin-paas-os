package me.flyray.bsin.domain.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;


import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import me.flyray.bsin.domain.enums.CustomerType;
import me.flyray.bsin.validate.AddGroup;

/**
 * 
 * @TableName crm_customer_base
 */
@TableName(value ="crm_customer_base")
@Data
public class CustomerBase implements Serializable {
    /**
     * 客户号
     */
    @TableId
    private String customerNo;

    /**
     * 登录名称
     * 微信等第三方授权等于，用户名可以为空
     */
//    @NotBlank(message = "用户名不能为空！", groups = {AddGroup.class})
    private String username;

    /**
     * 真实姓名
     */
    private String realName;

    /**
     * 0、手机号 1、邮箱 2、QQ 3、微信 4、用户名 5、微博
     *@see me.flyray.bsin.enums.AuthMethod
     */
    private String authMethod;

    /**
     * 凭据，第三方标识，微信平台的openId
     */
    private String credential;

    /**
     * 微信平台：登录请求会话key
     */
    private String sessionKey;

    /**
     * 身份证号
     */
    private String idNumber;

    /**
     * 登录密码
     */
    private String password;

    /**
     * 昵称
     */
    private String nickname;


    /**
     * 简介
     */
    private String info;

    /**
     * 支付密码
     */
    private String txPassword;

    /**
     * 支付密码状态 0、正常 1、未设置 2、锁定
     */
    private String txPasswordStatus;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 邮箱
     */
    private String email;


    /**
     * 生日
     */
    private String birthday;

    /**
     * 头像
     */
    private String avatar;

    /**
     * 客户|租户|商户|代理商类型 0、个人客户 1、企业客户
     * @see CustomerType
     */
    private String type;


    /**
     * 性别 1、男 2、女
     */
    private String sex;

    /**
     * 客户描述
     */
    private String description;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 修改时间
     */
    private Date updateTime;

    /**
     * 逻辑删除 0、未删除 1、已删除
     */
    @TableLogic(
            value = "0",
            delval = "1"
    )
    private Integer delFlag;

    /**
     * 是否是白名单 0、否 1、是
     */
    private Integer vipFlag;

    /**
     * 租户id
     */
    @NotBlank(message = "租户ID不能为空！", groups = {AddGroup.class})
    private String tenantId;

    /**
     * 实名认证标识 0：未认证 1：已认证
     */
    private boolean certificationStatus;

    /**
     * conflux链上钱包地址
     */
    private String walletAddress;

    /**
     * conflux链上钱包密码
     */
    private String walletPrivateKey;

    /**
     * evm链上钱包地址
     */
    private String evmWalletAddress;

    /**
     * evm链上钱包密码
     */
    private String evmWalletPrivateKey;

    /**
     * conflux链Profile地址
     */
    private String profileAddress;

    /**
     * evm链Profile地址
     */
    private String evmProfileAddress;

    /**
     * 创建profile时创建的数字分身(用户)|品牌馆(商户)ID
     */
    private String copilotNo;
    /**
     *
     */
    @TableField(exist = false)
    private BigDecimal bcBalance;

    /**
     *
     */
    @TableField(exist = false)
    private BigDecimal bcAccumulatedBalance;

    /**
     * 商户数字积分余额
     */
    @TableField(exist = false)
    private BigDecimal dpBalance;

    /**
     * 人民币账户余额
     */
    @TableField(exist = false)
    private BigDecimal cnyBalance;


    /**
     * tba账户地址
     */
    @TableField(exist = false)
    private String tbaAddress;


    /**
     * 邀请码
     */
    private String inviteCode;

}