package me.flyray.bsin.infrastructure.mapper;


import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

import me.flyray.bsin.domain.entity.SysMenu;

@Repository
@Mapper
public interface MenuMapper {

    void insert(SysMenu record);

    SysMenu getMenuInfoByMenuCode(@Param("appId") String appId,@Param("menuCode") String menuCode);

    void deleteById(String menuId);

    void updateById(SysMenu record);

    List<SysMenu> selectListByAppIdAndRoleId(@Param("appId") String appId , @Param("roleId") String roleId);

    List<SysMenu> selectListByAppId(String appId );

    List<SysMenu> selectChild(String parentId );

    void deleteByAppId(String appId);

    List<SysMenu> getPermissionMenuTemplate(String appId);

    List<SysMenu> selectMenuButtonByMenuId(String menuId);

    List<SysMenu> selectListByMenuIds(@Param("menuIds") List<String> menuIds);

    List<String> selectMenuIdsByAppIdAndRoleId(@Param("appId") String appId , @Param("roleId") String roleId);

    String selectTopMenuId(String appCode);

    SysMenu selectOneByMenuId(@Param("menuId") String menuId);

    List<String> selectListByAppFunctionIds(@Param("appFunctionIds") List<String> appFunctionIds);

}
