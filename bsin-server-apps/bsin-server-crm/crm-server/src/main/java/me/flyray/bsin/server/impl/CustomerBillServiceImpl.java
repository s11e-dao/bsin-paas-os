package me.flyray.bsin.server.impl;

import lombok.extern.slf4j.Slf4j;
import me.flyray.bsin.facade.service.CustomerBillService;
import org.apache.shenyu.client.apache.dubbo.annotation.ShenyuDubboService;
import org.apache.shenyu.client.apidocs.annotations.ApiDoc;
import org.apache.shenyu.client.apidocs.annotations.ApiModule;
import org.apache.shenyu.client.dubbo.common.annotation.ShenyuDubboClient;
import org.springframework.stereotype.Service;

import java.util.Map;

@Slf4j
@ShenyuDubboService(path = "/customerBill", timeout = 6000)
@ApiModule(value = "customerBill")
@Service
public class CustomerBillServiceImpl implements CustomerBillService {

    @ApiDoc(desc = "getPageList")
    @ShenyuDubboClient("/getPageList")
    @Override
    public Map<String, Object> getPageList(Map<String, Object> requestMap) {
        return null;
    }

    @ApiDoc(desc = "getHyperledgerSummaryData")
    @ShenyuDubboClient("/getHyperledgerSummaryData")
    @Override
    public Map<String, Object> getHyperledgerSummaryData(Map<String, Object> requestMap) {
        return null;
    }

}
