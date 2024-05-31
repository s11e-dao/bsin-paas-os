package me.flyray.bsin.server.impl;

import org.apache.shenyu.client.apache.dubbo.annotation.ShenyuDubboService;
import org.apache.shenyu.client.apidocs.annotations.ApiDoc;
import org.apache.shenyu.client.apidocs.annotations.ApiModule;
import org.apache.shenyu.client.dubbo.common.annotation.ShenyuDubboClient;
import org.springframework.stereotype.Service;

import java.util.Map;

import lombok.extern.slf4j.Slf4j;
import me.flyray.bsin.facade.service.MerchantSubscribeJournalService;

/**
 * @author bolei
 * @date 2023/11/8
 * @desc
 */

@Slf4j
@ShenyuDubboService(path = "/merchantSubscribeJournal", timeout = 6000)
@ApiModule(value = "merchantSubscribeJournal")
@Service
public class MerchantSubscribeJournalServiceImpl implements MerchantSubscribeJournalService {

    @ApiDoc(desc = "getPageList")
    @ShenyuDubboClient("/getPageList")
    @Override
    public Map<String, Object> getPageList(Map<String, Object> requestMap) {
        return null;
    }

}
