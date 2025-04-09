package me.flyray.bsin.server.handler;

import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Component;

/**
 * @author bolei
 * @date 2024/5/20
 * @desc
 */
@Component
//@RocketMQMessageListener(consumerGroup = "consumer_group",topic = "waas-test")
public class RocketMQConsumer implements RocketMQListener<String> {
    @Override
    public void onMessage(String message) {
        // 处理消息的逻辑
        System.out.println("Received message: " + message);
    }
}

