package me.flyray.bsin.server.impl;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import me.flyray.bsin.constants.ResponseCode;
import me.flyray.bsin.domain.entity.SysApp;
import me.flyray.bsin.domain.entity.SysAppFunction;
import me.flyray.bsin.domain.entity.SysTenant;
import me.flyray.bsin.domain.request.SysAppDTO;
import me.flyray.bsin.domain.response.AppResp;
import me.flyray.bsin.exception.BusinessException;
import me.flyray.bsin.facade.enums.TenantOrgAppType;
import me.flyray.bsin.facade.service.AppService;
import me.flyray.bsin.infrastructure.mapper.AppFunctionMapper;
import me.flyray.bsin.infrastructure.mapper.AppMapper;
import me.flyray.bsin.infrastructure.mapper.MenuMapper;
import me.flyray.bsin.infrastructure.mapper.OrgAppMapper;
import me.flyray.bsin.infrastructure.mapper.RoleMapper;
import me.flyray.bsin.infrastructure.mapper.RoleMenuMapper;
import me.flyray.bsin.infrastructure.mapper.TenantAppMapper;
import me.flyray.bsin.infrastructure.mapper.TenantMapper;
import me.flyray.bsin.mybatis.utils.Pagination;
import me.flyray.bsin.security.contex.LoginInfoContextHelper;
import me.flyray.bsin.server.biz.AppBiz;

import lombok.extern.slf4j.Slf4j;
import me.flyray.bsin.utils.BsinSnowflake;

import org.apache.commons.lang3.StringUtils;
import org.apache.shenyu.client.apache.dubbo.annotation.ShenyuDubboService;
import org.apache.shenyu.client.apidocs.annotations.ApiDoc;
import org.apache.shenyu.client.apidocs.annotations.ApiModule;
import org.apache.shenyu.client.dubbo.common.annotation.ShenyuDubboClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.Date;
import java.util.List;


@ShenyuDubboService(path = "/app", timeout = 6000)
@ApiModule(value = "app")
@Service
@Slf4j
public class AppServiceImpl implements AppService {

    @Autowired
    private AppMapper appMapper;
    @Autowired
    private AppBiz appBiz;
    @Autowired
    private TenantAppMapper tenantAppMapper;
    @Autowired
    private MenuMapper menuMapper;
    @Autowired
    private OrgAppMapper orgAppMapper;
    @Autowired
    private RoleMapper roleMapper;
    @Autowired
    private RoleMenuMapper roleMenuMapper;
    @Autowired
    private TenantMapper tenantMapper;
    @Autowired
    private AppFunctionMapper appFunctionMapper;


    /**
     * 添加应用
     * 登录用户添加自定义应用
     * 1、顶级租户添加默认应用，普通租户添加普通应用
     *
     * @param sysApp
     */
    @ApiDoc(desc = "add")
    @ShenyuDubboClient("/add")
    @Override
    public SysApp add(SysApp sysApp) {
        // 根据租户类型确定应用类型
        String tenantId = LoginInfoContextHelper.getTenantId();
        SysTenant sysTenant = tenantMapper.selectTenantInfoByTenantId(tenantId);
        if (sysTenant.getType() == 0) {
            sysApp.setType(0);
        } else {
            sysApp.setType(1);
        }
        appBiz.savaApp(sysApp, tenantId);
        return sysApp;
    }

    /**
     * 删除(解绑)应用
     * 1、判断当前租户下该应用是授权应用，还是新增应用，授权应用不能删除
     * 2、判断该应用是否被授权其他机构
     * 3、解除租户与应用的绑定
     * 4、解除菜单与角色绑定关系
     * 5、删除应用下的角色
     * 6、删除应用下的菜单
     * 7、删除应用
     *
     * @param appId
     */
    @ApiDoc(desc = "delete")
    @ShenyuDubboClient("/delete")
    @Override
    public void delete(String appId) {
        String tenantId = LoginInfoContextHelper.getTenantId();
        // 授权应用不能删除
        String type = tenantAppMapper.selectTenantAppType(tenantId, appId).toString();
        if (type.equals(TenantOrgAppType.AUTH.getCode()) || type.equals(TenantOrgAppType.DEF_AUTH.getCode())) {
            throw new BusinessException(ResponseCode.APP_NOT_DELETE);
        }
        // 判断该应用是否被授权给其他机构
        List<String> orgList = orgAppMapper.getOrgIdsByAppId(appId);
        if (orgList.size() > 0) {
            throw new BusinessException(ResponseCode.APP_EXIST_USER);
        }
        // 解除租户与应用的绑定
        tenantAppMapper.unAuthorizeApp(tenantId, appId);
        // 解除菜单与角色绑定关系
        roleMenuMapper.unAuthorizeMenusByAppId(appId);
        // 删除应用下的角色
        roleMapper.deleteByAppId(appId);
        // 删除应用下的菜单
        menuMapper.deleteByAppId(appId);
        // 删除应用
        appMapper.deleteById(appId);

    }

    /**
     * 编辑应用
     * 1、授权应用不能编辑
     *
     * @param sysApp
     */
    @ApiDoc(desc = "edit")
    @ShenyuDubboClient("/edit")
    @Override
    public SysApp edit(SysApp sysApp) {
        String tenantId = LoginInfoContextHelper.getTenantId();
        String appId = sysApp.getAppId();
        // 授权应用不能编辑
        String type = tenantAppMapper.selectTenantAppType(tenantId, appId).toString();
        if (type.equals(TenantOrgAppType.AUTH.getCode())) {
            throw new BusinessException(ResponseCode.APP_NOT_UPDATE);
        }
        //判断应用编码是否已存在
        SysApp appInfo = appMapper.getAppInfoByAppCode(sysApp.getAppCode());
        if (appInfo != null && !appInfo.getAppId().equals(appId)) {
            throw new BusinessException(ResponseCode.APP_CODE_EXISTS);
        }
        sysApp.setUpdateTime(new Date());
        appMapper.updateById(sysApp);
        return sysApp;
    }

    /**
     * 根据当前登录租户多条件分页查询应用
     *
     * @param sysAppPageDTO
     * @return
     */
    @ApiDoc(desc = "getPageList")
    @ShenyuDubboClient("/getPageList")
    @Override
    public IPage<?> getPageList(@Validated() SysAppDTO sysAppPageDTO) {
        String tenantId = LoginInfoContextHelper.getTenantId();
        Pagination pagination = sysAppPageDTO.getPagination();
        Page<SysApp> page = new Page<>(pagination.getPageNum(), pagination.getPageSize());
        IPage<SysApp> pageList = appMapper.selectPageList(page, tenantId, sysAppPageDTO.getAppId(), sysAppPageDTO.getAppCode(), sysAppPageDTO.getAppName());
        return pageList;
    }

    /**
     * 查询当前租户下的可授权的应用
     */
    @ApiDoc(desc = "getAuthorizableList")
    @ShenyuDubboClient("/getAuthorizableList")
    @Override
    public List<AppResp> getAuthorizableList(SysApp sysApp) {
        String tenantId = LoginInfoContextHelper.getTenantId();
        return appMapper.selectListByTenantIdAndAppName(tenantId, sysApp.getAppName());
    }

    /**
     * 查询某个租户下的授权应用
     */
    @ApiDoc(desc = "getAuthorizedList")
    @ShenyuDubboClient("/getAuthorizedList")
    @Override
    public List<AppResp> getAuthorizedList(SysTenant sysTenant) {
        String tenantId = sysTenant.getTenantId();
        if (StringUtils.isBlank(tenantId)) {
            tenantId = LoginInfoContextHelper.getTenantId();
        }
        if (tenantMapper.selectTenantInfoByTenantId(tenantId).getType() == 0) {
            return appMapper.selectListByTenantIdAndAppName(tenantId, null);
        }
        return appMapper.selectListByTenantId(tenantId);
    }

    /**
     * 查询基座需要注册的所发布子应用
     */
    @ApiDoc(desc = "getPublishedApps")
    @ShenyuDubboClient("/getPublishedApps")
    @Override
    public List<SysApp> getPublishedApps() {
        return appMapper.selectPublishApps();
    }

    @ApiDoc(desc = "addAppFunction")
    @ShenyuDubboClient("/addAppFunction")
    @Override
    public SysAppFunction addAppFunction(SysAppFunction appFunction) {
        appFunction.setAppFunctionId(BsinSnowflake.getId());
        appFunctionMapper.insert(appFunction);
        return appFunction;
    }

    @ApiDoc(desc = "deleteAppFunction")
    @ShenyuDubboClient("/deleteAppFunction")
    @Override
    public void deleteAppFunction(String appFunctionId) {
        appFunctionMapper.deleteById(appFunctionId);
    }

    @ApiDoc(desc = "getAppFunctionList")
    @ShenyuDubboClient("/getAppFunctionList")
    @Override
    public List<SysAppFunction> getAppFunctionList(String appId) {
        List<SysAppFunction> sysApps = appFunctionMapper.selectListByAppId(appId);
        return sysApps;
    }

    @ApiDoc(desc = "getAppFunctionPageList")
    @ShenyuDubboClient("/getAppFunctionPageList")
    @Override
    public IPage<?> getAppFunctionPageList(SysAppDTO sysAppDTO) {
        String appId = sysAppDTO.getAppId();
        Pagination pagination = sysAppDTO.getPagination();
        Page<SysApp> page = new Page<>(pagination.getPageNum(), pagination.getPageSize());
        IPage<SysAppFunction> pageList = appFunctionMapper.selectPageListByAppId(page, appId);
        return pageList;
    }
}
