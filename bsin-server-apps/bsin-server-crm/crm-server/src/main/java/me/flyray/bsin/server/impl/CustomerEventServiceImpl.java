package me.flyray.bsin.server.impl;

import org.apache.shenyu.client.apache.dubbo.annotation.ShenyuDubboService;
import org.apache.shenyu.client.apidocs.annotations.ApiModule;
import org.springframework.stereotype.Service;

import java.util.Map;

import lombok.extern.slf4j.Slf4j;
import me.flyray.bsin.facade.service.CustomerEventService;

/**
 * @author bolei
 * @date 2023/7/27 9:43
 * @desc
 */


@Slf4j
@ShenyuDubboService(path = "/eventRule", timeout = 6000)
@ApiModule(value = "eventRule")
@Service
public class CustomerEventServiceImpl implements CustomerEventService {


    @Override
    public Map<String, Object> add(Map<String, Object> requestMap) {
        return null;
    }

    @Override
    public Map<String, Object> delete(Map<String, Object> requestMap) {
        return null;
    }

    @Override
    public Map<String, Object> getList(Map<String, Object> requestMap) {
        return null;
    }

    @Override
    public Map<String, Object> getPageList(Map<String, Object> requestMap) {
        return null;
    }

    @Override
    public Map<String, Object> getDetail(Map<String, Object> requestMap) {
        return null;
    }

}
