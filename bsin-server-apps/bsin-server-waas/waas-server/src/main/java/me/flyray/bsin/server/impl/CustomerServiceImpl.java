package me.flyray.bsin.server.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import me.flyray.bsin.domain.entity.customer.CustomerBase;
import me.flyray.bsin.infrastructure.mapper.customer.CustomerBaseMapper;
import me.flyray.bsin.utils.BsinResultEntity;
import org.apache.dubbo.config.annotation.DubboService;
import org.apache.shenyu.client.apache.dubbo.annotation.ShenyuDubboService;
import org.apache.shenyu.client.apidocs.annotations.ApiDoc;
import org.apache.shenyu.client.apidocs.annotations.ApiModule;
import org.apache.shenyu.client.dubbo.common.annotation.ShenyuDubboClient;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;

import lombok.extern.slf4j.Slf4j;
import me.flyray.bsin.facade.service.customer.CustomerService;

/**
 * @author bolei
 * @date 2023/6/28 16:38
 * @desc
 */
@Slf4j
@DubboService
@ApiModule(value = "customer")
@ShenyuDubboService("/customer")
public class CustomerServiceImpl implements CustomerService {
    @Autowired
    private CustomerBaseMapper customerBaseMapper;

    @Override
    @ShenyuDubboClient("/getPageList")
    @ApiDoc(desc = "getPageList")
    public IPage<CustomerBase> getPageList(Map<String, Object> requestMap) {
        log.info("CustomerService.getPageList,param:{}", requestMap);
        QueryWrapper<CustomerBase> queryWrapper = new QueryWrapper();
        queryWrapper.allEq(requestMap);
        Page<CustomerBase> customerBasePage = new Page<>( requestMap.get("pageNo") != null ? (Integer) requestMap.get("pageNo") : 0, requestMap.get("pageSize") != null ? (Integer) requestMap.get("pageSize") : 10);
        return customerBaseMapper.selectPage(customerBasePage,queryWrapper);
    }
}
