package me.flyray.bsin.mqtt.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import me.flyray.bsin.mqtt.domain.channal.ChannelName;
import me.flyray.bsin.mqtt.domain.request.CommonTopicReceiver;
import me.flyray.bsin.mqtt.domain.request.RequestsReply;
import me.flyray.bsin.mqtt.domain.response.CommonTopicResponse;
import me.flyray.bsin.mqtt.facade.service.EnventsTestService;
import me.flyray.bsin.mqtt.facade.service.MessageSenderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.messaging.MessageHeaders;
import org.springframework.stereotype.Service;

/**
 * @author leonard
 * @version 0.1
 * @date 2024/11/17
 */
@Service
@Slf4j
public class EnventsTestServiceImpl implements EnventsTestService {

  @Autowired private MessageSenderService messageSenderService;

  @Autowired private ObjectMapper mapper;

  @Override
  @ServiceActivator(
      inputChannel = ChannelName.INBOUND_TASK_TEST1,
      outputChannel = ChannelName.OUTBOUND_TEST)
  public CommonTopicReceiver handleInboundTest1(
      CommonTopicReceiver receiver, MessageHeaders headers) {
    String dockSn = receiver.getGateway();
    log.info("handleInboundTest1");
    log.info("dockSn:{}", dockSn);
    log.info("receiver:{}", receiver);
    log.info("headers:{}", headers);
    return receiver;
  }

  @ServiceActivator(inputChannel = ChannelName.OUTBOUND_TEST)
  @Override
  public void handleOutboundTest(CommonTopicReceiver receiver, MessageHeaders headers) {
    log.info("handleOutboundTest 处理不用回复消息的通道消息");
    log.info("receiver:{}", receiver);
    log.info("headers:{}", headers);
  }

  @Override
  @ServiceActivator(
      inputChannel = ChannelName.INBOUND_TASK_TEST2,
      outputChannel = ChannelName.OUTBOUND_TEST_REPLY)
  public CommonTopicReceiver handleInboundTest1Reply(
      CommonTopicReceiver receiver, MessageHeaders headers) {
    String dockSn = receiver.getGateway();
    log.info("handleInboundTest1");
    log.info("dockSn:{}", dockSn);
    log.info("receiver:{}", receiver);
    log.info("headers:{}", headers);
    return receiver;
  }

  @ServiceActivator(
      inputChannel = ChannelName.OUTBOUND_TEST_REPLY,
      outputChannel = ChannelName.OUTBOUND)
  @Override
  public void handleOutboundTestReply(CommonTopicReceiver receiver, MessageHeaders headers) {
    log.info("handleOutboundTest");
    log.info("receiver:{}", receiver);
    log.info("headers:{}", headers);
    CommonTopicResponse<Object> build =
        CommonTopicResponse.builder()
            .tid("receiver.getTid()")
            .bid("receiver.getBid()")
            .method("reply")
            .timestamp(System.currentTimeMillis())
            .data(RequestsReply.success())
            .build();
    messageSenderService.publish("envents_test/response", build);
  }
}
