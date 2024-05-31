package me.flyray.bsin.server.impl;

import org.apache.shenyu.client.apache.dubbo.annotation.ShenyuDubboService;
import org.apache.shenyu.client.apidocs.annotations.ApiDoc;
import org.apache.shenyu.client.apidocs.annotations.ApiModule;
import org.apache.shenyu.client.dubbo.common.annotation.ShenyuDubboClient;
import org.springframework.stereotype.Service;

import java.util.Map;

import lombok.extern.slf4j.Slf4j;
import me.flyray.bsin.facade.service.QrCodeService;

/**
 * @author bolei
 * @date 2023/7/18 16:18
 * @desc
 */

@Slf4j
@ShenyuDubboService(path = "/qrCode", timeout = 6000)
@ApiModule(value = "qrCode")
@Service
public class QrCodeServiceImpl implements QrCodeService {

    @ShenyuDubboClient("/generate")
    @ApiDoc(desc = "generate")
    @Override
    public Map<String, Object> generate(Map<String, Object> requestMap) {
        return null;
    }

    @ShenyuDubboClient("/verify")
    @ApiDoc(desc = "verify")
    @Override
    public Map<String, Object> verify(Map<String, Object> requestMap) {
        return null;
    }
}
