package me.flyray.bsin.server.impl;

import lombok.extern.slf4j.Slf4j;
import me.flyray.bsin.domain.entity.EventModel;
import me.flyray.bsin.facade.service.EventModelService;
import org.apache.shenyu.client.apache.dubbo.annotation.ShenyuDubboService;
import org.apache.shenyu.client.apidocs.annotations.ApiModule;
import org.springframework.stereotype.Service;

import java.util.Map;

@Slf4j
@ShenyuDubboService(path = "/eventModel", timeout = 6000)
@ApiModule(value = "eventModel")
@Service
public class EventModelServiceImpl implements EventModelService {


    @Override
    public EventModel add(Map<String, Object> requestMap) {
        return null;
    }

    @Override
    public void delete(Map<String, Object> requestMap) {

    }

    @Override
    public void edit(Map<String, Object> requestMap) {

    }

    @Override
    public EventModel getDetail(Map<String, Object> requestMap) {
        return null;
    }

}
