package me.flyray.bsin.mqtt.biz;

import me.flyray.bsin.mqtt.domain.MqttServerData;
import me.flyray.bsin.mqtt.domain.MqttServerDataQueryDto;

import java.util.List;

public interface MqttServerDataService {
    List<MqttServerData> findByList(MqttServerDataQueryDto mqttServerDataQueryDto);

    void initMqttServer(MqttServerData mqttServerData);

}
