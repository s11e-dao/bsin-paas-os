package me.flyray.bsin.server.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import org.apache.commons.lang3.StringUtils;
import org.apache.shenyu.client.apache.dubbo.annotation.ShenyuDubboService;
import org.apache.shenyu.client.apidocs.annotations.ApiDoc;
import org.apache.shenyu.client.apidocs.annotations.ApiModule;
import org.apache.shenyu.client.dubbo.common.annotation.ShenyuDubboClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

import lombok.extern.slf4j.Slf4j;
import me.flyray.bsin.context.BsinServiceContext;
import me.flyray.bsin.domain.domain.MerchantApp;
import me.flyray.bsin.facade.service.MerchantAppService;
import me.flyray.bsin.infrastructure.mapper.MerchantAppMapper;
import me.flyray.bsin.security.contex.LoginInfoContextHelper;
import me.flyray.bsin.server.utils.Pagination;
import me.flyray.bsin.server.utils.RespBodyHandler;
import me.flyray.bsin.utils.BsinSnowflake;



@Slf4j
@ShenyuDubboService(path = "/merchantApp", timeout = 6000)
@ApiModule(value = "merchantApp")
@Service
public class MerchantAppServiceImpl implements MerchantAppService {

    @Autowired
    private MerchantAppMapper merchantAppMapper;

    /**
     * 添加
     * @param requestMap
     * @return
     */
    @ApiDoc(desc = "getList")
    @ShenyuDubboClient("/getList")
    @Override
    public Map<String, Object> add(Map<String, Object> requestMap) {
        String tenantId = LoginInfoContextHelper.getTenantId();
        String merchantNo = LoginInfoContextHelper.getMerchantNo();
        MerchantApp merchantApp = BsinServiceContext.getReqBodyDto(MerchantApp.class, requestMap);
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

        return RespBodyHandler.RespBodyDto();
    }

    /**
     * 删除
     * @param requestMap
     * @return
     */
    @ApiDoc(desc = "delete")
    @ShenyuDubboClient("/delete")
    @Override
    public Map<String, Object> delete(Map<String, Object> requestMap) {
        MerchantApp merchantApp = BsinServiceContext.bisId(MerchantApp.class, requestMap);
        merchantAppMapper.deleteById(merchantApp.getSerialNo());
        return RespBodyHandler.RespBodyDto();
    }

    /**
     * 修改
     * @param requestMap
     * @return
     */
    @ApiDoc(desc = "edit")
    @ShenyuDubboClient("/edit")
    @Override
    public Map<String, Object> edit(Map<String, Object> requestMap) {
        MerchantApp merchantApp = BsinServiceContext.bisId(MerchantApp.class, requestMap);
        String serialNo = (String) requestMap.get("serialNo");
        merchantApp.setAppId(serialNo);
        merchantAppMapper.updateById(merchantApp);
        return RespBodyHandler.RespBodyDto();
    }

    /**
     * 修改
     * @param requestMap
     * @return
     */
    @ApiDoc(desc = "getDetail")
    @ShenyuDubboClient("/getDetail")
    @Override
    public Map<String, Object> getDetail(Map<String, Object> requestMap) {
        String tenantId = LoginInfoContextHelper.getTenantId();
        // 从当前token中获取appId
        String serialNo = (String) requestMap.get("serialNo");
        MerchantApp merchantApp = new MerchantApp();
        merchantApp.setTenantId(tenantId);
        merchantApp.setAppId(serialNo);
        MerchantApp tenantAppResult = merchantAppMapper.getProductInfo(merchantApp);
        return RespBodyHandler.setRespBodyDto(tenantAppResult);
    }

    /**
     * 分页查询
     * @param requestMap
     * @return
     */
    @ApiDoc(desc = "getPageList")
    @ShenyuDubboClient("/getPageList")
    @Override
    public Map<String, Object> getPageList(Map<String, Object> requestMap) {
        String tenantId = LoginInfoContextHelper.getTenantId();
        String merchantNo = LoginInfoContextHelper.getMerchantNo();
        String appName = (String) requestMap.get("appName");
        Object paginationObj =  requestMap.get("pagination");
        Pagination pagination = new Pagination();
        BeanUtil.copyProperties(paginationObj,pagination);
        Page<MerchantApp> page = new Page<>(pagination.getPageNum(),pagination.getPageSize());
        LambdaUpdateWrapper<MerchantApp> warapper = new LambdaUpdateWrapper<>();
        warapper.orderByDesc(MerchantApp::getCreateTime);
        warapper.eq(MerchantApp::getTenantId, tenantId);
        warapper.eq(MerchantApp::getMerchantNo, merchantNo);
        warapper.eq(StringUtils.isNotEmpty(appName), MerchantApp::getAppName, appName);
        IPage<MerchantApp> pageList = merchantAppMapper.selectPage(page,warapper);
        return RespBodyHandler.setRespPageInfoBodyDto(pageList);
    }

}
