package me.flyray.bsin.mqtt.service.controller;

import me.flyray.bsin.mqtt.domain.request.RequestsReply;
import me.flyray.bsin.mqtt.domain.response.CommonTopicResponse;
import me.flyray.bsin.mqtt.facade.IMessageSenderService;
import me.flyray.bsin.mqtt.facade.IMqttTopicService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author yihui wang
 * @version 1.0
 * @description: TODO
 * @date 2023/7/26 16:05
 */
@RestController
@RequestMapping("/topic")
public class MqttTopicController {

  @Autowired private IMqttTopicService mqttTopicService;

  @Autowired private IMessageSenderService messageSenderService;

  @GetMapping("/add")
  public String add(String topic) {
    mqttTopicService.subscribe(topic);
    return topic + "添加成功";
  }

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
