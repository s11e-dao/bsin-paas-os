package me.flyray.bsin.thirdauth.wx.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.binarywang.wx.miniapp.api.impl.WxMaServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class BsinWxMaServiceUtil {

  private static ConcurrentHashMap<String, WxMaService> concurrentWxServiceHashMap =
      new ConcurrentHashMap();

  public WxMaService getWxMaService(
      WxMaProperties.MaConfig maConfig, WxMaProperties.RedisConfig redisConfig) {

    WxMaService wxMaService;
      wxMaService = (WxMaService) concurrentWxServiceHashMap.get(maConfig.getAppId());

    if (null != wxMaService) {
      return wxMaService;
    }
    final List<WxMaProperties.MaConfig> configs = new ArrayList<>();
    configs.add(maConfig);
    if (configs == null) {
      throw new RuntimeException("配资文件未生效！");
    }
    boolean isUseRedis = false;
      wxMaService = new WxMaServiceImpl();

    concurrentWxServiceHashMap.put(maConfig.getAppId(), wxMaService);
    return wxMaService;
  }
}
