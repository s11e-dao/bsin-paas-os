package me.flyray.bsin.server.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import me.flyray.bsin.context.BsinServiceContext;
import me.flyray.bsin.domain.entity.MerchantApiFeeConfig;
import me.flyray.bsin.domain.entity.MerchantApp;
import me.flyray.bsin.facade.service.MerchantAppApiFeeService;
import me.flyray.bsin.infrastructure.mapper.MerchantApiFeeConfigMapper;
import me.flyray.bsin.infrastructure.mapper.MerchantAppMapper;
import me.flyray.bsin.server.utils.Pagination;
import org.apache.commons.lang3.StringUtils;
import org.apache.shenyu.client.apache.dubbo.annotation.ShenyuDubboService;
import org.apache.shenyu.client.apidocs.annotations.ApiDoc;
import org.apache.shenyu.client.apidocs.annotations.ApiModule;
import org.apache.shenyu.client.dubbo.common.annotation.ShenyuDubboClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;


@Slf4j
@ShenyuDubboService(path = "/merchantAppApiFee", timeout = 6000)
@ApiModule(value = "merchantAppApiFee")
@Service
public class MerchantAppApiFeeServiceImpl implements MerchantAppApiFeeService {

    @Autowired
    private MerchantApiFeeConfigMapper tenantApiFeeConfigMapper;
    @Autowired
    private MerchantAppMapper tenantAppMapper;
    @Autowired
    private MerchantAppMapper merchantAppMapper;

    /**
     * 通过商户产品的审核
     * @param requestMap
     * @return
     */
    @ApiDoc(desc = "edit")
    @ShenyuDubboClient("/edit")
    @Override
    public void edit(Map<String, Object> requestMap) {
        MerchantApiFeeConfig merchantApiFeeConfig = BsinServiceContext.bisId(MerchantApiFeeConfig.class, requestMap);
        MerchantApp dampTenantApp = new MerchantApp();
        dampTenantApp.setAppId(merchantApiFeeConfig.getAppId());
        dampTenantApp.setStatus(merchantApiFeeConfig.getStatus());
        tenantAppMapper.updateById(dampTenantApp);
        // 更新应用调用费用配置
        tenantApiFeeConfigMapper.updateById(merchantApiFeeConfig);
        // 更新商户产品状态
        MerchantApp merchantApp = merchantAppMapper.selectOne(new LambdaQueryWrapper<MerchantApp>()
                .eq(MerchantApp::getAppId,merchantApiFeeConfig.getAppId()));
        merchantApp.setStatus("1");
        merchantAppMapper.updateById(merchantApp);
    }

    @ApiDoc(desc = "getPageList")
    @ShenyuDubboClient("/getPageList")
    @Override
    public IPage<?> getPageList(Map<String, Object> requestMap) {
        String tenantId = (String) requestMap.get("tenantId");
        String productId = (String) requestMap.get("productId");
        Object paginationObj =  requestMap.get("pagination");
        Pagination pagination = new Pagination();
        BeanUtil.copyProperties(paginationObj,pagination);
        Page<MerchantApiFeeConfig> page = new Page<>(pagination.getPageNum(),pagination.getPageSize());
        LambdaUpdateWrapper<MerchantApiFeeConfig> warapper = new LambdaUpdateWrapper<>();
        warapper.orderByDesc(MerchantApiFeeConfig::getCreateTime);
        warapper.eq(StringUtils.isNotEmpty(tenantId), MerchantApiFeeConfig::getTenantId, tenantId);
        IPage<MerchantApiFeeConfig> pageList = tenantApiFeeConfigMapper.selectPage(page,warapper);
        return pageList;
    }

    /**
     * 修改
     * @param requestMap
     * @return
     */
    @ApiDoc(desc = "getApiFeeConfigInfo")
    @ShenyuDubboClient("/getApiFeeConfigInfo")
    @Override
    public MerchantApiFeeConfig getApiFeeConfigInfo(Map<String, Object> requestMap) {
        String serialNo = (String) requestMap.get("serialNo");
        MerchantApiFeeConfig tenantApiFeeConfig = tenantApiFeeConfigMapper.getTenantApiFeeConfigById(serialNo);
        return tenantApiFeeConfig;
    }

    @ApiDoc(desc = "getList")
    @ShenyuDubboClient("/getList")
    @Override
    public List<?> getList(Map<String, Object> requestMap) {
        Map<String, Object> pagination = (Map<String, Object>) requestMap.get("pagination");
        String tenantId = (String) requestMap.get("bizTenantId");
        String appId = (String) requestMap.get("appId");
        List<MerchantApiFeeConfig> pageList = tenantApiFeeConfigMapper.getPageList(tenantId, appId);
        return pageList;
    }

}
