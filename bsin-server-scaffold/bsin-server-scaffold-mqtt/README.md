# BSIN MQTT Scaffold 开发指南

## 📋 项目概述

BSIN MQTT Scaffold 是一个基于 Spring Boot 的 MQTT 微服务脚手架，专门用于 IoT 设备通信和消息传递，提供轻量级、高效的 MQTT 协议支持。

## 🏗️ 架构特性

- 🚀 **轻量级协议**：基于 MQTT 3.1.1 协议，适合 IoT 设备
- 🏗️ **QoS 支持**：支持多种服务质量级别
- 🔧 **开箱即用**：预配置的 MQTT 客户端和服务器
- 📦 **消息持久化**：支持消息存储和重传
- 🛡️ **安全认证**：支持 TLS/SSL 和用户名密码认证

## 📁 项目结构

```
bsin-server-scaffold-mqtt/
├── src/                    # 源代码目录
├── doc/                    # 文档和示例
│   ├── MQTT.md            # MQTT 基础知识
│   └── assets/            # 文档资源
├── pom.xml                # Maven 配置
└── README.md              # 项目说明
```

## 🚀 快速开始

### 环境要求
- JDK 11+
- Maven 3.6+
- IDE (推荐 IntelliJ IDEA)
- MQTT Broker (如 Mosquitto、EMQ X)

### 使用步骤

1. **编译项目**
   ```bash
   mvn clean install
   ```

2. **配置 MQTT Broker**
   ```bash
   # 安装 Mosquitto (Ubuntu/Debian)
   sudo apt-get install mosquitto mosquitto-clients
   
   # 启动 Mosquitto
   mosquitto -p 1883
   ```

3. **启动服务**
   ```bash
   mvn spring-boot:run
   ```

## 🔧 技术栈

### 核心框架
- **Spring Boot** - 应用框架
- **Spring Integration MQTT** - MQTT 集成
- **Eclipse Paho** - MQTT 客户端

### 基础设施
- **Mosquitto** - MQTT Broker
- **EMQ X** - 企业级 MQTT Broker (可选)

### 开发工具
- **Maven** - 构建工具
- **MQTT Explorer** - MQTT 客户端工具

## 📚 开发指南

### 1. MQTT 配置

```java
@Configuration
public class MqttConfig {
    
    @Value("${mqtt.broker.url}")
    private String brokerUrl;
    
    @Value("${mqtt.client.id}")
    private String clientId;
    
    @Bean
    public MqttPahoClientFactory mqttClientFactory() {
        DefaultMqttPahoClientFactory factory = new DefaultMqttPahoClientFactory();
        MqttConnectOptions options = new MqttConnectOptions();
        options.setServerURIs(new String[] { brokerUrl });
        options.setUserName("username");
        options.setPassword("password".toCharArray());
        options.setCleanSession(true);
        factory.setConnectionOptions(options);
        return factory;
    }
    
    @Bean
    public MqttPahoMessageDrivenChannelAdapter inbound() {
        MqttPahoMessageDrivenChannelAdapter adapter =
                new MqttPahoMessageDrivenChannelAdapter(clientId, mqttClientFactory(), "topic1", "topic2");
        adapter.setCompletionTimeout(5000);
        adapter.setConverter(new DefaultPahoMessageConverter());
        adapter.setQos(1);
        return adapter;
    }
}
```

### 2. 消息处理器

```java
@Component
public class MqttMessageHandler {
    
    private static final Logger log = LoggerFactory.getLogger(MqttMessageHandler.class);
    
    @ServiceActivator(inputChannel = "mqttInputChannel")
    public void handleMessage(Message<?> message) {
        String topic = message.getHeaders().get(MqttHeaders.RECEIVED_TOPIC).toString();
        String payload = message.getPayload().toString();
        
        log.info("Received message from topic: {}, payload: {}", topic, payload);
        
        // 处理消息逻辑
        processMessage(topic, payload);
    }
    
    private void processMessage(String topic, String payload) {
        // 根据主题处理不同类型的消息
        switch (topic) {
            case "device/status":
                handleDeviceStatus(payload);
                break;
            case "device/data":
                handleDeviceData(payload);
                break;
            default:
                log.warn("Unknown topic: {}", topic);
        }
    }
}
```

### 3. 消息发送

```java
@Component
public class MqttMessageSender {
    
    @Autowired
    private MqttPahoClientFactory mqttClientFactory;
    
    public void sendMessage(String topic, String payload) {
        try {
            MqttPahoMessageHandler messageHandler = 
                    new MqttPahoMessageHandler("sender", mqttClientFactory);
            messageHandler.setAsync(true);
            messageHandler.setDefaultTopic(topic);
            messageHandler.setDefaultQos(1);
            
            Message<String> message = MessageBuilder
                    .withPayload(payload)
                    .setHeader(MqttHeaders.TOPIC, topic)
                    .build();
            
            messageHandler.handleMessage(message);
            
        } catch (Exception e) {
            log.error("Failed to send MQTT message", e);
        }
    }
}
```

### 4. 配置文件

```yaml
# application.yml
mqtt:
  broker:
    url: tcp://localhost:1883
  client:
    id: bsin-mqtt-client
    username: admin
    password: password
  topics:
    device-status: device/status
    device-data: device/data
    device-control: device/control
```

## 📖 测试工具

### 1. MQTT Explorer
- 下载地址：https://mqtt-explorer.com/
- 功能：可视化 MQTT 客户端，支持消息发布和订阅

### 2. 命令行测试
```bash
# 订阅主题
mosquitto_sub -h localhost -p 1883 -t "device/status"

# 发布消息
mosquitto_pub -h localhost -p 1883 -t "device/status" -m "online"
```

### 3. 在线测试工具
- HiveMQ Web Client: http://www.hivemq.com/demos/websocket-client/
- EMQ X Web Client: http://emqtt.io/online-mqtt-client

## 🔍 监控和调试

### 1. 连接监控
```java
@Component
public class MqttConnectionMonitor {
    
    @EventListener
    public void handleConnectionLost(MqttConnectionLostEvent event) {
        log.error("MQTT connection lost: {}", event.getCause());
        // 重连逻辑
    }
    
    @EventListener
    public void handleConnectionEstablished(MqttConnectionEstablishedEvent event) {
        log.info("MQTT connection established");
    }
}
```

### 2. 消息统计
```java
@Component
public class MqttMessageStatistics {
    
    private AtomicLong messageCount = new AtomicLong(0);
    private AtomicLong errorCount = new AtomicLong(0);
    
    public void incrementMessageCount() {
        messageCount.incrementAndGet();
    }
    
    public void incrementErrorCount() {
        errorCount.incrementAndGet();
    }
    
    @Scheduled(fixedRate = 60000) // 每分钟输出一次统计
    public void printStatistics() {
        log.info("MQTT Statistics - Messages: {}, Errors: {}", 
                messageCount.get(), errorCount.get());
    }
}
```

## 📚 相关资源

### 官方文档
- [MQTT 基础知识](./doc/MQTT.md)
- [Apache Shenyu MQTT Plugin](https://shenyu.apache.org/zh/docs/plugin-center/proxy/mqtt-plugin)
- [Spring Integration MQTT](https://github.com/ningzaichun/springboot-integration-mqtt-demo)

### 参考文档
- [MQTT 3.1.1 协议规范](http://docs.oasis-open.org/mqtt/mqtt/v3.1.1/os/mqtt-v3.1.1-os.html)
- [Eclipse Paho](https://www.eclipse.org/paho/)
- [Mosquitto](https://mosquitto.org/)

## 📄 许可证

本项目采用 Apache License 2.0 许可证。

---

**最后更新**: 2024年12月  
**版本**: 3.0.0-SNAPSHOT 