package me.flyray.bsin.facade.service;

import com.baomidou.mybatisplus.core.metadata.IPage;

import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;

import java.util.List;


import me.flyray.bsin.domain.entity.SysApp;
import me.flyray.bsin.domain.entity.SysAppFunction;
import me.flyray.bsin.domain.entity.SysTenant;
import me.flyray.bsin.domain.request.SysAppDTO;
import me.flyray.bsin.domain.response.AppResp;
import me.flyray.bsin.validate.AddGroup;

@Validated
public interface AppService {

    /**
     *添加应用
     */
    @Validated(AddGroup.class)
    SysApp add(@Valid SysApp sysAppDTO);

    /**
     *删除应用
     */
    void delete(String appId);

    /**
     *编辑应用
     */
    SysApp edit(SysApp sysApp);

    /**
     *分页查询
     */
    IPage<?> getPageList(SysAppDTO sysAppDTO);

    /**
     *根据当前租户查询应用
     */
    List<AppResp> getAuthorizableList(SysApp sysApp);

    /**
     *查询租户授权应用
     */
    List<AppResp> getAuthorizedList(SysTenant sysTenant);


    /**
     *查询所有已发布的应用
     */
    List<SysApp> getPublishedApps();

    /**
     * 添加应用功能
     */
    public SysAppFunction addAppFunction(SysAppFunction appFunction);

    /**
     * 删除应用功能
     */
    public void deleteAppFunction(String appFunctionId);

    /**
     * 查询应用功能列表
     */
    public List<SysAppFunction> getAppFunctionList(String appId);

    /**
     * 查询应用功能列表
     */
    public IPage<?> getAppFunctionPageList(SysAppDTO sysAppDTO);
}
