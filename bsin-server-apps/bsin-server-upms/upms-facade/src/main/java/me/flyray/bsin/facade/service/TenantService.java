package me.flyray.bsin.facade.service;


import com.baomidou.mybatisplus.core.metadata.IPage;

import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;

import java.util.List;

import me.flyray.bsin.domain.entity.SysApp;
import me.flyray.bsin.domain.entity.SysTenant;
import me.flyray.bsin.domain.request.SysTenantDTO;
import me.flyray.bsin.validate.AddGroup;
import me.flyray.bsin.validate.QueryGroup;

@Validated
public interface TenantService {

    /**
     * 添加租户
     */
    @Validated(AddGroup.class)
    SysTenant add(@Valid SysTenantDTO sysTenantDTO);

    /**
     * 删除租户
     */
    void delete(String tenantId);

    /**
     * 编辑租户
     */
    @Validated(AddGroup.class)
    SysTenant edit(SysTenant tenant);

    /**
     * 根据条件查询用户(rpc)
     * @return
     */
    SysTenant getDetail(String tenantId);

    /**
     * 分页查询租户
     */
    @Validated(QueryGroup.class)
    IPage<SysTenant> getPageList(@Valid SysTenantDTO sysTenantDTO);


    /**
     * 给租户授权应用
     */
    void authorizeApps(String tenantId, List<String> appIds);

    /**
     * 查询所有租户
     */
    List<SysTenant> getAllTenantList();

    /**
     * 查询租户代理产品的基础应用
     */
    public SysApp getTenantBaseApp();

}
