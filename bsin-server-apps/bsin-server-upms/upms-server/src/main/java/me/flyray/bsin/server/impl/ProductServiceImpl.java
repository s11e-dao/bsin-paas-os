package me.flyray.bsin.server.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import org.apache.shenyu.client.apache.dubbo.annotation.ShenyuDubboService;
import org.apache.shenyu.client.apidocs.annotations.ApiDoc;
import org.apache.shenyu.client.apidocs.annotations.ApiModule;
import org.apache.shenyu.client.dubbo.common.annotation.ShenyuDubboClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import me.flyray.bsin.constants.ResponseCode;
import me.flyray.bsin.domain.entity.SysApp;
import me.flyray.bsin.domain.entity.SysProduct;
import me.flyray.bsin.domain.entity.SysProductApp;
import me.flyray.bsin.domain.request.SysProductDTO;
import me.flyray.bsin.exception.BusinessException;
import me.flyray.bsin.facade.service.ProductService;
import me.flyray.bsin.infrastructure.config.TenantConfig;
import me.flyray.bsin.infrastructure.mapper.ProductAppMapper;
import me.flyray.bsin.infrastructure.mapper.ProductMapper;
import me.flyray.bsin.mybatis.utils.Pagination;
import me.flyray.bsin.utils.BsinSnowflake;


/**
 * @author bolei
 * @date 2023/11/5
 * @desc
 */

@ShenyuDubboService(path = "/product",timeout = 15000)
@ApiModule(value = "product")
@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private TenantConfig tenantConfig;
    @Autowired
    private ProductMapper productMapper;
    @Autowired
    private ProductAppMapper productAppMapper;

    @ApiDoc(desc = "add")
    @ShenyuDubboClient("/add")
    @Override
    public SysProduct add(SysProduct product) {
        String id = BsinSnowflake.getId();
        product.setProductId(id);
        productMapper.insert(product);
        return product;
    }

    @ApiDoc(desc = "delete")
    @ShenyuDubboClient("/delete")
    @Override
    public void delete(String id) {
        productMapper.deleteById(id);
    }

    @ApiDoc(desc = "edit")
    @ShenyuDubboClient("/edit")
    @Override
    public SysProduct edit(SysProduct product) {
        productMapper.updateById(product);
        return product;
    }

    @ApiDoc(desc = "getList")
    @ShenyuDubboClient("/getList")
    @Override
    public List<SysProduct> getList(String productName) {
        List<SysProduct> productList = productMapper.selectList(productName);
        return productList;
    }

    @ApiDoc(desc = "getPageList")
    @ShenyuDubboClient("/getPageList")
    @Override
    public IPage<?> getPageList(SysProductDTO sysProductDTO) {
        String productName = sysProductDTO.getProductName();
        Pagination pagination = sysProductDTO.getPagination();
        // BsinPageUtil.pageNotNull(pagination);
        Page<SysProduct> page = new Page<>(pagination.getPageNum(), pagination.getPageSize());
        IPage<SysProduct> pageList = productMapper.selectPageList(page, productName);
        return pageList;
    }

    @ApiDoc(desc = "addProductApp")
    @ShenyuDubboClient("/addProductApp")
    @Override
    public SysProductApp addProductApp(SysProductApp productApp) {
        String id = BsinSnowflake.getId();
        // 判断应用是否已经存在
        SysProductApp productAppResult = productAppMapper.selectByProductIdAndAppId(productApp.getProductId(),productApp.getAppId());
        if(productAppResult != null){
            throw new BusinessException(ResponseCode.DATA_HAS_EXIST);
        }
        // 判断添加的是否是权限管理
        if(tenantConfig.getAppId().equals(productApp.getAppId())){
            throw new BusinessException(ResponseCode.UPMS_NOT_ADD);
        }
        productApp.setId(id);
        productAppMapper.insert(productApp);
        return productApp;
    }

    @ApiDoc(desc = "deleteProductApp")
    @ShenyuDubboClient("/deleteProductApp")
    @Override
    public void deleteProductApp(String productId,String appId) {
        //TODO 默认应用不能商户
        productAppMapper.deleteById(productId, appId);
    }

    @ApiDoc(desc = "getProductAppList")
    @ShenyuDubboClient("/getProductAppList")
    @Override
    public List<SysApp> getProductAppList(String productId) {
        List<SysApp> sysApps = productAppMapper.selectListByProductId(productId);
        return sysApps;
    }

    @ApiDoc(desc = "getProductAppFunctionList")
    @ShenyuDubboClient("/getProductAppFunctionList")
    @Override
    public List<SysApp> getProductAppFunctionList(String productId) {
        List<SysApp> sysApps = productAppMapper.selectListByProductId(productId);
        // TODO 查询应用的功能
        return sysApps;
    }

    @ApiDoc(desc = "getProductAppPageList")
    @ShenyuDubboClient("/getProductAppPageList")
    @Override
    public IPage<?> getProductAppPageList(SysProductDTO sysProductDTO) {
        String productId = sysProductDTO.getProductId();
        Pagination pagination = sysProductDTO.getPagination();
        // BsinPageUtil.pageNotNull(pagination);
        Page<SysApp> page = new Page<>(pagination.getPageNum(), pagination.getPageSize());
        IPage<SysApp> pageList = productAppMapper.selectPageList(page, productId);
        return pageList;
    }

}
