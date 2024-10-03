package me.flyray.bsin.thirdauth.wx.utils;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.api.impl.WxMpServiceImpl;
import me.chanjar.weixin.mp.config.impl.WxMpDefaultConfigImpl;

@Service
public class BsinWxMpServiceUtil {

  private static ConcurrentHashMap<String, WxMpService> concurrentWxServiceHashMap =
      new ConcurrentHashMap();

  public WxMpService getWxMpService(
      WxMpProperties.MpConfig mpConfig, WxRedisConfig redisConfig) {

    WxMpService wxService;
    wxService = (WxMpService) concurrentWxServiceHashMap.get(mpConfig.getAppId());

    if (null != wxService) {
      return wxService;
    }

    final List<WxMpProperties.MpConfig> configs = new ArrayList<>();
    configs.add(mpConfig);
    if (configs == null) {
      throw new RuntimeException("配资文件未生效！");
    }
    boolean isUseRedis = false;
    wxService = new WxMpServiceImpl();
    wxService.setMultiConfigStorages(
        configs.stream()
            .map(
                a -> {
                  WxMpDefaultConfigImpl configStorage;
//                  if (redisConfig != null) {
//                    JedisPoolConfig poolConfig = new JedisPoolConfig();
//                    JedisPool jedisPool =
//                        new JedisPool(
//                            poolConfig,
//                            redisConfig.getHost(),
//                            redisConfig.getPort(),
//                            redisConfig.getTimeout(),
//                            redisConfig.getPassword());
//                    configStorage =
//                        new WxMpRedisConfigImpl(new JedisWxRedisOps(jedisPool), a.getAppId());
//                  } else
                  {
                    configStorage = new WxMpDefaultConfigImpl();
                  }
                  configStorage.setAppId(a.getAppId());
                  configStorage.setSecret(a.getSecret());
                  configStorage.setToken(a.getToken());
                  configStorage.setAesKey(a.getAesKey());
                  //                    configStorage.set();
                  return configStorage;
                })
            .collect(Collectors.toMap(WxMpDefaultConfigImpl::getAppId, a -> a, (o, n) -> o)));
    concurrentWxServiceHashMap.put(mpConfig.getAppId(), wxService);
    return wxService;
  }
}
