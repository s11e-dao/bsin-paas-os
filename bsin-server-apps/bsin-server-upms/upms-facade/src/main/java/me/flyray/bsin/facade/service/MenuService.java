package me.flyray.bsin.facade.service;


import org.springframework.validation.annotation.Validated;

import java.util.List;

import javax.validation.Valid;

import me.flyray.bsin.domain.entity.SysMenu;
import me.flyray.bsin.domain.entity.SysRole;
import me.flyray.bsin.domain.request.SysMenuDTO;
import me.flyray.bsin.domain.response.MenuTree;
import me.flyray.bsin.validate.AddGroup;

@Validated
public interface MenuService {

    /**
     * 添加菜单
     */
    @Validated(AddGroup.class)
    SysMenu add(@Valid SysMenuDTO sysMenu);

    /**
     * 删除菜单
     */
    void delete(String menuId);

    /**
     * 编辑菜单
     */
    SysMenu edit(SysMenu sysMenu);

    /**
     * 返回菜单树
     */
    List<MenuTree> getMenuTree(SysRole sysRole);


    /**
     * 根据用户id返回菜单权限树
     */
    List<MenuTree> getUserMenuTree(String appCode);

    /**
     * 根据用户id返回菜单权限树
     */
    List<String> getMenusByAppIdAndRoleId(String appId, String roleId);
}
