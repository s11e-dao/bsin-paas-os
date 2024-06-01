package me.flyray.bsin.server.handler;

import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Component;

/**
 * @author bolei
 * @date 2024/5/20
 * @desc
 */

@Slf4j
//@Component
//@RocketMQMessageListener(consumerGroup = "consumer_group",topic = "waas-test")
public class WaasMQConsumer implements RocketMQListener<String> {

    /**
     * 1、处理钱包创建消息
     * 2、处理加油确认消息
     * 3、确认交易确认消息
     * @param message
     */
    @Override
    public void onMessage(String message) {
        // 处理消息的逻辑
        log.info("Received message: " + message);
        // 如果是创建钱包队列，则请求MPC网络查询地址

        // 如果是gas加油，则发送归集交易

        // 如果是归集交易，则确认修改交易状态

    }
}

