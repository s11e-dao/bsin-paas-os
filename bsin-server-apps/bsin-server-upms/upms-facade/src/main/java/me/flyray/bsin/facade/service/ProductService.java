package me.flyray.bsin.facade.service;

import com.baomidou.mybatisplus.core.metadata.IPage;

import java.util.List;

import me.flyray.bsin.domain.entity.SysApp;
import me.flyray.bsin.domain.entity.SysProduct;
import me.flyray.bsin.domain.entity.SysProductApp;
import me.flyray.bsin.domain.request.SysProductDTO;

/**
* @author bolei
* @description 针对表【sys_product】的数据库操作Service
* @createDate 2023-11-05 17:59:52
*/

public interface ProductService {

    /**
     * 新增
     */
    public SysProduct add(SysProduct product);

    /**
     * 删除
     */
    public void delete(String id);

    /**
     * 更新
     */
    public SysProduct edit(SysProduct product);

    /**
     * 根据条件查询列表
     */
    public List<SysProduct> getList(String productName);

    /**
     * 根据条件查询列表
     */
    public IPage<?> getPageList(SysProductDTO sysProductDTO);

    /**
     * 添加产品对应的应用
     */
    public SysProductApp addProductApp(SysProductApp productApp);

    /**
     * 删除产品与应用的对应关系
     */
    public void deleteProductApp(String productId,String appId);

    /**
     * 查询产品对应的应用列表
     */
    public List<SysApp> getProductAppList(String productId);

    /**
     * 查询产品对应的应用及功能列表
     */
    public List<SysApp> getProductAppFunctionList(String productId);

    /**
     * 查询产品对应的应用列表
     */
    public IPage<?> getProductAppPageList(SysProductDTO sysProductDTO);

}
