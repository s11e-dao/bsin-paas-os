package me.flyray.bsin.domain.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;

import org.springframework.format.annotation.DateTimeFormat;

import java.beans.Transient;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;

import javax.validation.constraints.NotBlank;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import me.flyray.bsin.validate.AddGroup;

@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("sys_tenant")
public class SysTenant implements Serializable {

    /**
     * 租户id
     */
    @TableId
    private String tenantId;

    /**
     * 租户编号
     */
    @NotBlank(message = "tenantCode不能为空！", groups = AddGroup.class)
    private String tenantCode;

    /**
     * 应用租户类型
     */
    private String productCode;

    /**
     * 租户名
     */
    @NotBlank(message = "tenantName不能为空！", groups = AddGroup.class)
    private String tenantName;

    /**
     * 描述
     */
    private String remark;

    /**
     * 小图标
     */
    private String logo;

    /**
     * 租户类型 0、超级租户(顶级平台租户) 1、普通租户
     */
    private Integer type;

    /**
     * 租户状态 0、正常 1、冻结
     */
    private Integer status;

    /**
     * 创建时间
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    /**
     * 修改时间
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;

    /**
     * 逻辑删除
     */
    private Integer delFlag;

    @TableField(exist = false)
    private String customerNo;

    private static final long serialVersionUID = 1L;
}
