package me.flyray.bsin.server.impl;

import lombok.extern.slf4j.Slf4j;
import me.flyray.bsin.facade.service.SysAgentModelService;
import org.apache.shenyu.client.apache.dubbo.annotation.ShenyuDubboService;
import org.apache.shenyu.client.apidocs.annotations.ApiModule;
import org.springframework.stereotype.Service;

@Slf4j
@ShenyuDubboService(path = "/sysAgentModel", timeout = 6000)
@ApiModule(value = "sysAgentModel")
@Service
public class SysAgentModelServiceImpl implements SysAgentModelService {


}
