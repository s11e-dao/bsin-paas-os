package me.flyray.bsin.mqtt.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author ：bolei
 * @description： Mqtt配置表
 * @date ：2024/7/18 8:51
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class MqttServerData {

    private String serverHost;

    private String serverPort;

    private String userName;

    private String password;

    private String clientId;

    /**
     * mqtt订阅的主体，以“，”隔开
     */
    private String clientTopic;

    /**
     * 客户端每次重连是否清除session
     */
    private String clientCleanSession;

    private String remark;

    private Boolean enableFlag;

}
