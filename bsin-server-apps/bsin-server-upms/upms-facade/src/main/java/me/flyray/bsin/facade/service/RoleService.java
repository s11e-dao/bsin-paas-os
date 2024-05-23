package me.flyray.bsin.facade.service;


import com.baomidou.mybatisplus.core.metadata.IPage;

import org.springframework.validation.annotation.Validated;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;

import me.flyray.bsin.domain.entity.SysRole;
import me.flyray.bsin.domain.request.SysRoleDTO;
import me.flyray.bsin.validate.AddGroup;


@Validated
public interface RoleService {

    /**
     * 添加角色
     */
    @Validated(AddGroup.class)
    SysRole add(@Valid SysRole role);

    /**
     * 删除角色
     */
    void delete(String roleId);

    /**
     * 编辑角色
     */
    SysRole edit(SysRole role);

    /**
     * 查询角色
     */
    IPage<?> getPageList(SysRoleDTO roleDTO);

    /**
     * 添加菜单权限
     */
    void authorizeMenus(String appId, String roleId, List<String> menus);

    /**
     * 根据应用id查询角色列表
     */
    List<SysRole> getListByAppId(@NotBlank(message = "appId不能为空！") String appId);

}
