package me.flyray.bsin.mqtt.service.config.model;

import lombok.Data;

/**
 * @author leonard
 * @date 2024/11/17
 * @version 0.1
 */
@Data
public class MqttClientOptions {

    private MqttProtocolEnum protocol;

    private String host;

    private Integer port;

    private String username;

    private String password;

    private String clientId;

    private String path;

    /**
     * The topic to subscribe to immediately when client connects. Only required for basic link.
     */
    private String inboundTopic;
}
