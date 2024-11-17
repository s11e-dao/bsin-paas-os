package me.flyray.bsin.mqtt.facade.service;

import me.flyray.bsin.mqtt.domain.channal.ChannelName;
import org.springframework.integration.annotation.MessagingGateway;
import org.springframework.integration.mqtt.support.MqttHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

/**
 * @author leonard
 * @version 0.1
 * @date 2024/11/17
 */
@Component
@MessagingGateway(defaultRequestChannel = ChannelName.OUTBOUND)
public interface MqttMessageGateway {

    /**
     * Publish a message to a specific topic.
     * @param topic target
     * @param payload   message
     */
    void publish(@Header(MqttHeaders.TOPIC) String topic, byte[] payload);

    /**
     * Use a specific qos to push messages to a specific topic.
     * @param topic     target
     * @param payload   message
     * @param qos   qos
     */
    void publish(@Header(MqttHeaders.TOPIC) String topic, byte[] payload, @Header(MqttHeaders.QOS) int qos);
}
