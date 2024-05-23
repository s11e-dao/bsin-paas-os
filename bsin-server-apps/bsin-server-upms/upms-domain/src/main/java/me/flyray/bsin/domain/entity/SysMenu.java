package me.flyray.bsin.domain.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import me.flyray.bsin.validate.AddGroup;

import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;

import javax.validation.constraints.NotBlank;


@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("sys_menu")
public class SysMenu implements Serializable {

    /**
     * 雪花算法生成唯一菜单id
     */
    @TableId
    private String menuId;

    /**
     * 菜单编码
     */
    @NotBlank(message = "menuCode不能为空！", groups = AddGroup.class)
    private String menuCode;

    /**
     * 菜单名称
     */
    @NotBlank(message = "menuName不能为空！", groups = AddGroup.class)
    private String menuName;

    /**
     * 权限标识
     */
    private String permission;

    /**
     * 应用id
     */
    @NotBlank(message = "appId不能为空！", groups = AddGroup.class)
    private String appId;

    /**
     * 应用功能ID
     */
    private String appFunctionId;

    /**
     * 前端路由
     */
    @NotBlank(message = "path不能为空！", groups = AddGroup.class)
    private String path;

    /**
     * 父级id
     */
    @NotBlank(message = "parentId不能为空！", groups = AddGroup.class)
    private String parentId;

    /**
     * 菜单图标
     */
    private String icon;

    /**
     * 排序值
     */
    private Integer sort;
    /**
     * 菜单状态
     */
    private Integer status;

    /**
     * 菜单类型：菜单、按钮
     * 菜单类型 0、主菜单 1、菜单 2、按钮 99、模板菜单
     */
    private Integer type;

    /**
     * 描述
     */
    private String remark;

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
    @TableLogic(value = "0", delval = "1")
    private Integer delFlag;
}
