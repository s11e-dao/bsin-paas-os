# 🚀 RocketMQ Spring Boot Starter 使用指南

## 📋 概述

`rocketmq-spring-boot-starter` 是 Apache RocketMQ 官方提供的 Spring Boot 集成包，版本 2.3.4。它简化了 RocketMQ 在 Spring Boot 应用中的使用，提供了自动配置和便捷的 API。

## 🔧 1. 依赖配置

### 1.1 Maven 依赖

**位置**: `bsin-common-all/bsin-common-mq/pom.xml`

```xml
<dependency>
    <groupId>org.apache.rocketmq</groupId>
    <artifactId>rocketmq-spring-boot-starter</artifactId>
    <version>2.3.4</version>
</dependency>
```

### 1.2 版本说明

- **当前版本**: 2.3.4
- **支持 Spring Boot**: 2.x
- **支持 Java**: 8+
- **RocketMQ 版本**: 4.9.x

## ⚙️ 2. 配置文件

### 2.1 基础配置

**位置**: `application.yml`

```yaml
rocketmq:
  # NameServer 地址 - 必填
  name-server: 127.0.0.1:9876
  
  # Producer 配置
  producer:
    # 生产者组名 - 必填
    group: springboot_producer_group
    # 发送消息超时时间（毫秒）
    sendMessageTimeout: 10000
    # 发送失败重试次数
    retryTimesWhenSendFailed: 2
    # 异步发送失败重试次数
    retryTimesWhenSendAsyncFailed: 2
    # 消息最大长度（字节）
    maxMessageSize: 4096
    # 压缩消息阈值（字节）
    compressMessageBodyThreshold: 4096
    # 是否重试其他服务器
    retryNextServer: false
    # 访问密钥（可选）
    access-key: rocketmq2
    # 秘密密钥（可选）
    secret-key: 12345678
  
  # Consumer 配置
  consumer:
    # 消费者组名 - 必填
    group: consumer_group
    # 订阅主题
    topic: waas-test
    # 批量拉取消息数量
    pull-batch-size: 10
    # 访问密钥（可选）
    access-key: rocketmq2
    # 秘密密钥（可选）
    secret-key: 12345678
```

## 🏗️ 3. 自动配置机制

### 3.1 自动创建的 Bean

Spring Boot Starter 会自动创建以下 Bean：

```java
// 1. RocketMQTemplate - 消息发送模板
@Bean
public RocketMQTemplate rocketMQTemplate() {
    // 基于配置创建 RocketMQTemplate
    // 内部封装了 DefaultMQProducer
}

// 2. RocketMQMessageConverter - 消息转换器
@Bean
public RocketMQMessageConverter rocketMQMessageConverter() {
    // 配置消息序列化/反序列化
}

// 3. 消费者监听器容器
@Bean
public DefaultRocketMQListenerContainer defaultRocketMQListenerContainer() {
    // 管理消费者监听器
}
```

### 3.2 配置增强

**位置**: `bsin-common-all/bsin-common-mq/src/main/java/me/flyray/bsin/mq/config/RocketMQEnhanceConfig.java`

```java
@Configuration
public class RocketMQEnhanceConfig {
    
    @Bean
    @Primary
    public RocketMQMessageConverter enhanceRocketMQMessageConverter(){
        RocketMQMessageConverter converter = new RocketMQMessageConverter();
        CompositeMessageConverter compositeMessageConverter = 
            (CompositeMessageConverter) converter.getMessageConverter();
        
        List<MessageConverter> messageConverterList = 
            compositeMessageConverter.getConverters();
        
        for (MessageConverter messageConverter : messageConverterList) {
            if(messageConverter instanceof MappingJackson2MessageConverter){
                MappingJackson2MessageConverter jackson2MessageConverter = 
                    (MappingJackson2MessageConverter) messageConverter;
                ObjectMapper objectMapper = jackson2MessageConverter.getObjectMapper();
                // 注册 Java 8 时间模块
                objectMapper.registerModules(new JavaTimeModule());
            }
        }
        return converter;
    }
}
```

## 📤 4. 消息发送 (Producer)

### 4.1 封装的生产者类

**位置**: `bsin-common-all/bsin-common-mq/src/main/java/me/flyray/bsin/mq/producer/RocketMQProducer.java`

```java
@Slf4j
@Component
public class RocketMQProducer {

    @Autowired
    public RocketMQTemplate mqTemplate;  // 自动注入

    /**
     * 发送同步消息
     */
    public SendResult send(String topic, String body) {
        SendResult result = mqTemplate.syncSend(topic, 
            MessageBuilder.withPayload(body).build());
        
        if (result.getSendStatus() != SendStatus.SEND_OK) {
            log.error("消息发送失败，topic：{}，数据：{}", topic, body);
        }
        return result;
    }

    /**
     * 发送异步消息
     */
    public void sendAsync(String topic, String body, SendCallback callback) {
        mqTemplate.asyncSend(topic, 
            MessageBuilder.withPayload(body).build(), callback);
    }

    /**
     * 发送延时消息
     * 延时等级：1s 5s 10s 30s 1m 2m 3m 4m 5m 6m 7m 8m 9m 10m 20m 30m 1h 2h
     */
    public void sendDelay(String topic, String body, SendCallback callback, 
                         Integer delayLevel) {
        mqTemplate.asyncSend(topic, 
            MessageBuilder.withPayload(body).build(), callback, 3000, delayLevel);
    }

    /**
     * 发送带 Tag 的消息
     */
    public void sendTag(String topic, String tag, String body) {
        SendResult result = mqTemplate.syncSend(
            String.format("%s:%s", topic, tag), 
            MessageBuilder.withPayload(body).build());
        
        if (result.getSendStatus() != SendStatus.SEND_OK) {
            log.error("消息发送失败，topic：{}，tag:{}，数据：{}", topic, tag, body);
        }
    }

    /**
     * 单向发送（不保证可靠性）
     */
    public void sendOneWay(String topic, String body) {
        mqTemplate.sendOneWay(topic, MessageBuilder.withPayload(body).build());
    }
}
```

### 4.2 使用示例

```java
@Service
public class MessageService {
    
    @Autowired
    private RocketMQProducer rocketMQProducer;
    
    public void sendMessage() {
        // 1. 同步发送
        String message = "Hello RocketMQ!";
        SendResult result = rocketMQProducer.send("waas-test", message);
        
        // 2. 异步发送
        rocketMQProducer.sendAsync("waas-test", message, new SendCallback() {
            @Override
            public void onSuccess(SendResult sendResult) {
                log.info("异步消息发送成功: {}", sendResult.getMsgId());
            }
            
            @Override
            public void onException(Throwable e) {
                log.error("异步消息发送失败", e);
            }
        });
        
        // 3. 延时发送（延时1分钟）
        rocketMQProducer.sendDelay("waas-test", message, callback, 4);
        
        // 4. 带 Tag 发送
        rocketMQProducer.sendTag("waas-test", "order", message);
        
        // 5. 单向发送
        rocketMQProducer.sendOneWay("waas-test", message);
    }
}
```

## 📥 5. 消息接收 (Consumer)

### 5.1 消费者实现

**位置**: `bsin-server-apps/bsin-server-iot/iot-server/src/main/java/me/flyray/bsin/server/handler/RocketMQConsumer.java`

```java
@Component
@RocketMQMessageListener(
    consumerGroup = "consumer_group",  // 消费者组
    topic = "waas-test"                // 订阅主题
)
public class RocketMQConsumer implements RocketMQListener<String> {
    
    @Override
    public void onMessage(String message) {
        // 处理消息的逻辑
        System.out.println("Received message: " + message);
        
        // 业务处理
        try {
            // 解析消息
            JSONObject messageJson = JSON.parseObject(message);
            String eventCode = messageJson.getString("eventCode");
            
            // 根据事件码处理不同业务
            switch (eventCode) {
                case "CREATE_MPC_WALLET":
                    handleCreateWallet(messageJson);
                    break;
                case "GET_GAS_NOTIFY":
                    handleGasNotify(messageJson);
                    break;
                default:
                    log.warn("未知的事件码: {}", eventCode);
            }
            
        } catch (Exception e) {
            log.error("消息处理失败: {}", message, e);
            // 可以抛出异常触发重试
            throw new RuntimeException("消息处理失败", e);
        }
    }
    
    private void handleCreateWallet(JSONObject message) {
        // 处理创建钱包逻辑
        log.info("处理创建钱包消息: {}", message);
    }
    
    private void handleGasNotify(JSONObject message) {
        // 处理 Gas 通知逻辑
        log.info("处理 Gas 通知消息: {}", message);
    }
}
```

### 5.2 消费者配置选项

```java
@RocketMQMessageListener(
    consumerGroup = "consumer_group",           // 消费者组名
    topic = "waas-test",                        // 主题名
    selectorType = SelectorType.TAG,            // 选择器类型：TAG/SQL92
    selectorExpression = "order || payment",    // 选择器表达式
    consumeMode = ConsumeMode.CONCURRENTLY,     // 消费模式：并发/顺序
    messageModel = MessageModel.CLUSTERING,     // 消息模式：集群/广播
    consumeTimeout = 15L,                       // 消费超时时间（分钟）
    maxReconsumeTimes = 16,                     // 最大重试次数
    enableMsgTrace = true,                      // 是否开启消息轨迹
    accessKey = "rocketmq2",                    // 访问密钥
    secretKey = "12345678"                      // 秘密密钥
)
public class AdvancedRocketMQConsumer implements RocketMQListener<String> {
    
    @Override
    public void onMessage(String message) {
        // 消息处理逻辑
    }
}
```

## 🧪 6. 测试示例

### 6.1 单元测试

**位置**: `bsin-server-apps/bsin-server-waas/waas-server/src/test/java/me/flyray/bsin/server/RocketMqTest.java`

```java
@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class RocketMqTest {

    @Autowired
    private RocketMQProducer rocketMQProducer;

    @Value("${rocketmq.consumer.topic}")
    private String topic;

    /**
     * 测试同步消息发送
     */
    @Test
    public void testSyncSendMessage() {
        log.info("=== 开始测试同步消息发送 ===");
        
        // 构建测试消息
        JSONObject message = new JSONObject();
        message.put("eventCode", MqEventCode.CREATE_MPC_WALLET.getCode());
        message.put("requisitionId", "test-sync-" + System.currentTimeMillis());
        message.put("userId", "test-user-001");
        message.put("walletType", "MPC");
        message.put("timestamp", System.currentTimeMillis());
        
        String messageBody = message.toJSONString();
        log.info("发送同步消息: {}", messageBody);
        
        try {
            // 发送同步消息
            SendResult result = rocketMQProducer.send(topic, messageBody);
            
            // 验证发送结果
            assert result != null : "发送结果不能为空";
            assert result.getSendStatus() == SendStatus.SEND_OK : "消息发送失败";
            
            log.info("同步消息发送成功 - MessageId: {}, QueueId: {}, SendStatus: {}", 
                    result.getMsgId(), result.getMessageQueue().getQueueId(), result.getSendStatus());
            
        } catch (Exception e) {
            log.error("同步消息发送失败", e);
            throw new RuntimeException("同步消息发送失败", e);
        }
        
        log.info("=== 同步消息发送测试完成 ===");
    }

    /**
     * 测试异步消息发送
     */
    @Test
    public void testAsyncSendMessage() throws InterruptedException {
        log.info("=== 开始测试异步消息发送 ===");
        
        CountDownLatch latch = new CountDownLatch(1);
        AtomicBoolean success = new AtomicBoolean(false);
        
        // 构建测试消息
        JSONObject message = new JSONObject();
        message.put("eventCode", MqEventCode.GET_GAS_NOTIFY.getCode());
        message.put("requisitionId", "test-async-" + System.currentTimeMillis());
        message.put("userId", "test-user-002");
        message.put("gasAmount", "0.001");
        message.put("timestamp", System.currentTimeMillis());
        
        String messageBody = message.toJSONString();
        log.info("发送异步消息: {}", messageBody);
        
        // 发送异步消息
        rocketMQProducer.sendAsync(topic, messageBody, new SendCallback() {
            @Override
            public void onSuccess(SendResult sendResult) {
                log.info("异步消息发送成功 - MessageId: {}, SendStatus: {}", 
                        sendResult.getMsgId(), sendResult.getSendStatus());
                success.set(true);
                latch.countDown();
            }
            
            @Override
            public void onException(Throwable e) {
                log.error("异步消息发送失败", e);
                success.set(false);
                latch.countDown();
            }
        });
        
        // 等待异步回调
        boolean await = latch.await(10, TimeUnit.SECONDS);
        assert await : "异步消息发送超时";
        assert success.get() : "异步消息发送失败";
        
        log.info("=== 异步消息发送测试完成 ===");
    }
}
```

## 🔍 7. 高级特性

### 7.1 消息过滤

```java
// 基于 Tag 过滤
@RocketMQMessageListener(
    consumerGroup = "tag_consumer",
    topic = "waas-test",
    selectorType = SelectorType.TAG,
    selectorExpression = "order || payment"  // 只消费 order 或 payment 标签的消息
)
public class TagFilterConsumer implements RocketMQListener<String> {
    // ...
}

// 基于 SQL 过滤
@RocketMQMessageListener(
    consumerGroup = "sql_consumer",
    topic = "waas-test",
    selectorType = SelectorType.SQL92,
    selectorExpression = "userId = 'test-user-001' AND amount > 100"
)
public class SqlFilterConsumer implements RocketMQListener<String> {
    // ...
}
```

### 7.2 顺序消息

```java
@RocketMQMessageListener(
    consumerGroup = "order_consumer",
    topic = "order-topic",
    consumeMode = ConsumeMode.ORDERLY  // 顺序消费
)
public class OrderConsumer implements RocketMQListener<String> {
    
    @Override
    public void onMessage(String message) {
        // 顺序处理消息
        log.info("顺序消费消息: {}", message);
    }
}
```

### 7.3 事务消息

```java
@Service
public class TransactionMessageService {
    
    @Autowired
    private RocketMQTemplate rocketMQTemplate;
    
    public void sendTransactionMessage() {
        // 发送事务消息
        rocketMQTemplate.sendMessageInTransaction(
            "transaction-topic",
            MessageBuilder.withPayload("transaction message").build(),
            "transaction-arg"
        );
    }
    
    @RocketMQTransactionListener
    public class TransactionListenerImpl implements RocketMQTransactionListener {
        
        @Override
        public RocketMQLocalTransactionState executeLocalTransaction(
                Message msg, Object arg) {
            // 执行本地事务
            try {
                // 本地业务逻辑
                return RocketMQLocalTransactionState.COMMIT;
            } catch (Exception e) {
                return RocketMQLocalTransactionState.ROLLBACK;
            }
        }
        
        @Override
        public RocketMQLocalTransactionState checkLocalTransaction(Message msg) {
            // 检查本地事务状态
            return RocketMQLocalTransactionState.COMMIT;
        }
    }
}
```

## 🚨 8. 常见问题

### 8.1 配置问题

```yaml
# 问题：连接超时
rocketmq:
  producer:
    sendMessageTimeout: 30000  # 增加超时时间

# 问题：消息发送失败
rocketmq:
  producer:
    retryTimesWhenSendFailed: 5  # 增加重试次数
```

### 8.2 消费问题

```java
// 问题：消息重复消费
@RocketMQMessageListener(
    consumerGroup = "unique_consumer_group",  // 确保消费者组名唯一
    topic = "waas-test"
)
public class UniqueConsumer implements RocketMQListener<String> {
    // ...
}

// 问题：消费失败重试
@Override
public void onMessage(String message) {
    try {
        // 业务处理
        processMessage(message);
    } catch (Exception e) {
        log.error("消息处理失败，将触发重试: {}", message, e);
        throw e;  // 抛出异常触发重试
    }
}
```

## 📝 9. 最佳实践

### 9.1 消息设计

```java
// 1. 使用统一的消息格式
public class MessageWrapper {
    private String eventCode;
    private String requestId;
    private Long timestamp;
    private Object data;
    // getters and setters
}

// 2. 使用枚举定义事件码
public enum MqEventCode {
    CREATE_MPC_WALLET("CREATE_MPC_WALLET", "创建MPC钱包"),
    GET_GAS_NOTIFY("GET_GAS_NOTIFY", "Gas通知"),
    CASH_CONCENTRATION_NOTIFY("CASH_CONCENTRATION_NOTIFY", "资金归集通知");
    
    private final String code;
    private final String description;
}
```

### 9.2 错误处理

```java
@Component
public class RobustRocketMQConsumer implements RocketMQListener<String> {
    
    @Override
    public void onMessage(String message) {
        try {
            // 业务处理
            processBusinessLogic(message);
        } catch (BusinessException e) {
            // 业务异常，记录日志但不重试
            log.error("业务处理失败: {}", message, e);
        } catch (Exception e) {
            // 系统异常，记录日志并重试
            log.error("系统异常，将触发重试: {}", message, e);
            throw e;
        }
    }
}
```

### 9.3 监控和日志

```java
@Component
public class MonitoredRocketMQProducer {
    
    @Autowired
    private RocketMQTemplate mqTemplate;
    
    public SendResult send(String topic, String body) {
        long startTime = System.currentTimeMillis();
        
        try {
            SendResult result = mqTemplate.syncSend(topic, 
                MessageBuilder.withPayload(body).build());
            
            long duration = System.currentTimeMillis() - startTime;
            log.info("消息发送成功 - Topic: {}, Duration: {}ms, MessageId: {}", 
                    topic, duration, result.getMsgId());
            
            return result;
        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            log.error("消息发送失败 - Topic: {}, Duration: {}ms", topic, duration, e);
            throw e;
        }
    }
}
```

---

**总结**: `rocketmq-spring-boot-starter` 提供了完整的 RocketMQ 集成方案，通过自动配置简化了使用复杂度，同时保持了灵活性和可扩展性。合理使用其提供的 API 和配置选项，可以构建稳定可靠的消息队列系统。
