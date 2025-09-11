# 智能Gas费管理和Web3j连接池管理指南

## 概述

本指南介绍了BSIN区块链模块中的两个核心优化功能：
1. **智能Gas费管理** - 动态Gas价格估算和自动加油功能
2. **Web3j连接池管理** - 复用Web3j连接，减少创建开销

## 功能特性

### 智能Gas费管理 (SmartGasFeeService)

#### 核心功能
- **动态Gas价格估算**: 根据网络状况实时计算最优Gas价格
- **EIP-1559支持**: 自动识别链是否支持EIP-1559，使用相应的Gas价格机制
- **交易类型优化**: 支持fast/normal/slow三种交易类型，平衡速度与成本
- **自动加油**: 交易失败时自动增加Gas价格重试
- **价格缓存**: 30秒缓存机制，避免频繁查询

#### 使用示例

```java
@Autowired
private SmartGasFeeService smartGasFeeService;

@Autowired
private Web3jConnectionPoolService connectionPoolService;

// 获取智能Gas价格
Web3j web3j = connectionPoolService.getHttpConnection("ethereum");
SmartGasFeeService.GasPriceInfo gasPriceInfo = smartGasFeeService.getSmartGasPrice(
    "ethereum", web3j, "normal"
);

// 检查是否支持EIP-1559
if (gasPriceInfo.isEIP1559) {
    // 使用EIP-1559交易
    RawTransaction tx = RawTransaction.createTransaction(
        chainId, nonce, gasLimit, to, value, data,
        gasPriceInfo.maxPriorityFeePerGas,
        gasPriceInfo.maxFeePerGas
    );
} else {
    // 使用传统交易
    RawTransaction tx = RawTransaction.createTransaction(
        chainId, nonce, gasLimit, to, value, data,
        gasPriceInfo.gasPrice
    );
}

// 自动加油功能
BigInteger boostedGasPrice = smartGasFeeService.getBoostedGasPrice(
    "ethereum", web3j, originalGasPrice, retryCount
);
```

### Web3j连接池管理 (Web3jConnectionPoolService)

#### 核心功能
- **连接复用**: 避免频繁创建Web3j连接，提高性能
- **连接健康检查**: 定期检查连接状态，自动清理无效连接
- **连接限制**: 每条链最多5个连接，防止资源过度消耗
- **自动清理**: 30分钟超时自动清理，释放资源
- **HTTP/WebSocket支持**: 同时支持HTTP和WebSocket连接池

#### 使用示例

```java
@Autowired
private Web3jConnectionPoolService connectionPoolService;

// 获取HTTP连接
Web3j httpWeb3j = connectionPoolService.getHttpConnection("ethereum");

// 获取WebSocket连接
Web3j wsWeb3j = connectionPoolService.getWebSocketConnection("ethereum");

// 使用完成后释放连接（可选，连接池会自动管理）
connectionPoolService.releaseConnection("ethereum", httpWeb3j, true);

// 获取连接池统计信息
String stats = connectionPoolService.getPoolStats();
log.info("连接池状态: {}", stats);
```

## 配置说明

### 基础配置

```yaml
bsin:
  blockchain:
    enabled: true
    gateway-url: http://127.0.0.1:8125
    
    gas-fee:
      address: '0x5d90A41098954fd90eb70805b3E9442AF9E91625'
      amount: 1000
    
    supported-chains:
      - conflux
      - bsc
      - polygon
      - ethereum
```

### 链特定配置

```yaml
chains:
  ethereum:
    rpc-url: "https://mainnet.infura.io/v3/YOUR_PROJECT_ID"
    websocket-url: "wss://mainnet.infura.io/ws/v3/YOUR_PROJECT_ID"
    enabled: true
    support-eip1559: true  # 支持EIP-1559
    gas-price: "20000000000"  # 20 Gwei
    confirmation-blocks: 12
    
  bsc:
    rpc-url: "https://bsc-dataseed.bnbchain.org"
    enabled: true
    support-eip1559: false  # 不支持EIP-1559
    gas-price: "5000000000"  # 5 Gwei
    confirmation-blocks: 15
```

## 集成到现有代码

### 更新TransactionBiz

现有的`TransactionBiz`已经集成了新的服务：

```java
@Autowired
private BlockchainTransactionService blockchainTransactionService;

// 代币转账（自动使用智能Gas费管理和连接池）
public String tokenTransfer(String chainName, String fromAddress, String toAddress, 
                          String contractAddress, BigInteger amount, BigInteger decimals) {
    return blockchainTransactionService.tokenTransfer(
        chainName, fromAddress, toAddress, contractAddress, amount, decimals
    );
}

// ETH转账（自动使用智能Gas费管理和连接池）
public String ethTransfer(String chainName, String fromAddress, String toAddress, 
                         BigInteger amount) {
    return blockchainTransactionService.ethTransfer(
        chainName, fromAddress, toAddress, amount
    );
}
```

### 带重试的交易发送

```java
// 使用带重试和自动加油的交易发送
String txHash = blockchainTransactionService.signAndSendTransactionWithRetry(
    chainName, rawTransaction, fromAddress, 3  // 最多重试3次
);
```

## 监控和调试

### 日志配置

```yaml
logging:
  level:
    me.flyray.bsin.blockchain.service.SmartGasFeeService: DEBUG
    me.flyray.bsin.blockchain.service.Web3jConnectionPoolService: DEBUG
```

### 获取统计信息

```java
// Gas价格缓存统计
String gasStats = smartGasFeeService.getCacheStats();

// 连接池统计
String poolStats = connectionPoolService.getPoolStats();
String detailedStats = connectionPoolService.getDetailedPoolStats();
```

## 性能优化建议

### 1. Gas价格策略
- **Fast交易**: 用于紧急转账，Gas价格较高但确认快
- **Normal交易**: 日常使用，平衡成本与速度
- **Slow交易**: 非紧急转账，Gas价格较低但确认较慢

### 2. 连接池优化
- 合理设置`MAX_CONNECTIONS_PER_CHAIN`（默认5个）
- 定期监控连接池状态，避免连接泄漏
- 使用WebSocket连接进行实时监听，HTTP连接进行查询

### 3. 缓存策略
- Gas价格缓存30秒，平衡实时性与性能
- 连接池自动清理过期连接，避免资源浪费

## 故障排除

### 常见问题

1. **Gas价格获取失败**
   - 检查RPC端点是否可访问
   - 查看网络连接状态
   - 系统会自动使用备用Gas价格

2. **连接池连接失败**
   - 检查RPC URL配置是否正确
   - 确认网络连接正常
   - 查看连接池统计信息

3. **EIP-1559交易失败**
   - 确认链是否支持EIP-1559
   - 检查Gas价格设置是否合理
   - 系统会自动降级到传统交易

### 调试命令

```java
// 清理过期Gas价格缓存
smartGasFeeService.cleanExpiredCache();

// 获取详细连接池信息
String detailedInfo = connectionPoolService.getDetailedPoolStats();
System.out.println(detailedInfo);
```

## 最佳实践

1. **生产环境配置**
   - 使用可靠的RPC端点（如Infura、Alchemy）
   - 设置合适的确认区块数
   - 启用连接池监控

2. **开发环境配置**
   - 使用测试网RPC端点
   - 降低Gas价格以节省成本
   - 启用详细日志记录

3. **错误处理**
   - 实现重试机制
   - 使用自动加油功能
   - 记录详细的错误日志

## 版本兼容性

- 支持Web3j 4.x版本
- 兼容Spring Boot 2.x/3.x
- 支持Java 8+版本
