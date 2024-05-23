package me.flyray.bsin.domain.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.experimental.SuperBuilder;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.validation.constraints.NotBlank;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import me.flyray.bsin.validate.AddGroup;


@Data
@EqualsAndHashCode(callSuper = false)
@TableName("sys_user")
@NoArgsConstructor
@SuperBuilder(toBuilder = true)
public class SysUser implements Serializable {

    /**
     * 用户id
     */
    @TableId
    private String userId;

    /**
     * 用户名
     */
    @NotBlank(message = "用户名不能为空！", groups = AddGroup.class)
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

    @TableField(exist = false)
    private SysOrg org;

    @TableField(exist = false)
    private List<SysRole> roles;

    @TableField(exist = false)
    private List<String> roleIds;

    @TableField(exist = false)
    private String appId;

    /**
     * 所属租户id
     */
    private String tenantId;

    /**
     * 昵称/姓名
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
     * 店铺id
     */
    @TableField(exist = false)
    private String storeId;

    /**
     * 性别  0、女  1、男
     */
    private Integer sex;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    /**
     * 修改人
     */
    private String updateBy;

    /**
     * 等级名称
     */
    @TableField(exist = false)
    private String gradeName;

    /**
     * 等级序列号
     */
    @TableField(exist = false)
    private String gradeNo;

    @TableField(exist = false)
    private List<SysPost> postList;

    @TableField(exist = false)
    private List<String> postIds;
    /**
     * 修改时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;

    /**
     * 用户类型 0、普通用户 1、超级管理员、2租户商户用户
     */
    private Integer type;

    @TableField(exist = false)
    private Integer LoginType;

    @TableField(exist = false)
    private String orgName;

    /**
     * 学分
     */
    @TableField(exist = false)
    private Integer credits;

    /**
     * 证书数量
     */
    @TableField(exist = false)
    private Integer certificateNum;

    /**
     * 排行序号
     */
    @TableField(exist = false)
    private Integer rankNum;

    public SysUser(String userId, String username, String password, String orgId, String tenantId) {
        this.username = username;
        this.password = password;
        this.orgId = orgId;
        this.tenantId = tenantId;
        this.userId = userId;
    }

}
