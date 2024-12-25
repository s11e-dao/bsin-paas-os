package me.flyray.bsin.log.event;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 登录事件
 */

@Data
public class LoginInfoEvent implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 租户id
     */
    private String tenantId;

    /**
     * 用户账号
     */
    private String username;

    /**
     * 用户昵称
     */
    private String nickname;

    /**
     * 登录IP地址
     */
    private String ip;

    /**
     * 登录地点
     */
    private String loginLocation;

    /**
     * 浏览器类型
     */
    private String browser;

    /**
     * 操作系统
     */
    private String os;

    /**
     * 登录状态（1成功 0失败）
     */
    private String status;

    /**
     * 访问时间
     */
    private Date loginTime;

    /**
     * 业务角色（1.运营平台 2.租户平台 3.商户 4.店铺 5.客户 99.无）
     */
    private String bizRoleType;

    /**
     * 业务角色id
     */
    private String bizRoleTypeNo;

    /**
     * 提示消息
     */
    private String msg;

    /**
     * 登录类型（1- 登录  2-登出 ）
     */
    private Integer type;

    /**
     * 应用id
     */
    private String appId;

    /**
     * 来源
     */
    private String source;

    /**
     * 参数
     */
    private Object[] args;

    /**
     * 提示消息
     */
    private String message;

}
