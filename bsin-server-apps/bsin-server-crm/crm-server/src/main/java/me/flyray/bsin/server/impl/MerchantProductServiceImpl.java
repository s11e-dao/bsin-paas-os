package me.flyray.bsin.server.impl;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import org.apache.commons.lang3.StringUtils;
import org.apache.shenyu.client.apache.dubbo.annotation.ShenyuDubboService;
import org.apache.shenyu.client.apidocs.annotations.ApiModule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

import lombok.extern.slf4j.Slf4j;
import me.flyray.bsin.context.BsinServiceContext;
import me.flyray.bsin.domain.domain.MerchantProduct;
import me.flyray.bsin.facade.service.MerchantProductService;
import me.flyray.bsin.infrastructure.mapper.MerchantProductMapper;
import me.flyray.bsin.security.contex.LoginInfoContextHelper;
import me.flyray.bsin.server.utils.Pagination;
import me.flyray.bsin.server.utils.RespBodyHandler;
import me.flyray.bsin.utils.BsinSnowflake;



@Slf4j
@ShenyuDubboService(path = "/merchantApp", timeout = 6000)
@ApiModule(value = "merchantApp")
@Service
public class MerchantProductServiceImpl implements MerchantProductService {

    @Autowired
    private MerchantProductMapper merchantProductMapper;

    /**
     * 添加
     * @param requestMap
     * @return
     */
    @Override
    public Map<String, Object> add(Map<String, Object> requestMap) {
        String tenantId = LoginInfoContextHelper.getTenantId();
        String merchantNo = LoginInfoContextHelper.getMerchantNo();
        MerchantProduct merchantProduct = BsinServiceContext.getReqBodyDto(MerchantProduct.class, requestMap);
        merchantProduct.setSerialNo(BsinSnowflake.getId());
        String productId = BsinSnowflake.getId();
        merchantProduct.setTenantId(tenantId);
        merchantProduct.setMerchantNo(merchantNo);
        merchantProduct.setProductId(productId);
        merchantProduct.setProductSecret(BsinSnowflake.getId());
        merchantProductMapper.insert(merchantProduct);
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
    @Override
    public Map<String, Object> delete(Map<String, Object> requestMap) {
        MerchantProduct merchantProduct = BsinServiceContext.bisId(MerchantProduct.class, requestMap);
        merchantProductMapper.deleteById(merchantProduct.getSerialNo());
        return RespBodyHandler.RespBodyDto();
    }

    /**
     * 修改
     * @param requestMap
     * @return
     */
    @Override
    public Map<String, Object> edit(Map<String, Object> requestMap) {
        MerchantProduct merchantProduct = BsinServiceContext.bisId(MerchantProduct.class, requestMap);
        String serialNo = (String) requestMap.get("serialNo");
        merchantProduct.setProductId(serialNo);
        merchantProductMapper.updateById(merchantProduct);
        return RespBodyHandler.RespBodyDto();
    }

    /**
     * 修改
     * @param requestMap
     * @return
     */
    @Override
    public Map<String, Object> getDetail(Map<String, Object> requestMap) {
        String tenantId = LoginInfoContextHelper.getTenantId();
        // 从当前token中获取appId
        String serialNo = (String) requestMap.get("serialNo");
        MerchantProduct merchantProduct = new MerchantProduct();
        merchantProduct.setTenantId(tenantId);
        merchantProduct.setProductId(serialNo);
        MerchantProduct tenantAppResult = merchantProductMapper.getProductInfo(merchantProduct);
        return RespBodyHandler.setRespBodyDto(tenantAppResult);
    }

    /**
     * 分页查询
     * @param requestMap
     * @return
     */
    @Override
    public Map<String, Object> getPageList(Map<String, Object> requestMap) {
        String tenantId = LoginInfoContextHelper.getTenantId();
        String merchantNo = LoginInfoContextHelper.getMerchantNo();
        String productName = (String) requestMap.get("productName");
        Pagination pagination = (Pagination) requestMap.get("pagination");
        Page<MerchantProduct> page = new Page<>(pagination.getPageNum(),pagination.getPageSize());
        LambdaUpdateWrapper<MerchantProduct> warapper = new LambdaUpdateWrapper<>();
        warapper.orderByDesc(MerchantProduct::getCreateTime);
        warapper.eq(MerchantProduct::getTenantId, tenantId);
        warapper.eq(MerchantProduct::getMerchantNo, merchantNo);
        warapper.eq(StringUtils.isNotEmpty(productName), MerchantProduct::getProductName, productName);
        IPage<MerchantProduct> pageList = merchantProductMapper.selectPage(page,warapper);
        return RespBodyHandler.setRespPageInfoBodyDto(pageList);
    }
}
