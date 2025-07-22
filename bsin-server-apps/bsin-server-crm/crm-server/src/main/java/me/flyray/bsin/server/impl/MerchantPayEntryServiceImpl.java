package me.flyray.bsin.server.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import me.flyray.bsin.domain.entity.CustomerBase;
import me.flyray.bsin.domain.request.MerchantDTO;
import me.flyray.bsin.facade.service.MerchantPayEntryService;
import me.flyray.bsin.infrastructure.mapper.MerchantPayEntryMapper;
import me.flyray.bsin.security.contex.LoginInfoContextHelper;
import me.flyray.bsin.server.utils.Pagination;
import org.apache.shenyu.client.apache.dubbo.annotation.ShenyuDubboService;
import org.apache.shenyu.client.apidocs.annotations.ApiDoc;
import org.apache.shenyu.client.apidocs.annotations.ApiModule;
import org.apache.shenyu.client.dubbo.common.annotation.ShenyuDubboClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Slf4j
@ShenyuDubboService(path = "/merchantPayEntry", timeout = 6000)
@ApiModule(value = "merchantPayEntry")
@Service
public class MerchantPayEntryServiceImpl implements MerchantPayEntryService {

    @Autowired
    private MerchantPayEntryMapper merchantPayEntryMapper;

    @ApiDoc(desc = "getPageList")
    @ShenyuDubboClient("/getPageList")
    @Override
    public IPage<MerchantDTO> getPageList(Map<String, Object> requestMap) {
        String tenantId = LoginInfoContextHelper.getTenantId();
        Object paginationObj =  requestMap.get("pagination");
        Pagination pagination = new Pagination();
        BeanUtil.copyProperties(paginationObj,pagination);
        Page<CustomerBase> page = new Page<>(pagination.getPageNum(), pagination.getPageSize());
        IPage<MerchantDTO> pageList =  merchantPayEntryMapper.selectPageList(page,tenantId);
        return pageList;
    }

}
