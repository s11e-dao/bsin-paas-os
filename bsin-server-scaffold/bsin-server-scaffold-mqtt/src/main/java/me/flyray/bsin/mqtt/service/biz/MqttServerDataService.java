package me.flyray.bsin.mqtt.service.biz;

import me.flyray.bsin.mqtt.domain.entity.MqttServerData;
import me.flyray.bsin.mqtt.domain.entity.MqttServerDataQueryDto;

import java.util.List;

public interface MqttServerDataService {
    List<MqttServerData> findByList(MqttServerDataQueryDto mqttServerDataQueryDto);

    void initMqttServer(MqttServerData mqttServerData);

}
