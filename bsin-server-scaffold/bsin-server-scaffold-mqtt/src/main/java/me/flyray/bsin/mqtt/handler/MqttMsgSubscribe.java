package me.flyray.bsin.mqtt.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.integration.mqtt.support.MqttHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.MessagingException;
import org.springframework.stereotype.Component;

/**
 * @ClassName: MqttMsgSubscribe
 * @Description: mqtt订阅处理
 * @Author: jodi
 * @Date: 2021/6/29 15:47
 * @Version: 1.0
 */
@Component
@ConditionalOnProperty(value = "mqtt.enabled", havingValue = "true")
public class MqttMsgSubscribe implements MessageHandler {

    private Logger logger = LoggerFactory.getLogger(MqttMsgSubscribe.class);

    @Override
    public void handleMessage(Message<?> message) throws MessagingException {
        String id = String.valueOf(message.getHeaders().get(MqttHeaders.ID));
        String topic = String.valueOf(message.getHeaders().get(MqttHeaders.RECEIVED_TOPIC));
        String payload = String.valueOf(message.getPayload());
        logger.info("Mqtt服务器ID-->{},订阅主题-->{},收到消息-->{}", id, topic, payload);
    }
}

