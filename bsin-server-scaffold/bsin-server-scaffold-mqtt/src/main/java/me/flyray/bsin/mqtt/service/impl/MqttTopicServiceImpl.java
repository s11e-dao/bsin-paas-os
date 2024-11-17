package me.flyray.bsin.mqtt.service.impl;

import javax.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import me.flyray.bsin.mqtt.facade.IMqttTopicService;
import org.springframework.integration.mqtt.inbound.MqttPahoMessageDrivenChannelAdapter;
import org.springframework.stereotype.Component;

/**
 * @author sean.zhou
 * @date 2021/11/10
 * @version 0.1
 */
@Component
@Slf4j
public class MqttTopicServiceImpl implements IMqttTopicService {

  @Resource private MqttPahoMessageDrivenChannelAdapter adapter;

  @Override
  public void subscribe(String topic) {
    log.debug("subscribe topic: {}", topic);
    adapter.addTopic(topic);
  }

  @Override
  public void subscribe(String topic, int qos) {
    log.debug("subscribe topic: {}", topic);
    adapter.addTopic(topic, qos);
  }

  @Override
  public void unsubscribe(String topic) {
    log.debug("unsubscribe topic: {}", topic);
    adapter.removeTopic(topic);
  }

  public String[] getSubscribedTopic() {
    return adapter.getTopic();
  }
}
