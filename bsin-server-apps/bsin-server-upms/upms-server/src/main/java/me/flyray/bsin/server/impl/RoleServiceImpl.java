package me.flyray.bsin.server.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import org.apache.shenyu.client.apache.dubbo.annotation.ShenyuDubboService;
import org.apache.shenyu.client.apidocs.annotations.ApiDoc;
import org.apache.shenyu.client.apidocs.annotations.ApiModule;
import org.apache.shenyu.client.dubbo.common.annotation.ShenyuDubboClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

import me.flyray.bsin.constants.ResponseCode;
import me.flyray.bsin.domain.entity.SysRole;
import me.flyray.bsin.domain.request.SysRoleDTO;
import me.flyray.bsin.exception.BusinessException;
import me.flyray.bsin.facade.service.RoleService;
import me.flyray.bsin.infrastructure.mapper.RoleMapper;
import me.flyray.bsin.infrastructure.mapper.RoleMenuMapper;
import me.flyray.bsin.mybatis.utils.Pagination;
import me.flyray.bsin.security.contex.LoginInfoContextHelper;
import me.flyray.bsin.utils.BsinSnowflake;

@ShenyuDubboService(path = "/role")
@ApiModule(value = "role")
@Transactional(rollbackFor = Exception.class)
@Service
public class RoleServiceImpl implements RoleService {

    @Autowired
    private RoleMapper roleMapper;
    @Autowired
    private RoleMenuMapper roleMenuMapper;

    /**
     * 添加角色
     */
    @ApiDoc(desc = "add")
    @ShenyuDubboClient("/add")
    @Override
    public SysRole add(SysRole role) {
        String tenantId = LoginInfoContextHelper.getTenantId();
        // 判断角色编码是否已存在
        SysRole roleInfo = roleMapper.getRoleInfoByRoleCode(role.getRoleCode(), role.getAppId());
        if (roleInfo != null) {
            throw new BusinessException(ResponseCode.ROLE_CODE_EXISTS);
        }
        role.setRoleId(BsinSnowflake.getId());
        role.setType(3);
        role.setTenantId(tenantId);
        roleMapper.insert(role);
        return role;
    }

    /**
     * 删除角色
     */
    @ApiDoc(desc = "delete")
    @ShenyuDubboClient("/delete")
    @Override
    public void delete(String roleId) {
        roleMapper.deleteById(roleId);
    }

    /**
     * 编辑角色
     */
    @ApiDoc(desc = "edit")
    @ShenyuDubboClient("/edit")
    @Override
    public SysRole edit(SysRole role) {
        // 判断角色编码是否已存在
        SysRole roleInfo = roleMapper.getRoleInfoByRoleCode(role.getRoleCode(), role.getAppId());
        if (roleInfo != null && !roleInfo.getRoleCode().equals(role.getRoleCode())) {
            throw new BusinessException(ResponseCode.ROLE_CODE_EXISTS);
        }
        role.setUpdateBy(LoginInfoContextHelper.getCustomerNo());
        role.setUpdateTime(new Date());
        roleMapper.updateById(role);
        return role;
    }

    /**
     * 通过角色编码或者角色名称查询当前租户下应用的角色列表（appId必填）
     */
    @ApiDoc(desc = "getPageList")
    @ShenyuDubboClient("/getPageList")
    @Override
    public IPage<?> getPageList(SysRoleDTO roleDTO) {
        String tenantId = LoginInfoContextHelper.getTenantId();
        String appId = roleDTO.getAppId();
        if (appId == null || appId.equals("")) {
            throw new BusinessException(ResponseCode.ID_NOT_ISNULL);
        }
        String roleCode = roleDTO.getRoleCode();
        String roleName = roleDTO.getRoleName();
        Pagination pagination = roleDTO.getPagination();
        Page<SysRole> page = new Page<>(pagination.getPageNum(), pagination.getPageSize());
        IPage<SysRole> pageList = roleMapper.selectPageList(page, roleCode, roleName, appId, tenantId);
        return pageList;
    }

    /**
     * 授予菜单权限
     */
    @ApiDoc(desc = "authorizeMenus")
    @ShenyuDubboClient("/authorizeMenus")
    @Override
    public void authorizeMenus(String appId, String roleId, List<String> menus) {
        roleMenuMapper.unAuthorizeMenusByRoleId(roleId);
        if (menus.size() < 1) {
            return;
        }
        roleMenuMapper.authorizeMenus(appId, roleId, menus);
    }

    /**
     * 通过应用id查询角色列表
     */
    @ApiDoc(desc = "getListByAppId")
    @ShenyuDubboClient("/getListByAppId")
    @Override
    public List<SysRole> getListByAppId(String appId) {
        String tenantId = LoginInfoContextHelper.getTenantId();
        return roleMapper.selectListByAppId(appId, tenantId);
    }
}
