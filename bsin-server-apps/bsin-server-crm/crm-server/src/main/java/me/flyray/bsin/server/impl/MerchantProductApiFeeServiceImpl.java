package me.flyray.bsin.server.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import me.flyray.bsin.context.BsinServiceContext;
import me.flyray.bsin.domain.domain.MerchantApiFeeConfig;
import me.flyray.bsin.domain.domain.MerchantProduct;
import me.flyray.bsin.facade.service.MerchantProductApiFeeService;
import me.flyray.bsin.infrastructure.mapper.MerchantApiFeeConfigMapper;
import me.flyray.bsin.infrastructure.mapper.MerchantProductMapper;
import me.flyray.bsin.server.utils.Pagination;
import me.flyray.bsin.server.utils.RespBodyHandler;
import org.apache.commons.lang3.StringUtils;
import org.apache.shenyu.client.apache.dubbo.annotation.ShenyuDubboService;
import org.apache.shenyu.client.apidocs.annotations.ApiModule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;


@Slf4j
@ShenyuDubboService(path = "/merchantApiFeeConfig", timeout = 6000)
@ApiModule(value = "merchantApiFeeConfig")
@Service
public class MerchantProductApiFeeServiceImpl implements MerchantProductApiFeeService {

    @Autowired
    private MerchantApiFeeConfigMapper tenantApiFeeConfigMapper;
    @Autowired
    private MerchantProductMapper tenantAppMapper;
    @Autowired
    private MerchantProductMapper merchantProductMapper;

    /**
     * 通过商户产品的审核
     * @param requestMap
     * @return
     */
    @Override
    public Map<String, Object> edit(Map<String, Object> requestMap) {
        MerchantApiFeeConfig merchantApiFeeConfig = BsinServiceContext.bisId(MerchantApiFeeConfig.class, requestMap);
        MerchantProduct dampTenantApp = new MerchantProduct();
        dampTenantApp.setProductId(merchantApiFeeConfig.getProductId());
        dampTenantApp.setStatus(merchantApiFeeConfig.getStatus());
        tenantAppMapper.updateById(dampTenantApp);
        // 更新应用调用费用配置
        tenantApiFeeConfigMapper.updateById(merchantApiFeeConfig);
        // 更新商户产品状态
        MerchantProduct merchantProduct = merchantProductMapper.selectOne(new LambdaQueryWrapper<MerchantProduct>()
                .eq(MerchantProduct::getProductId,merchantApiFeeConfig.getProductId()));
        merchantProduct.setStatus("1");
        merchantProductMapper.updateById(merchantProduct);
        return RespBodyHandler.RespBodyDto();
    }

    @Override
    public Map<String, Object> getPageList(Map<String, Object> requestMap) {
        String tenantId = (String) requestMap.get("tenantId");
        String productId = (String) requestMap.get("productId");
        Pagination pagination = (Pagination) requestMap.get("pagination");
        Page<MerchantApiFeeConfig> page = new Page<>(pagination.getPageNum(),pagination.getPageSize());
        LambdaUpdateWrapper<MerchantApiFeeConfig> warapper = new LambdaUpdateWrapper<>();
        warapper.orderByDesc(MerchantApiFeeConfig::getCreateTime);
        warapper.eq(StringUtils.isNotEmpty(tenantId), MerchantApiFeeConfig::getTenantId, tenantId);
        IPage<MerchantApiFeeConfig> pageList = tenantApiFeeConfigMapper.selectPage(page,warapper);
        return RespBodyHandler.setRespPageInfoBodyDto(pageList);
    }

    /**
     * 修改
     * @param requestMap
     * @return
     */
    @Override
    public Map<String, Object> getApiFeeConfigInfo(Map<String, Object> requestMap) {
        String serialNo = (String) requestMap.get("serialNo");
        MerchantApiFeeConfig tenantApiFeeConfig = tenantApiFeeConfigMapper.getTenantApiFeeConfigById(serialNo);
        return RespBodyHandler.setRespBodyDto(tenantApiFeeConfig);
    }

    @Override
    public Map<String, Object> getList(Map<String, Object> requestMap) {
        Map<String, Object> pagination = (Map<String, Object>) requestMap.get("pagination");
        String tenantId = (String) requestMap.get("bizTenantId");
        String appId = (String) requestMap.get("appId");
        List<MerchantApiFeeConfig> pageList = tenantApiFeeConfigMapper.getPageList(tenantId, appId);
        return RespBodyHandler.setRespBodyListDto(pageList);
    }
}
