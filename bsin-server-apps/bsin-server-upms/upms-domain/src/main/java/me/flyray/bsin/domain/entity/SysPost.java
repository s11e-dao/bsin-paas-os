package me.flyray.bsin.domain.entity;


import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import me.flyray.bsin.validate.AddGroup;

import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("sys_post")
public class SysPost implements Serializable {


    /**
     * 商户id
     */
    private String merchantId;
    /**
     * 岗位id
     */
    @TableId
    private String postId;

    /**
     * 岗位编号
     */
    @NotBlank(message = "postCode不能为空！", groups = AddGroup.class)
    private String postCode;

    /**
     * 岗位名称
     */
    @NotBlank(message = "岗位名称不能为空", groups = AddGroup.class)
    private String postName;

    /**
     * 岗位排序
     */
    private Integer sort;

    /**
     * 状态 0、启用 1、停用
     */
    private Integer status;

    /**
     * 描述
     */
    private String remark;

    /**
     * 创建者
     */
    private String createBy;

    /**
     * 创建时间
     */
    @JsonFormat(shape =JsonFormat.Shape.STRING,pattern ="yyyy-MM-dd HH:mm:ss",timezone ="GMT+8")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    /**
     * 更新者
     */
    private String updateBy;

    /**
     * 更新时间
     */
    @JsonFormat(shape =JsonFormat.Shape.STRING,pattern ="yyyy-MM-dd HH:mm:ss",timezone ="GMT+8")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private Date updateTime;

    /**
     * 逻辑删除 0、未删除 1、已删除
     */
    private Integer delFlag;

    /**
     * 租户id
     */
    private String tenantId;

    public SysPost(String postId, String postCode, String postName, String tenantId) {
        this.postId = postId;
        this.postCode = postCode;
        this.postName = postName;
        this.tenantId = tenantId;
    }
}
