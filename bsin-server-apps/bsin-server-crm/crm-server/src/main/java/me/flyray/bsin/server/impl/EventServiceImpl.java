package me.flyray.bsin.server.impl;

import lombok.extern.slf4j.Slf4j;
import me.flyray.bsin.domain.entity.Event;
import me.flyray.bsin.facade.service.EventService;
import org.apache.shenyu.client.apache.dubbo.annotation.ShenyuDubboService;
import org.apache.shenyu.client.apidocs.annotations.ApiModule;
import org.springframework.stereotype.Service;

import java.util.Map;

@Slf4j
@ShenyuDubboService(path = "/event", timeout = 6000)
@ApiModule(value = "event")
@Service
public class EventServiceImpl implements EventService {


    @Override
    public Event add(Map<String, Object> requestMap) {
        return null;
    }

    @Override
    public void delete(Map<String, Object> requestMap) {

    }

    @Override
    public void edit(Map<String, Object> requestMap) {

    }

    @Override
    public Event getDetail(Map<String, Object> requestMap) {
        return null;
    }
}
