package me.flyray.bsin.domain.entity;

import java.io.Serializable;
import java.util.Date;

/**
 * 
 * @TableName sms_log
 */
public class SmsLog implements Serializable {
    /**
     * 序号
     */
    private String serialNo;

    /**
     * 手机号
     */
    private String mobile;

    /**
     * 1、成功 2、失败
     */
    private Integer status;

    /**
     * 1、注册验证码 2、找回密码
     */
    private Integer logType;

    /**
     * 客户端ip
     */
    private String ip;

    /**
     * 标题
     */
    private String title;

    /**
     * 短信内容
     */
    private String content;

    /**
     * 1、InfoBip
     */
    private Integer channel;

    /**
     * 通道返回结果
     */
    private String rspMsg;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 创建人
     */
    private String createBy;

    private static final long serialVersionUID = 1L;

    /**
     * 序号
     */
    public String getSerialNo() {
        return serialNo;
    }

    /**
     * 序号
     */
    public void setSerialNo(String serialNo) {
        this.serialNo = serialNo;
    }

    /**
     * 手机号
     */
    public String getMobile() {
        return mobile;
    }

    /**
     * 手机号
     */
    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    /**
     * 1、成功 2、失败
     */
    public Integer getStatus() {
        return status;
    }

    /**
     * 1、成功 2、失败
     */
    public void setStatus(Integer status) {
        this.status = status;
    }

    /**
     * 1、注册验证码 2、找回密码
     */
    public Integer getLogType() {
        return logType;
    }

    /**
     * 1、注册验证码 2、找回密码
     */
    public void setLogType(Integer logType) {
        this.logType = logType;
    }

    /**
     * 客户端ip
     */
    public String getIp() {
        return ip;
    }

    /**
     * 客户端ip
     */
    public void setIp(String ip) {
        this.ip = ip;
    }

    /**
     * 标题
     */
    public String getTitle() {
        return title;
    }

    /**
     * 标题
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * 短信内容
     */
    public String getContent() {
        return content;
    }

    /**
     * 短信内容
     */
    public void setContent(String content) {
        this.content = content;
    }

    /**
     * 1、InfoBip
     */
    public Integer getChannel() {
        return channel;
    }

    /**
     * 1、InfoBip
     */
    public void setChannel(Integer channel) {
        this.channel = channel;
    }

    /**
     * 通道返回结果
     */
    public String getRspMsg() {
        return rspMsg;
    }

    /**
     * 通道返回结果
     */
    public void setRspMsg(String rspMsg) {
        this.rspMsg = rspMsg;
    }

    /**
     * 创建时间
     */
    public Date getCreateTime() {
        return createTime;
    }

    /**
     * 创建时间
     */
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getCreateBy() {
        return createBy;
    }

    public void setCreateBy(String createBy) {
        this.createBy = createBy;
    }

    @Override
    public boolean equals(Object that) {
        if (this == that) {
            return true;
        }
        if (that == null) {
            return false;
        }
        if (getClass() != that.getClass()) {
            return false;
        }
        SmsLog other = (SmsLog) that;
        return (this.getSerialNo() == null ? other.getSerialNo() == null : this.getSerialNo().equals(other.getSerialNo()))
            && (this.getMobile() == null ? other.getMobile() == null : this.getMobile().equals(other.getMobile()))
            && (this.getStatus() == null ? other.getStatus() == null : this.getStatus().equals(other.getStatus()))
            && (this.getLogType() == null ? other.getLogType() == null : this.getLogType().equals(other.getLogType()))
            && (this.getIp() == null ? other.getIp() == null : this.getIp().equals(other.getIp()))
            && (this.getTitle() == null ? other.getTitle() == null : this.getTitle().equals(other.getTitle()))
            && (this.getContent() == null ? other.getContent() == null : this.getContent().equals(other.getContent()))
            && (this.getChannel() == null ? other.getChannel() == null : this.getChannel().equals(other.getChannel()))
            && (this.getRspMsg() == null ? other.getRspMsg() == null : this.getRspMsg().equals(other.getRspMsg()))
            && (this.getCreateTime() == null ? other.getCreateTime() == null : this.getCreateTime().equals(other.getCreateTime()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getSerialNo() == null) ? 0 : getSerialNo().hashCode());
        result = prime * result + ((getMobile() == null) ? 0 : getMobile().hashCode());
        result = prime * result + ((getStatus() == null) ? 0 : getStatus().hashCode());
        result = prime * result + ((getLogType() == null) ? 0 : getLogType().hashCode());
        result = prime * result + ((getIp() == null) ? 0 : getIp().hashCode());
        result = prime * result + ((getTitle() == null) ? 0 : getTitle().hashCode());
        result = prime * result + ((getContent() == null) ? 0 : getContent().hashCode());
        result = prime * result + ((getChannel() == null) ? 0 : getChannel().hashCode());
        result = prime * result + ((getRspMsg() == null) ? 0 : getRspMsg().hashCode());
        result = prime * result + ((getCreateTime() == null) ? 0 : getCreateTime().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", serialNo=").append(serialNo);
        sb.append(", mobile=").append(mobile);
        sb.append(", status=").append(status);
        sb.append(", logType=").append(logType);
        sb.append(", ip=").append(ip);
        sb.append(", title=").append(title);
        sb.append(", content=").append(content);
        sb.append(", channel=").append(channel);
        sb.append(", rspMsg=").append(rspMsg);
        sb.append(", createTime=").append(createTime);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}