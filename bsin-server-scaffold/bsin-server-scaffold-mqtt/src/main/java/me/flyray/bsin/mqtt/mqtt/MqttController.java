package me.flyray.bsin.mqtt.mqtt;

import me.flyray.bsin.mqtt.handler.MqttMsgPublisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: MqttController
 * @Description: 测试控制类
 * @Author: jodi
 * @Date: 2021/6/29 15:39
 * @Version: 1.0
 */
@RestController
@ConditionalOnProperty(value = "mqtt.enabled", havingValue = "true")
@RequestMapping("/mqtt")
public class MqttController {

    private Logger logger = LoggerFactory.getLogger(MqttController.class);

    private MqttMsgPublisher publisher;

    public MqttController(MqttMsgPublisher mqttMsgPublisher) {
        this.publisher = mqttMsgPublisher;
    }

    /**
     * 推送消息
     * @param topic 主题
     * @param payload 消息
     */
    @GetMapping("/publish")
    public void publish(String topic, String payload){
        this.publisher.publishMsg(topic, payload);
        logger.info("推送主题-->{},推送消息-->{}", topic, payload);
    }
}
