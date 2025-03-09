package me.flyray.bsin.domain.request;

import me.flyray.bsin.domain.entity.SysOrg;
import me.flyray.bsin.domain.entity.SysUser;
import lombok.*;
import lombok.experimental.SuperBuilder;
import me.flyray.bsin.mybatis.utils.Pagination;
import me.flyray.bsin.security.enums.BizRoleType;
import me.flyray.bsin.validate.QueryGroup;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Collection;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder(toBuilder = true)
public class SysUserDTO extends SysUser implements Serializable {


    /**
     * 岗位ID
     */
    private String postId;

    /**
     * 商户id
     */
    private String merchantNo;

    /**
     * 所属机构
     */
    private SysOrg org;

    /**
     * 应用ID
     */
    private String appId;

    /**
     * 应用角色ID
     */
    private String roleId;

    /**
     * 业务角色类型
     */
    private String bizRoleType;

    private Integer type;

    /**
     * 租户的应用类型
     * TODO 租户会存在多个顶级应用
     */
    private String tenantAppType;

    /**
     * 个人简介
     */
    private String description;

    @Valid
    @NotNull(message = "分页不能为空！", groups = QueryGroup.class)
    private Pagination pagination;

    /**
     * 查询开始时间
     */
    private String beginTime;

    /**
     * 查询结束时间
     */
    private String endTime;

    private List<String> userIds;


    private String orgName;


    private Collection<String> orgIds;


    private String tenantId;

    private String password;

    /**
     * 是否查询所有,默认不查
     */
    private Boolean selectAll = false;

    private String[] excludeUserIds;

}
