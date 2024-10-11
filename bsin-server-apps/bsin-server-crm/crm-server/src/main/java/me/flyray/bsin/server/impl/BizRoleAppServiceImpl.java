package me.flyray.bsin.server.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import me.flyray.bsin.context.BsinServiceContext;
import me.flyray.bsin.domain.entity.BizRoleApp;
import me.flyray.bsin.facade.service.BizRoleAppService;
import me.flyray.bsin.infrastructure.mapper.BizRoleAppMapper;
import me.flyray.bsin.security.contex.LoginInfoContextHelper;
import me.flyray.bsin.server.utils.Pagination;
import me.flyray.bsin.utils.BsinSnowflake;
import org.apache.commons.lang3.StringUtils;
import org.apache.shenyu.client.apache.dubbo.annotation.ShenyuDubboService;
import org.apache.shenyu.client.apidocs.annotations.ApiDoc;
import org.apache.shenyu.client.apidocs.annotations.ApiModule;
import org.apache.shenyu.client.dubbo.common.annotation.ShenyuDubboClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;



@Slf4j
@ShenyuDubboService(path = "/merchantApp", timeout = 6000)
@ApiModule(value = "merchantApp")
@Service
public class BizRoleAppServiceImpl implements BizRoleAppService {

    @Autowired
    private BizRoleAppMapper merchantAppMapper;

    /**
     * 添加
     * @param requestMap
     * @return
     */
    @ApiDoc(desc = "add")
    @ShenyuDubboClient("/add")
    @Override
    public void add(Map<String, Object> requestMap) {
        String tenantId = LoginInfoContextHelper.getTenantId();
        String merchantNo = LoginInfoContextHelper.getMerchantNo();
        BizRoleApp merchantApp = BsinServiceContext.getReqBodyDto(BizRoleApp.class, requestMap);
        merchantApp.setSerialNo(BsinSnowflake.getId());
        String appId = BsinSnowflake.getId();
        merchantApp.setTenantId(tenantId);
        merchantApp.setMerchantNo(merchantNo);
        merchantApp.setAppId(appId);
        merchantApp.setAppSecret(BsinSnowflake.getId());
        merchantAppMapper.insert(merchantApp);
//        MerchantApiFeeConfig tenantApiFeeConfig = new MerchantApiFeeConfig();
//        tenantApiFeeConfig.setTenantId(tenantId);
//        tenantApiFeeConfig.setProductId(productId);
//        tenantApiFeeConfig.setSerialNo(BsinSnowflake.getId());
//        tenantApiFeeConfig.setCreateTime(new Date());
//        tenantApiFeeConfig.setStatus("0");
//        // 添加一条app的调用费用配置信息
//        merchantApiFeeConfigMapper.insert(tenantApiFeeConfig);

    }

    /**
     * 删除
     * @param requestMap
     * @return
     */
    @ApiDoc(desc = "delete")
    @ShenyuDubboClient("/delete")
    @Override
    public void delete(Map<String, Object> requestMap) {
        BizRoleApp merchantApp = BsinServiceContext.bisId(BizRoleApp.class, requestMap);
        merchantAppMapper.deleteById(merchantApp.getSerialNo());
    }

    /**
     * 修改
     * @param requestMap
     * @return
     */
    @ApiDoc(desc = "edit")
    @ShenyuDubboClient("/edit")
    @Override
    public void edit(Map<String, Object> requestMap) {
        BizRoleApp merchantApp = BsinServiceContext.bisId(BizRoleApp.class, requestMap);
        String serialNo = (String) requestMap.get("serialNo");
        merchantApp.setAppId(serialNo);
        merchantAppMapper.updateById(merchantApp);
    }

    /**
     * 修改
     * @param requestMap
     * @return
     */
    @ApiDoc(desc = "getDetail")
    @ShenyuDubboClient("/getDetail")
    @Override
    public BizRoleApp getDetail(Map<String, Object> requestMap) {
        String tenantId = LoginInfoContextHelper.getTenantId();
        // 从当前token中获取appId
        String serialNo = (String) requestMap.get("serialNo");
        BizRoleApp merchantApp = new BizRoleApp();
        merchantApp.setTenantId(tenantId);
        merchantApp.setAppId(serialNo);
        BizRoleApp tenantAppResult = merchantAppMapper.getAppInfo(merchantApp);
        return tenantAppResult;
    }

    /**
     * 分页查询
     * @param requestMap
     * @return
     */
    @ApiDoc(desc = "getPageList")
    @ShenyuDubboClient("/getPageList")
    @Override
    public IPage<?> getPageList(Map<String, Object> requestMap) {
        String tenantId = LoginInfoContextHelper.getTenantId();
        String merchantNo = LoginInfoContextHelper.getMerchantNo();
        String appName = (String) requestMap.get("appName");
        Object paginationObj =  requestMap.get("pagination");
        Pagination pagination = new Pagination();
        BeanUtil.copyProperties(paginationObj,pagination);
        Page<BizRoleApp> page = new Page<>(pagination.getPageNum(),pagination.getPageSize());
        LambdaUpdateWrapper<BizRoleApp> warapper = new LambdaUpdateWrapper<>();
        warapper.orderByDesc(BizRoleApp::getCreateTime);
        warapper.eq(BizRoleApp::getTenantId, tenantId);
        warapper.eq(BizRoleApp::getMerchantNo, merchantNo);
        warapper.eq(StringUtils.isNotEmpty(appName), BizRoleApp::getAppName, appName);
        IPage<BizRoleApp> pageList = merchantAppMapper.selectPage(page,warapper);
        return pageList;
    }

}
