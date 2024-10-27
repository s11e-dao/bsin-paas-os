package me.flyray.bsin.server.impl;

import lombok.extern.slf4j.Slf4j;
import me.flyray.bsin.context.BsinServiceContext;
import me.flyray.bsin.domain.entity.MerchantHyperledgerParam;
import me.flyray.bsin.facade.service.MerchantHyperledgerService;
import me.flyray.bsin.infrastructure.mapper.MerchantHyperledgerParamMapper;
import org.apache.commons.collections4.MapUtils;
import org.apache.shenyu.client.apache.dubbo.annotation.ShenyuDubboService;
import org.apache.shenyu.client.apidocs.annotations.ApiDoc;
import org.apache.shenyu.client.apidocs.annotations.ApiModule;
import org.apache.shenyu.client.dubbo.common.annotation.ShenyuDubboClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * @author bolei
 * @date 2023/6/28 16:33
 * @desc
 */

@ShenyuDubboService(path = "/hyperledgerSetting", timeout = 6000)
@ApiModule(value = "hyperledgerSetting")
@Service
@Slf4j
public class MerchantHyperledgerParamServiceImpl implements MerchantHyperledgerService {

    @Autowired
    private MerchantHyperledgerParamMapper hyperledgerSettingMapper;

    @ApiDoc(desc = "setting")
    @ShenyuDubboClient("/setting")
    @Override
    public MerchantHyperledgerParam setting(Map<String, Object> requestMap) {
        MerchantHyperledgerParam hyperledgerSetting =  BsinServiceContext.getReqBodyDto(MerchantHyperledgerParam.class, requestMap);
        hyperledgerSettingMapper.insert(hyperledgerSetting);
        return hyperledgerSetting;
    }

    @ApiDoc(desc = "edit")
    @ShenyuDubboClient("/edit")
    @Override
    public MerchantHyperledgerParam edit(Map<String, Object> requestMap) {
        MerchantHyperledgerParam hyperledgerSetting =  BsinServiceContext.getReqBodyDto(MerchantHyperledgerParam.class, requestMap);
        hyperledgerSettingMapper.updateById(hyperledgerSetting);
        return hyperledgerSetting;
    }

    @ApiDoc(desc = "getDetail")
    @ShenyuDubboClient("/getDetail")
    @Override
    public MerchantHyperledgerParam getDetail(Map<String, Object> requestMap) {
        String serialNo = MapUtils.getString(requestMap, "serialNo");
        MerchantHyperledgerParam hyperledgerSetting = hyperledgerSettingMapper.selectById(serialNo);
        return hyperledgerSetting;
    }
}
