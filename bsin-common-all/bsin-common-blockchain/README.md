# BSIN 区块链通用模块

## 概述

`bsin-common-blockchain` 是 BSIN 平台的区块链通用模块，提供统一的区块链服务接口，支持多链监听、事件处理和交易管理。

## 功能特性

- ✅ **多链支持**：支持 Conflux、BSC、Polygon 等主流区块链
- ✅ **事件监听**：支持区块监听、交易监听、合约事件监听
- ✅ **配置化**：支持灵活的配置管理，可按需启用/禁用
- ✅ **错误处理**：优雅的错误处理和降级机制
- ✅ **监控指标**：集成 Micrometer 监控指标
- ✅ **Spring Boot 集成**：开箱即用的 Spring Boot 自动配置
- ✅ **安全默认值**：默认所有功能都禁用，需要显式配置才能启用

## 默认行为

**🔒 重要：为了安全起见，默认情况下所有区块链功能都是禁用的。**

如果不进行任何配置，模块的行为如下：

- `bsin.blockchain.enabled = false` - 区块链服务完全禁用
- `bsin.blockchain.supported-chains = []` - 不开启任何链
- 每个链的 `enabled = false` - 每条链都禁用
- 每个链的监听配置都默认为 `false` - 不监听区块、交易、合约事件

### 默认禁用的好处

✅ **安全性**：不会意外启动区块链监听服务  
✅ **稳定性**：不会尝试连接未实现的链（如 ethereum、tron）  
✅ **性能**：不会消耗不必要的系统资源  
✅ **简洁性**：不会产生连接错误日志  
✅ **快速启动**：应用启动更快  
✅ **避免问题**：避免网络连接和 API 密钥相关问题  

### 推荐配置

**大多数情况下，建议保持默认禁用状态：**

```yaml
# 不配置任何区块链相关配置（推荐）
# 或者显式禁用：
bsin:
  blockchain:
    enabled: false  # 明确禁用区块链服务
```

**只有在确实需要区块链功能时，才进行启用配置。**

## 快速开始

### 1. 添加依赖

```xml
<dependency>
    <groupId>me.flyray.bsin</groupId>
    <artifactId>bsin-common-blockchain</artifactId>
    <version>3.0.0-SNAPSHOT</version>
</dependency>
```

### 2. 配置区块链服务

**重要：默认情况下所有区块链功能都是禁用的，需要显式配置才能启用。**

#### 2.1 最简单的配置

```yaml
bsin:
  blockchain:
    enabled: true
    chains:
      bsc:
        enabled: true
        rpc-url: "https://bsc-testnet.public.blastapi.io"
```

#### 2.2 启用监听功能

```yaml
bsin:
  blockchain:
    enabled: true
    chains:
      bsc:
        enabled: true
        rpc-url: "https://bsc-testnet.public.blastapi.io"
        transaction-listening-enabled: true  # 启用交易监听
```

#### 2.3 完整配置示例

```yaml
bsin:
  blockchain:
    enabled: true
    continue-on-failure: true
    chains:
      conflux:
        enabled: true
        rpc-url: "https://test.confluxrpc.com"
        block-listening-enabled: true
        transaction-listening-enabled: true
        contract-event-listening-enabled: true
        
      bsc:
        enabled: true
        rpc-url: "https://bsc-testnet.public.blastapi.io"
        block-listening-enabled: true
        transaction-listening-enabled: false
        contract-event-listening-enabled: false
```

### 3. 配置预设

我们提供了多个配置预设，您可以直接复制使用：

- `application-blockchain-simple.yml` - 简化配置示例
- `application-blockchain-presets.yml` - 常用配置预设
- `application-blockchain-example.yml` - 完整配置示例

### 4. 智能默认值

系统会根据链名称自动设置合理的默认值：

| 链名称 | 默认名称 | 默认符号 | 默认确认数 | 默认连接池大小 |
|--------|----------|----------|------------|----------------|
| bsc | BSC Testnet | BNB | 3 | 15 |
| conflux | Conflux Testnet | CFX | 12 | 10 |
| polygon | Polygon Mumbai | MATIC | 5 | 12 |
| ethereum | Ethereum Testnet | ETH | 12 | 10 |

### 3. 使用服务

```java
@Autowired
private BlockchainService blockchainService;

// 监听新区块
Disposable subscription = blockchainService.listenNewBlocks("bsc", blockNumber -> {
    log.info("监听到新区块: {}", blockNumber);
    // 处理新区块逻辑
});

// 监听合约事件
Disposable eventSubscription = blockchainService.listenContractEvent(
    "bsc", 
    "0x...", 
    "Transfer", 
    log -> {
        // 处理 Transfer 事件
    }
);
```

## 配置选项

### 全局配置

| 配置项 | 类型 | 默认值 | 说明 |
|--------|------|--------|------|
| `bsin.blockchain.enabled` | boolean | true | 是否启用区块链服务 |
| `bsin.blockchain.continue-on-failure` | boolean | true | 启动失败时是否继续运行应用 |

### 监听器配置

| 配置项 | 类型 | 默认值 | 说明 |
|--------|------|--------|------|
| `bsin.blockchain.listener.block-listening-enabled` | boolean | true | 是否启用区块监听 |
| `bsin.blockchain.listener.transaction-listening-enabled` | boolean | true | 是否启用交易监听 |
| `bsin.blockchain.listener.contract-event-listening-enabled` | boolean | true | 是否启用合约事件监听 |
| `bsin.blockchain.listener.connection-timeout-ms` | long | 10000 | 连接超时时间（毫秒） |
| `bsin.blockchain.listener.read-timeout-ms` | long | 30000 | 读取超时时间（毫秒） |
| `bsin.blockchain.listener.retry-count` | int | 3 | 重试次数 |
| `bsin.blockchain.listener.retry-interval-ms` | long | 5000 | 重试间隔（毫秒） |

### 链配置

每个链支持以下配置：

| 配置项 | 类型 | 默认值 | 说明 |
|--------|------|--------|------|
| `rpc-url` | string | - | RPC 端点 URL |
| `enabled` | boolean | true | 是否启用该链 |
| `name` | string | - | 链名称 |
| `symbol` | string | - | 链符号 |
| `mainnet` | boolean | false | 是否为主网 |
| `confirmations` | int | 12 | 区块确认数 |
| `connection-pool-size` | int | 10 | 连接池大小 |
| `api-key` | string | - | API 密钥（如果需要） |

## 环境配置示例

### 开发环境

```yaml
spring:
  profiles:
    active: dev

bsin:
  blockchain:
    enabled: true
    continue-on-failure: true
    chains:
      conflux:
        enabled: true
        rpc-url: "https://test.confluxrpc.com"
      bsc:
        enabled: true
        rpc-url: "https://bsc-testnet.public.blastapi.io"
      polygon:
        enabled: false  # 开发环境禁用
```

### 生产环境

```yaml
spring:
  profiles:
    active: prod

bsin:
  blockchain:
    enabled: true
    continue-on-failure: false  # 生产环境严格模式
    chains:
      conflux:
        enabled: true
        rpc-url: "https://main.confluxrpc.com"
        mainnet: true
        confirmations: 12
      bsc:
        enabled: true
        rpc-url: "https://bsc-dataseed.binance.org"
        mainnet: true
        confirmations: 3
```

### 禁用区块链服务

```yaml
spring:
  profiles:
    active: blockchain-disabled

bsin:
  blockchain:
    enabled: false  # 完全禁用区块链服务
```

## 常见问题

### Q: 如何解决 RPC 端点连接失败问题？

A: 可以通过以下方式解决：

1. **检查网络连接**：确保服务器可以访问 RPC 端点
2. **使用备用 RPC**：配置多个 RPC 端点作为备用
3. **禁用问题链**：在配置中将 `enabled` 设置为 `false`
4. **启用容错模式**：设置 `continue-on-failure: true`

### Q: 如何避免 "暂未开放的链，敬请期待！！" 错误？

A: 这个错误通常是因为尝试初始化未实现的链（如 ethereum、tron）。解决方法：

1. **使用安全模式配置**：
```yaml
spring:
  profiles:
    active: safe-mode
```

2. **只配置已实现的链**：
```yaml
bsin:
  blockchain:
    enabled: true
    continue-on-failure: true
    chains:
      conflux:
        enabled: true
        rpc-url: "https://test.confluxrpc.com"
      bsc:
        enabled: true
        rpc-url: "https://bsc-testnet.public.blastapi.io"
      polygon:
        enabled: false  # 如果连接有问题，可以禁用
      # 不要配置 ethereum 和 tron
```

3. **修改 BsinBlockChainEngineFactory**：只初始化已实现的链类型

### Q: 如何为每条链单独控制监听功能？

A: 每条链都支持独立的监听配置：

```yaml
bsin:
  blockchain:
    chains:
      conflux:
        enabled: true
        block-listening-enabled: true      # 启用区块监听
        transaction-listening-enabled: false  # 禁用交易监听
        contract-event-listening-enabled: false # 禁用合约事件监听
      bsc:
        enabled: true
        block-listening-enabled: false     # 禁用区块监听
        transaction-listening-enabled: true   # 启用交易监听
        contract-event-listening-enabled: true # 启用合约事件监听
```

### Q: 如何添加新的区块链支持？

A: 需要：

1. 在 `BlockchainProperties` 中添加新链配置
2. 在 `getRpcUrl` 方法中添加默认 RPC 端点
3. 更新 `BsinBlockChainEngineFactory` 支持新链类型

### Q: 如何监控区块链服务状态？

A: 模块集成了 Micrometer 监控指标，可以通过以下方式监控：

```java
@Autowired
private BlockchainListenerManager listenerManager;

// 获取活跃监听数量
int activeCount = listenerManager.getActiveSubscriptionCount();

// 获取活跃监听键
Set<String> activeKeys = listenerManager.getActiveSubscriptionKeys();
```

## 更新日志

### v3.0.0-SNAPSHOT
- ✅ 添加配置化支持
- ✅ 优化错误处理机制
- ✅ 支持优雅降级
- ✅ 添加监控指标
- ✅ 支持多环境配置

## 许可证

本项目采用 Apache 2.0 许可证。