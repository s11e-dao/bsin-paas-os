package me.flyray.bsin.mqtt.facade.service;

import me.flyray.bsin.mqtt.domain.request.CommonTopicReceiver;
import org.springframework.messaging.MessageHeaders;

/**
 * @author leonard
 * @version 0.1
 * @date 2024/11/17
 */
public interface EnventsTestService {

  CommonTopicReceiver handleInboundTest1(CommonTopicReceiver receiver, MessageHeaders headers);

  CommonTopicReceiver handleInboundTest1Reply(CommonTopicReceiver receiver, MessageHeaders headers);

  void handleOutboundTest(CommonTopicReceiver receiver, MessageHeaders headers);

  void handleOutboundTestReply(CommonTopicReceiver receiver, MessageHeaders headers);
}
