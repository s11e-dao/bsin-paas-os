package me.flyray.bsin.mqtt.facade;

import me.flyray.bsin.mqtt.domain.request.CommonTopicReceiver;
import org.springframework.messaging.MessageHeaders;

/**
 * @author sean
 * @version 1.2
 * @date 2022/7/29
 */
public interface EnventsTestService {

  CommonTopicReceiver handleInboundTest1(CommonTopicReceiver receiver, MessageHeaders headers);

  CommonTopicReceiver handleInboundTest1Reply(CommonTopicReceiver receiver, MessageHeaders headers);

  void handleOutboundTest(CommonTopicReceiver receiver, MessageHeaders headers);

  void handleOutboundTestReply(CommonTopicReceiver receiver, MessageHeaders headers);
}
