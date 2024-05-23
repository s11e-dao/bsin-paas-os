package me.flyray.bsin.thirdauth.wx.utils;

import me.chanjar.weixin.cp.api.WxCpService;
import me.chanjar.weixin.cp.api.impl.WxCpServiceImpl;
import me.chanjar.weixin.cp.config.impl.WxCpDefaultConfigImpl;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class BsinWxCpServiceUtil {

    private static ConcurrentHashMap<String, WxCpService> concurrentWxServiceHashMap = new ConcurrentHashMap();

    public WxCpService getWxCpService(WxCpProperties.CpConfig cpConfig) {

        WxCpService wxCpService;
        wxCpService = (WxCpService) concurrentWxServiceHashMap.get(cpConfig.getCorpId() + cpConfig.getAgentId());

        if (null != wxCpService) {
            return wxCpService;
        }

        final List<WxCpProperties.CpConfig> configs = new ArrayList<>();
        configs.add(cpConfig);
        if (configs == null) {
            throw new RuntimeException("配资文件未生效！");
        }
        boolean isUseRedis = false;
        wxCpService = new WxCpServiceImpl();
        WxCpDefaultConfigImpl configStorage = new WxCpDefaultConfigImpl();
        configStorage.setAgentId(cpConfig.getAgentId());
        configStorage.setCorpId(cpConfig.getCorpId());
        configStorage.setToken(cpConfig.getToken());
        configStorage.setAesKey(cpConfig.getAesKey());
        wxCpService.setWxCpConfigStorage(configStorage);
        concurrentWxServiceHashMap.put(cpConfig.getCorpId() + cpConfig.getAgentId(), wxCpService);
        return wxCpService;
    }


}
