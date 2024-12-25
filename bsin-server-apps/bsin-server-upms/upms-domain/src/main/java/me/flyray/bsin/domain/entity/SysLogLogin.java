package me.flyray.bsin.domain.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 系统访问记录
 * @TableName sys_log_login
 */

@Data
@TableName(value ="sys_log_login")
public class SysLogLogin implements Serializable {

    /**
     * 访问ID
     */
    @TableId
    private String serialNo;

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
    private Integer status;

    /**
     * 访问时间
     */
    private Date loginTime;

    /**
     * 业务角色（1.运营平台 2.租户平台 3.商户 4.店铺 5.客户 99.无）
     */
    private Integer bizRoleType;

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

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

}