package me.flyray.bsin.domain.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import me.flyray.bsin.validate.AddGroup;

import org.springframework.format.annotation.DateTimeFormat;

import java.beans.Transient;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;


@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("sys_app")
public class SysApp implements Serializable {

    /**
     * 应用id
     */
    @TableId
    private String appId;

    /**
     * 应用编码
     */
    @NotBlank(message = "appCode不能为空！", groups = AddGroup.class)
    private String appCode;

    /**
     * 应用名称
     */
    @NotBlank(message = "appName不能为空！", groups = AddGroup.class)
    private String appName;

    /**
     * logo图片
     */
    private String logo;

    /**
     * url地址
     */
    private String url;

    /**
     * 主题 json字符串
     */
    private String theme;

    /**
     * 应用类型
     */
    private Integer type;

    /**
     *  租户应用类型
     */
//    annotate
    private Integer tenantAppType;

    /**
     * 前端语言
     */
    private Integer appLanguage;

    /**
     * 描述
     */
    private String remark;

    /**
     * 状态
     */
    private Integer status;

    /**
     * 创建人
     */
    private String createBy;

    /**
     * 创建时间
     */
    @JsonFormat(shape =JsonFormat.Shape.STRING,pattern ="yyyy-MM-dd HH:mm:ss",timezone ="GMT+8")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    /**
     * 修改人
     */
    private String updateBy;

    /**
     * 修改时间
     */
    @JsonFormat(shape =JsonFormat.Shape.STRING,pattern ="yyyy-MM-dd HH:mm:ss",timezone ="GMT+8")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private Date updateTime;

    /**
     * 逻辑删除
     */
    private Integer delFlag;

    /**
     * 是否是默认产品应用标识
     */
    private Integer baseFlag;

    /**
     * 业务角色类型
     */
    private String bizRoleType;

}
