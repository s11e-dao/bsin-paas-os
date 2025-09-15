# RocketMQ 测试用例说明

## 概述

这个测试类 `RocketMqTest` 提供了完整的 RocketMQ 消息队列测试功能，涵盖了项目中使用的所有消息发送模式和业务场景。

## 测试环境准备

### 1. RocketMQ 服务启动

确保 RocketMQ 服务正在运行：

```bash
# 启动 NameServer
nohup sh mqnamesrv > name.out 2>&1 &

# 启动 Broker
nohup sh mqbroker -c /path/to/broker.conf > broker.out 2>&1 &

# 或者使用 Docker 启动
docker-compose -f rocketmq/docker-compose.yml up -d
```

### 2. 检查服务状态

```bash
# 查看 NameServer 状态
ps -ef | grep mqnamesrv

# 查看 Broker 状态
ps -ef | grep mqbroker

# 查看 RocketMQ 控制台
# 访问: http://localhost:8080/#/cluster
```

### 3. 创建测试 Topic

```bash
# 进入 Broker 容器
docker exec -it rmqbroker bash

# 创建测试 Topic
sh mqadmin updatetopic -t waas-test -c DefaultCluster
```

## 测试用例说明

### 1. testSyncSendMessage() - 同步消息发送测试
- **功能**: 测试同步消息发送功能
- **场景**: 创建 MPC 钱包消息
- **验证**: 发送状态、消息ID、队列ID

### 2. testAsyncSendMessage() - 异步消息发送测试
- **功能**: 测试异步消息发送功能
- **场景**: Gas 加油通知消息
- **验证**: 异步回调、发送状态

### 3. testDelaySendMessage() - 延时消息发送测试
- **功能**: 测试延时消息发送功能
- **场景**: 资金归集通知消息
- **验证**: 延时等级4（1分钟）、异步回调

### 4. testTagSendMessage() - 带Tag消息发送测试
- **功能**: 测试带Tag的消息发送功能
- **场景**: 钱包创建消息带wallet标签
- **验证**: Tag过滤功能

### 5. testOneWaySendMessage() - 单向消息发送测试
- **功能**: 测试单向消息发送功能
- **场景**: 日志消息（不可靠发送）
- **验证**: 单向发送特性

### 6. testBatchSendMessage() - 批量消息发送测试
- **功能**: 测试批量消息发送功能
- **场景**: 连续发送5条消息
- **验证**: 批量发送成功率

### 7. testMessageFormatValidation() - 消息格式验证测试
- **功能**: 测试消息格式验证
- **场景**: 有效格式、空消息、null消息
- **验证**: 格式验证逻辑

### 8. testAllEventTypes() - 所有事件类型测试
- **功能**: 测试所有消息事件类型
- **场景**: CREATE_MPC_WALLET、GET_GAS_NOTIFY、CASH_CONCENTRATION_NOTIFY
- **验证**: 所有事件类型支持

### 9. testBusinessScenario() - 综合业务场景测试
- **功能**: 模拟真实业务场景
- **场景**: 钱包创建 → Gas加油 → 资金归集
- **验证**: 完整业务流程

## 运行测试

### 1. 运行单个测试方法

```bash
# 运行同步消息测试
mvn test -Dtest=RocketMqTest#testSyncSendMessage

# 运行异步消息测试
mvn test -Dtest=RocketMqTest#testAsyncSendMessage

# 运行延时消息测试
mvn test -Dtest=RocketMqTest#testDelaySendMessage
```

### 2. 运行所有测试

```bash
# 运行所有 RocketMQ 测试
mvn test -Dtest=RocketMqTest

# 或者运行整个测试套件
mvn test
```

### 3. 在 IDE 中运行

1. 右键点击测试类或测试方法
2. 选择 "Run" 或 "Debug"
3. 查看控制台输出和测试结果

## 测试配置

### 1. 测试配置文件

测试使用 `application-test.yml` 配置文件，包含：
- RocketMQ 连接配置
- 测试 Topic 配置
- 日志级别配置

### 2. 环境变量

确保以下环境变量已设置：
- `BSIN_ROCKETMQ_NAME_SERVER`: RocketMQ NameServer 地址
- `BSIN_ROCKETMQ_ACCESS_KEY`: 访问密钥
- `BSIN_ROCKETMQ_SECRET_KEY`: 秘密密钥

## 测试结果验证

### 1. 控制台输出

测试运行时会输出详细的日志信息：
- 消息发送状态
- 消息ID和队列ID
- 错误信息和异常堆栈

### 2. RocketMQ 控制台

可以通过 RocketMQ 控制台查看：
- 消息发送统计
- 消息消费情况
- Topic 和队列状态

### 3. 断言验证

每个测试方法都包含断言验证：
- 发送状态检查
- 消息ID验证
- 成功率统计

## 常见问题

### 1. 连接失败

```
org.apache.rocketmq.client.exception.MQClientException: No route info of this topic
```

**解决方案**:
- 检查 RocketMQ 服务是否启动
- 确认 NameServer 地址配置正确
- 创建对应的 Topic

### 2. 认证失败

```
org.apache.rocketmq.client.exception.MQClientException: CODE: 1  DESC: The broker does not support consumer to filter message by SQL92
```

**解决方案**:
- 检查 access-key 和 secret-key 配置
- 确认 Broker 配置支持消息过滤

### 3. 超时问题

```
java.util.concurrent.TimeoutException
```

**解决方案**:
- 增加超时时间配置
- 检查网络连接
- 确认 Broker 负载情况

## 扩展测试

### 1. 添加新的测试方法

```java
@Test
public void testCustomScenario() {
    // 自定义测试逻辑
}
```

### 2. 测试新的消息类型

```java
// 在 MqEventCode 枚举中添加新的事件类型
// 在测试方法中使用新的事件类型
```

### 3. 性能测试

```java
@Test
public void testPerformance() {
    long startTime = System.currentTimeMillis();
    // 发送大量消息
    long endTime = System.currentTimeMillis();
    // 计算性能指标
}
```

## 注意事项

1. **测试环境隔离**: 使用独立的测试 Topic 和 Consumer Group
2. **资源清理**: 测试完成后清理测试数据
3. **并发安全**: 注意多线程测试的并发安全性
4. **异常处理**: 妥善处理测试中的异常情况
5. **日志记录**: 保持详细的测试日志记录

## 相关文档

- [RocketMQ 官方文档](https://rocketmq.apache.org/docs/quick-start/)
- [Spring Boot RocketMQ 集成](https://github.com/apache/rocketmq-spring)
- [Bsin-PaaS 消息队列模块文档](../bsin-common-mq/README.md)
