package me.flyray.bsin.mqtt.service.biz;

import me.flyray.bsin.mqtt.domain.entity.MqttServerData;
import me.flyray.bsin.mqtt.domain.entity.MqttServerDataQueryDto;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class MqttServerDataServiceImpl implements MqttServerDataService{


    @Override
    public List<MqttServerData> findByList(MqttServerDataQueryDto mqttServerDataQueryDto) {
        return null;
    }

    @Override
    public void initMqttServer(MqttServerData mqttServerData) {

    }
}
