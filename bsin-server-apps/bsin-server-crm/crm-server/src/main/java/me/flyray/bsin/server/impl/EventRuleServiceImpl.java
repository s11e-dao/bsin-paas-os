package me.flyray.bsin.server.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.shenyu.client.apache.dubbo.annotation.ShenyuDubboService;
import org.apache.shenyu.client.apidocs.annotations.ApiDoc;
import org.apache.shenyu.client.apidocs.annotations.ApiModule;
import org.apache.shenyu.client.dubbo.common.annotation.ShenyuDubboClient;
import org.springframework.stereotype.Service;

import java.util.Map;

import lombok.extern.slf4j.Slf4j;
import me.flyray.bsin.facade.service.EventRuleService;

/**
 * @author bolei
 * @date 2023/7/27 9:43
 * @desc
 */


@Slf4j
@ShenyuDubboService(path = "/eventRule", timeout = 6000)
@ApiModule(value = "eventRule")
@Service
public class EventRuleServiceImpl implements EventRuleService {


    @ApiDoc(desc = "add")
    @ShenyuDubboClient("/add")
    @Override
    public Map<String, Object> add(Map<String, Object> requestMap) {
        return null;
    }

    @ApiDoc(desc = "delete")
    @ShenyuDubboClient("/delete")
    @Override
    public Map<String, Object> delete(Map<String, Object> requestMap) {
        return null;
    }

    @ApiDoc(desc = "getList")
    @ShenyuDubboClient("/getList")
    @Override
    public Map<String, Object> getList(Map<String, Object> requestMap) {
        return null;
    }

    @ApiDoc(desc = "getPageList")
    @ShenyuDubboClient("/getPageList")
    @Override
    public IPage<?> getPageList(Map<String, Object> requestMap) {
        return null;
    }

    @ApiDoc(desc = "getDetail")
    @ShenyuDubboClient("/getDetail")
    @Override
    public Map<String, Object> getDetail(Map<String, Object> requestMap) {
        return null;
    }

}
