package me.flyray.bsin.mqtt.service.listener;

import lombok.extern.slf4j.Slf4j;
import me.flyray.bsin.mqtt.service.biz.MqttServerDataService;
import me.flyray.bsin.mqtt.domain.entity.MqttServerData;
import me.flyray.bsin.mqtt.domain.entity.MqttServerDataQueryDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

/** 项目启动 监听主题 */
@Slf4j
@Component
public class MqttClientListener implements ApplicationListener<ContextRefreshedEvent> {

  //    @Value("${spring.profiles.active}")
  //    private String active;

  @Autowired private MqttServerDataService mqttServerDataService;

  //  /** 客户端 */
  //  public static ConcurrentHashMap<String, MqttClientConnect> mqttClients = new
  // ConcurrentHashMap();

  private final AtomicBoolean isInit = new AtomicBoolean(false);

  @Override
  public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
    // 防止重复触发
    if (!isInit.compareAndSet(false, true)) {
      return;
    }
    // TODO 需要传入启用参数
    List<MqttServerData> mqttServers =
        mqttServerDataService.findByList(
            new MqttServerDataQueryDto() {
              {
                setEnableFlag(Boolean.TRUE);
              }
            });
    try {
      if (!CollectionUtils.isEmpty(mqttServers)) {
        for (MqttServerData mqttServerData : mqttServers) {
          mqttServerDataService.initMqttServer(mqttServerData);
        }
      }
    } catch (Exception e) {
      log.error(e.getMessage(), e);
    }
  }
}
