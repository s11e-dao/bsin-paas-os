package me.flyray.bsin.mqtt.service.controller;

import lombok.extern.slf4j.Slf4j;
import me.flyray.bsin.mqtt.domain.request.RequestsReply;
import me.flyray.bsin.mqtt.domain.response.CommonTopicResponse;
import me.flyray.bsin.mqtt.facade.service.MessageSenderService;
import org.apache.shenyu.client.apache.dubbo.annotation.ShenyuDubboService;
import org.apache.shenyu.client.apidocs.annotations.ApiModule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * @author leonard
 * @date 2024/11/17
 * @version 0.1
 * @description mqtt topic 发布订阅
 */
@Slf4j
@ShenyuDubboService(path = "/mqttTopic", timeout = 6000)
@ApiModule(value = "mqttTopic")
@Service
public class MqttTopicController {

  @Autowired private MessageSenderService messageSenderService;

  @GetMapping("/pulish")
  public String pulish(String topic) {
    CommonTopicResponse<Object> build =
        CommonTopicResponse.builder()
            .tid("receiver.getTid()")
            .bid("receiver.getBid()")
            .method("reply")
            .timestamp(System.currentTimeMillis())
            .data(RequestsReply.success())
            .build();
    messageSenderService.publish(topic, build);
    return "向" + topic + "发送消息";
  }

  @GetMapping("/reply")
  public CommonTopicResponse reply() {
    CommonTopicResponse<Object> build =
        CommonTopicResponse.builder()
            .tid("receiver.getTid()")
            .bid("receiver.getBid()")
            .method("reply")
            .timestamp(System.currentTimeMillis())
            .data(RequestsReply.success())
            .build();
    messageSenderService.publish("test/9876", build);
    return build;
  }
}
