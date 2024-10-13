package me.flyray.bsin.domain.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.util.Date;

import javax.validation.constraints.NotBlank;

import lombok.Data;

/**
 * 系统接入的产品应用信息表：平台和商户接入的应用
 */
@Data
@TableName(value ="crm_biz_role_app")
public class BizRoleApp implements Serializable {

    /**
     * 序号
     */
    @TableId
    private String serialNo;

    /**
     * 租户号
     */
    private String tenantId;

    /**
     * 业务角色类型：见枚举
     */
    private String bizRoleType;

    /**
     * 商户号
     */
    private String bizRoleTypeNo;

    /**
     * 应用名称
     */
    @NotBlank(message = "app名称不能为空！")
    private String appName;

    /**
     * 应用ID：自动生成
     * 微信公众号的appID：公众号通过此ID检索公众号参数 设置企业微信的corpId：企业微信通过此ID检索公众号参数
     */
    private String appId;

    /** 企业号ID，数据库统一字段为appId，此字段废弃 */
    private String corpId;

    /** 设置企业微信应用的AgentId */
    private Integer agentId;

    /**
     * 1: app应用 2：接口 3：小程序 4:mp(公众号服务订阅号) 5:miniapp(小程序) 6:cp(企业号|企业微信) 7:pay(微信支付) 8:open(微信开放平台) 9:wechat(个人微信) 10:menu(菜单模版)
     */
    private String appType;
    /**
     * 应用密钥：自动生成
     */
    private String appSecret;

    /** 企业微信/微信公众号的EncodingAESKey */
    private String aesKey;

    /** 微信公众号/企业微信/小程序的token */
    private String token;

    /**
     * 通知地址
     */
    private String notifyUrl;

    /**
     * 应用状态: 0 待审核 1 审核通过
     */
    private String status;

    /**
     * 应用描述
     */
    @NotBlank
    private String appDescription;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 修改时间
     */
    private Date updateTime;

    /**
     * 逻辑删除
     */
    @TableLogic(
            value = "0",
            delval = "1"
    )
    private Integer delFlag;

}