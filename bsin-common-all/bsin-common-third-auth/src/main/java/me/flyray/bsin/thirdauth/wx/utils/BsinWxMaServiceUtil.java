package me.flyray.bsin.thirdauth.wx.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.binarywang.wx.miniapp.api.impl.WxMaServiceImpl;
import cn.binarywang.wx.miniapp.config.impl.WxMaDefaultConfigImpl;
import me.chanjar.weixin.common.error.WxRuntimeException;
import me.chanjar.weixin.cp.config.impl.WxCpDefaultConfigImpl;
import org.springframework.stereotype.Service;

@Service
public class BsinWxMaServiceUtil {

  private static ConcurrentHashMap<String, WxMaService> concurrentWxServiceHashMap =
      new ConcurrentHashMap();

  public WxMaService getWxMaService(WxMaProperties.MaConfig maConfig, WxRedisConfig redisConfig) {

    WxMaService wxMaService;
    wxMaService = (WxMaService) concurrentWxServiceHashMap.get(maConfig.getAppId());

    if (null != wxMaService) {
      return wxMaService;
    }
    final List<WxMaProperties.MaConfig> configs = new ArrayList<>();
    configs.add(maConfig);
    if (configs == null) {
      throw new RuntimeException("配置文件未生效！");
    }
    wxMaService = new WxMaServiceImpl();
    wxMaService.setMultiConfigs(
        configs.stream()
            .map(
                a -> {
                  WxMaDefaultConfigImpl config = new WxMaDefaultConfigImpl();
                  //                WxMaDefaultConfigImpl config = new WxMaRedisConfigImpl(new
                  // JedisPool());
                  // 使用上面的配置时，需要同时引入jedis-lock的依赖，否则会报类无法找到的异常
                  config.setAppid(a.getAppId());
                  config.setSecret(a.getSecret());
                  config.setToken(a.getToken());
                  config.setAesKey(a.getAesKey());
                  config.setMsgDataFormat(a.getMsgDataFormat());
                  return config;
                })
            .collect(Collectors.toMap(WxMaDefaultConfigImpl::getAppid, a -> a, (o, n) -> o)));
    concurrentWxServiceHashMap.put(maConfig.getAppId(), wxMaService);
    return wxMaService;
  }
}
