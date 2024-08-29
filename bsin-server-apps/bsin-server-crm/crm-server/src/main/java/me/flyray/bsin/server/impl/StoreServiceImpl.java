package me.flyray.bsin.server.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import me.flyray.bsin.context.BsinServiceContext;
import me.flyray.bsin.domain.entity.Store;
import me.flyray.bsin.facade.service.StoreService;
import me.flyray.bsin.infrastructure.mapper.StoreMapper;
import me.flyray.bsin.security.contex.LoginInfoContextHelper;
import me.flyray.bsin.security.domain.LoginUser;
import me.flyray.bsin.server.utils.Pagination;
import org.apache.commons.collections4.MapUtils;
import org.apache.shenyu.client.apache.dubbo.annotation.ShenyuDubboService;
import org.apache.shenyu.client.apidocs.annotations.ApiDoc;
import org.apache.shenyu.client.apidocs.annotations.ApiModule;
import org.apache.shenyu.client.dubbo.common.annotation.ShenyuDubboClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * @author bolei
 * @date 2023/6/28 16:42
 * @desc
 */

@Slf4j
@ShenyuDubboService(path = "/store", timeout = 6000)
@ApiModule(value = "store")
@Service
public class StoreServiceImpl implements StoreService {

    @Autowired
    private StoreMapper storeMapper;

    @ShenyuDubboClient("/openStore")
    @ApiDoc(desc = "openStore")
    @Override
    public void openStore(Map<String, Object> requestMap) {
        LoginUser loginUser = LoginInfoContextHelper.getLoginUser();
        Store store = BsinServiceContext.getReqBodyDto(Store.class, requestMap);
        store.setTenantId(loginUser.getTenantId());
        store.setMerchantNo(loginUser.getMerchantNo());
        storeMapper.insert(store);
    }

    @ShenyuDubboClient("/delete")
    @ApiDoc(desc = "delete")
    @Override
    public void delete(Map<String, Object> requestMap) {
        String serialNo = MapUtils.getString(requestMap, "serialNo");
        storeMapper.deleteById(serialNo);
    }

    @ShenyuDubboClient("/edit")
    @ApiDoc(desc = "edit")
    @Override
    public void edit(Map<String, Object> requestMap) {
        Store store = BsinServiceContext.getReqBodyDto(Store.class, requestMap);
        storeMapper.updateById(store);
    }

    @ShenyuDubboClient("/getPageList")
    @ApiDoc(desc = "getPageList")
    @Override
    public IPage<?> getPageList(Map<String, Object> requestMap) {
        LoginUser loginUser = LoginInfoContextHelper.getLoginUser();
        Store store = BsinServiceContext.getReqBodyDto(Store.class, requestMap);
        Object paginationObj =  requestMap.get("pagination");
        Pagination pagination = new Pagination();
        BeanUtil.copyProperties(paginationObj,pagination);
        Page<Store> page = new Page<>(pagination.getPageNum(),pagination.getPageSize());
        LambdaUpdateWrapper<Store> warapper = new LambdaUpdateWrapper<>();
        warapper.orderByDesc(Store::getCreateTime);
        warapper.eq(Store::getTenantId, loginUser.getTenantId());
        warapper.eq(Store::getMerchantNo, loginUser.getMerchantNo());
        IPage<Store> pageList = storeMapper.selectPage(page,warapper);
        return pageList;
    }

    @ShenyuDubboClient("/getDetail")
    @ApiDoc(desc = "getDetail")
    @Override
    public Store getDetail(Map<String, Object> requestMap) {
        String serialNo = MapUtils.getString(requestMap, "serialNo");
        Store store = storeMapper.selectById(serialNo);
        return store;
    }

}
