package me.flyray.bsin.mqtt.service.config.model;

import lombok.Getter;

/**
 * @author leonard
 * @date 2024/11/17
 * @version 0.1
 */
@Getter
public enum MqttProtocolEnum {

    MQTT("tcp"),

    MQTTS("tcp"),

    WS("ws"),

    WSS("wss");

    String protocol;

    MqttProtocolEnum(String protocol) {
        this.protocol = protocol;
    }

    public String getProtocolAddr() {
        return protocol + "://";
    }
}
