package me.flyray.bsin.server.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.extern.slf4j.Slf4j;
import me.flyray.bsin.domain.entity.SysAgent;
import me.flyray.bsin.facade.service.SysAgentService;
import me.flyray.bsin.infrastructure.mapper.SysAgentMapper;
import org.apache.shenyu.client.apache.dubbo.annotation.ShenyuDubboService;
import org.apache.shenyu.client.apidocs.annotations.ApiModule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * 系统代理商服务
 */

@Slf4j
@ShenyuDubboService(path = "/sysAgent",timeout = 15000)
@ApiModule(value = "sysAgent")
@Service
public class SysAgentServiceImpl implements SysAgentService {

    @Autowired
    SysAgentMapper sysAgentMapper;

    @Override
    public SysAgent add(Map<String, Object> requestMap) {
        return null;
    }

    @Override
    public void delete(Map<String, Object> requestMap) {

    }

    @Override
    public SysAgent edit(Map<String, Object> requestMap) {
        return null;
    }

    @Override
    public IPage<?> getPageList(Map<String, Object> requestMap) {
        return null;
    }

    @Override
    public SysAgent getDetail(Map<String, Object> requestMap) {
        return null;
    }
}
