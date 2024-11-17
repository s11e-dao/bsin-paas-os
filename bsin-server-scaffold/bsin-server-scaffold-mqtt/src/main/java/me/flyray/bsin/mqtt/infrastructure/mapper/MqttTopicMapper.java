package me.flyray.bsin.mqtt.infrastructure.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import me.flyray.bsin.mqtt.domain.entity.MqttTopic;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * @author leonard
 * @description 针对表【iot_mqtt_topic】的数据库操作Mapper
 * @createDate 2024-11-17 23:06:17 @Entity me.flyray.bsin.mqtt.domain.entity.MqttTopic
 */
@Repository
@Mapper
public interface MqttTopicMapper extends BaseMapper<MqttTopic> {}
