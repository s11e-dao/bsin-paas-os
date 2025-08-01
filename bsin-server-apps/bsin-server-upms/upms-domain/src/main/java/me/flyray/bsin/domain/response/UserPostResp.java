package me.flyray.bsin.domain.response;

import me.flyray.bsin.domain.entity.SysUser;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class UserPostResp extends SysUser implements Serializable{

    /**
     * 用户id
     */
    private String userId;

    /**
     * 用户名
     */
    private String username;

    /**
     * 密码
     */
    private String password;

    /**
     * 状态 0，在岗 1，离职
     */
    private Integer status;

    /**
     * 所属机构id
     */
    private String orgId;

    /**
     * 所属租户id
     */
    private String tenantId;

    /**
     * 昵称
     */
    private String nickname;

    /**
     * 电话号码
     */
    private String phone;

    /**
     * 用户头像
     */
    private String avatar;

    /**
     * 电子邮箱
     */
    private String email;

    /**
     * 逻辑删除 0、未删除 1、已删除
     */
    private Integer delFlag;

    /**
     * 创建人
     */
    private String createBy;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 修改人
     */
    private String updateBy;

    /**
     * 修改时间
     */
    private Date updateTime;

    /**
     * 用户类型 0、普通用户 1、超级管理员
     */
    private Integer type;

    /**
     * 岗位id
     */
    private String postId;

    /**
     * 岗位名称
     */
    private String postName;

    /**
     * 个人简介
     */
    private String description;

}
