package me.flyray.bsin.mqtt.config;

import me.flyray.bsin.mqtt.handler.MqttMessageHandle;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.channel.ExecutorChannel;
import org.springframework.integration.core.MessageProducer;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.mqtt.core.DefaultMqttPahoClientFactory;
import org.springframework.integration.mqtt.core.MqttPahoClientFactory;
import org.springframework.integration.mqtt.inbound.MqttPahoMessageDrivenChannelAdapter;
import org.springframework.integration.mqtt.outbound.MqttPahoMessageHandler;
import org.springframework.integration.mqtt.support.DefaultPahoMessageConverter;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.ThreadPoolExecutor;

@Configuration
public class MqttConfig {

    // 注入MQTT配置属性
    @Autowired
    private MqttProperties mqttProperties;

    // 注入消息处理器
    @Autowired
    private MqttMessageHandle mqttMessageHandle;

    /**
     * 配置MQTT客户端工厂，包含连接选项，如服务器地址、用户名和密码
     * @return MqttPahoClientFactory 实例
     */
    @Bean
    public MqttPahoClientFactory mqttClientFactory() {
        DefaultMqttPahoClientFactory factory = new DefaultMqttPahoClientFactory();
        MqttConnectOptions options = new MqttConnectOptions();
        options.setServerURIs(new String[]{"tcp://localhost:1883"});
        options.setUserName("username");
        options.setPassword("password".toCharArray());
        factory.setConnectionOptions(options);
        return factory;
    }

    /**
     * 配置MQTT输入通道，用于接收订阅的消息
     * @return MessageChannel 实例
     */
    @Bean
    public MessageChannel mqttInputChannel() {
        // 点对点的 DirectChannel 通道
        return new DirectChannel();
    }

    /**
     * 配置MQTT消息驱动的通道适配器，用于接收MQTT消息
     * @param factory MQTT客户端工厂
     * @param mqttInputChannel MQTT输入通道
     * @return MqttPahoMessageDrivenChannelAdapter 实例
     */
    @Bean
    public MqttPahoMessageDrivenChannelAdapter mqttPahoMessageDrivenChannelAdapter(MqttPahoClientFactory factory, MessageChannel mqttInputChannel) {
        MqttPahoMessageDrivenChannelAdapter adapter = new MqttPahoMessageDrivenChannelAdapter("clientId", factory, "topic");
        adapter.setQos(1);
        adapter.setOutputChannel(mqttInputChannel);
        return adapter;
    }

    /**
     * 配置MQTT消息生产者，用于订阅MQTT消息并将其发送到输入通道
     * @param mqttInputChannel 订阅消息通道
     * @param factory MQTT客户端工厂
     * @return MessageProducer 实例
     */
    @Bean
    public MessageProducer mqttInbound(MessageChannel mqttInputChannel, MqttPahoClientFactory factory) {
        String clientId = "mqtt-subscribe-" + System.currentTimeMillis();
        MqttPahoMessageDrivenChannelAdapter adapter = new MqttPahoMessageDrivenChannelAdapter(clientId, factory, "topic");
        adapter.setCompletionTimeout(5000);
        adapter.setConverter(new DefaultPahoMessageConverter());
        adapter.setQos(2);
        adapter.setOutputChannel(mqttInputChannel);
        return adapter;
    }

    /**
     * 配置MQTT消息处理器，用于发送MQTT消息
     * @return MessageHandler 实例
     */
    @Bean
    @ServiceActivator(inputChannel = "mqttOutboundChannel")
    public MessageHandler mqttOutbound() {
        MqttPahoMessageHandler messageHandler = new MqttPahoMessageHandler("clientId", mqttClientFactory());
        messageHandler.setAsync(true);
        return messageHandler;
    }

    /**
     * 配置MQTT输出通道，用于发送消息
     * @return MessageChannel 实例
     */
    @Bean
    public MessageChannel mqttOutboundChannel() {
        return new DirectChannel();
    }

    /**
     * 配置集成流，从MQTT消息驱动的适配器中接收消息并通过线程池处理
     * @param adapter MQTT消息驱动的通道适配器
     * @return IntegrationFlow 实例
     */
    @Bean
    public IntegrationFlow mqttInbound(MqttPahoMessageDrivenChannelAdapter adapter) {
        adapter.setCompletionTimeout(5000);
        adapter.setQos(2);
        return IntegrationFlows.from(adapter)
                .channel(new ExecutorChannel(mqttThreadPoolTaskExecutor()))
                .handle(mqttMessageHandle)
                .get();
    }

    /**
     * 配置线程池任务执行器，用于处理MQTT消息
     * @return ThreadPoolTaskExecutor 实例
     */
    @Bean
    public ThreadPoolTaskExecutor mqttThreadPoolTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        int maxPoolSize = mqttProperties.getMaxPoolSize();
        int corePoolSize = mqttProperties.getCorePoolSize();
        int queueCapacity = mqttProperties.getQueueCapacity();
        int keepAliveSeconds = mqttProperties.getKeepAliveSeconds();
        executor.setMaxPoolSize(maxPoolSize);
        executor.setCorePoolSize(corePoolSize);
        executor.setQueueCapacity(queueCapacity);
        executor.setKeepAliveSeconds(keepAliveSeconds);
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        return executor;
    }
}
