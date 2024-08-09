package me.flyray.bsin.mqtt.config;

import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;

/**
 * MQTT连接配置
 */
@Slf4j
@Component
public class MqttClientConnect {

    private MqttClient mqttClient;

    /**
     * 系统的mqtt客户端id
     */
    private String mqttClientId;

    /**
     * 客户端列表
     */
    public static ConcurrentHashMap<String, MqttClientConnect> mqttClients = new ConcurrentHashMap();

    /**
     * 客户端connect连接mqtt服务器
     *
     * @param userName     用户名
     * @param passWord     密码
     * @param mqttCallback 回调函数
     **/
    public void setMqttClient(String host, String clientId, String userName, String passWord, boolean cleanSession, MqttCallback mqttCallback) throws MqttException {
        MqttConnectOptions options = mqttConnectOptions(host, clientId, userName, passWord, cleanSession);
        mqttClient.setCallback(mqttCallback);
        mqttClient.connect(options);
    }

    /**
     * MQTT连接参数设置
     */
    private MqttConnectOptions mqttConnectOptions(String host, String clientId, String userName, String passWord, boolean cleanSession) throws MqttException {
        mqttClient = new MqttClient(host, clientId, new MemoryPersistence());
        MqttConnectOptions options = new MqttConnectOptions();
        options.setUserName(userName);
        options.setPassword(passWord.toCharArray());
        options.setConnectionTimeout(30);///默认：30
        options.setAutomaticReconnect(true);//默认：false
        options.setCleanSession(cleanSession);//默认：true
        //options.setKeepAliveInterval(20);//默认：60
        // 设置遗愿消息
        options.setWill("/lastWill", "device exception down".getBytes(), 1, false);
        return options;
    }

    /**
     * 关闭MQTT连接
     */
    public void close() throws MqttException {
//        mqttClient.close();
        mqttClient.disconnect();
    }

    /**
     * 向某个主题发布消息 默认qos：1
     *
     * @param topic:发布的主题
     * @param msg：发布的消息
     */
    public void pub(String topic, String msg) throws MqttException {
        MqttMessage mqttMessage = new MqttMessage();
        //mqttMessage.setQos(2);
        mqttMessage.setPayload(msg.getBytes());
        MqttTopic mqttTopic = mqttClient.getTopic(topic);
        MqttDeliveryToken token = mqttTopic.publish(mqttMessage);
        token.waitForCompletion();
    }

    /**
     * 向某个主题发布消息
     *
     * @param topic: 发布的主题
     * @param msg:   发布的消息
     * @param qos:   消息质量    Qos：0、1、2
     */
    public void pub(String topic, String msg, int qos) throws MqttException {
        MqttMessage mqttMessage = new MqttMessage();
        mqttMessage.setQos(qos);
        mqttMessage.setPayload(msg.getBytes());
        MqttTopic mqttTopic = mqttClient.getTopic(topic);
        MqttDeliveryToken token = mqttTopic.publish(mqttMessage);
        token.waitForCompletion();
    }

    /**
     * 订阅多个主题 ，此方法默认的的Qos等级为：1
     *
     * @param topic 主题
     */
    public void sub(String[] topic) throws MqttException {
        mqttClient.subscribe(topic);
    }

    /**
     * 订阅某一个主题，可携带Qos
     *
     * @param topic 所要订阅的主题
     * @param qos   消息质量：0、1、2
     */
    public void sub(String topic, int qos) throws MqttException {
        mqttClient.subscribe(topic, qos);
    }

    public String getMqttClientId() {
        return mqttClientId;
    }

    public void setMqttClientId(String mqttClientId) {
        this.mqttClientId = mqttClientId;
    }

}

