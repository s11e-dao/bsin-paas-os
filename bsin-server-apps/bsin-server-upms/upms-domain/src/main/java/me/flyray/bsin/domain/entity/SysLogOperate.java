package me.flyray.bsin.domain.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 操作日志记录
 * @TableName sys_log_oper
 */

@Data
@TableName(value ="sys_log_oper")
public class SysLogOperate implements Serializable {

    /**
     * 日志主键
     */
    @TableId
    private String serialNo;

    /**
     * 租户id
     */
    private String tenantId;

    /**
     * 方法标题
     */
    private String methodTitle;

    /**
     * 方法
     */
    private String method;

    /**
     * 请求方式
     */
    private String requestMethod;

    /**
     * 操作者渠道（0其它 1后台用户 2手机端用户）
     */
    private Integer operatorChannel;

    /**
     * 操作人员
     */
    private String operBy;

    /**
     * 请求URL
     */
    private String requestUrl;

    /**
     * 主机地址
     */
    private String operIp;

    /**
     * 操作地点
     */
    private String operLocation;

    /**
     * 请求参数
     */
    private Object inputParam;

    /**
     * 响应参数
     */
    private String outputParam;

    /**
     * 操作状态（0正常 1异常）
     */
    private Integer status;

    /**
     * 错误消息
     */
    private String errorMsg;

    /**
     * 操作时间
     */
    private Date operTime;

    /**
     * 业务角色（1.运营平台 2.租户平台 3.商户 4.店铺 5.客户 99.无）
     */
    private Integer bizRoleType;

    /**
     * 业务角色id
     */
    private String bizRoleTypeNo;

    /**
     * 消耗时间（单位：毫秒）
     */
    private Long costTime;

    /**
     * 浏览器类型
     */
    private String browser;

    /**
     * 操作系统
     */
    private String os;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

}