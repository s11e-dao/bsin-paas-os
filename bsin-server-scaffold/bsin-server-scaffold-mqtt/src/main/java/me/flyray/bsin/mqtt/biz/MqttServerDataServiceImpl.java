package me.flyray.bsin.mqtt.biz;

import me.flyray.bsin.mqtt.domain.MqttServerData;
import me.flyray.bsin.mqtt.domain.MqttServerDataQueryDto;
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
