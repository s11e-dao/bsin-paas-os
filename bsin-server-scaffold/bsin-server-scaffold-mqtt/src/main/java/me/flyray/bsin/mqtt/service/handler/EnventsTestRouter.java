package me.flyray.bsin.mqtt.service.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import me.flyray.bsin.mqtt.domain.enums.EnventsTestMethodEnum;
import me.flyray.bsin.mqtt.domain.enums.TopicConst;
import me.flyray.bsin.mqtt.domain.channal.ChannelName;
import me.flyray.bsin.mqtt.domain.request.CommonTopicReceiver;
import me.flyray.bsin.mqtt.domain.request.RequestsReply;
import me.flyray.bsin.mqtt.domain.response.CommonTopicResponse;
import me.flyray.bsin.mqtt.facade.service.MessageSenderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.mqtt.support.MqttHeaders;
import org.springframework.messaging.MessageHeaders;

/**
 * @author leonard
 * @date 2024/11/17
 * @version 0.1
 * @description 此处的过滤链相关的东西，没有进行实现啦，具体可参考大疆的开源项目
 */
@Configuration
@Slf4j
public class EnventsTestRouter {

  @Autowired private ObjectMapper mapper;

  @Autowired private MessageSenderService messageSenderService;

  @Bean
  public IntegrationFlow myTestMethodRouterFlow() {
    return IntegrationFlow.from(ChannelName.ENVENTS_INBOUND_TEST)
        .<byte[], CommonTopicReceiver>transform(
            payload -> {
              try {
                return mapper.readValue(payload, CommonTopicReceiver.class);
              } catch (IOException e) {
                e.printStackTrace();
              }
              return new CommonTopicReceiver();
            })
        .<CommonTopicReceiver, EnventsTestMethodEnum>route(
            receiver -> EnventsTestMethodEnum.find(receiver.getMethod()),
            mapping ->
                Arrays.stream(EnventsTestMethodEnum.values())
                    .forEach(
                        methodEnum ->
                            mapping.channelMapping(methodEnum, methodEnum.getChannelName())))
        .get();
  }

  @ServiceActivator(
      inputChannel = ChannelName.REPLY_EVENTS_OUTBOUND,
      outputChannel = ChannelName.OUTBOUND)
  public void replyEventsOutbound(CommonTopicReceiver receiver, MessageHeaders headers) {
    if (Optional.ofNullable(receiver)
        .map(CommonTopicReceiver::getNeedReply)
        .flatMap(val -> Optional.of(1 != val))
        .orElse(true)) {
      return;
    }
    messageSenderService.publish(
        headers.get(MqttHeaders.RECEIVED_TOPIC) + TopicConst._REPLY_SUF,
        CommonTopicResponse.builder()
            .tid(receiver.getTid())
            .bid(receiver.getBid())
            .method(receiver.getMethod())
            .timestamp(System.currentTimeMillis())
            .data(RequestsReply.success())
            .build());
  }
}
