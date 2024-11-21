package me.flyray.bsin.mqtt.service.config;

import java.util.concurrent.Executor;

import me.flyray.bsin.mqtt.domain.channal.ChannelName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.channel.ExecutorChannel;
import org.springframework.messaging.MessageChannel;

/**
 * Definition classes for all channels
 *
 * @author leonard
 * @date 2024/11/17
 * @version 0.1
 */
@Configuration
public class MqttMessageChannel {

  @Autowired private Executor threadPool;

  @Bean(name = ChannelName.INBOUND)
  public MessageChannel inboundChannel() {
    return new ExecutorChannel(threadPool);
  }

  @Bean(name = ChannelName.ENVENTS_INBOUND_TEST)
  public MessageChannel enventsInboundTest() {
    return new DirectChannel();
  }

  @Bean(name = ChannelName.INBOUND_TASK_TEST1)
  public MessageChannel inboundTaskTest1() {
    return new DirectChannel();
  }

  @Bean(name = ChannelName.INBOUND_TASK_TEST2)
  public MessageChannel inboundTaskTest2() {
    return new DirectChannel();
  }

  @Bean(name = ChannelName.INBOUND_TASK_TEST3)
  public MessageChannel inboundTaskTest3() {
    return new DirectChannel();
  }
}
