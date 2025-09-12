# RxJava UndeliverableException 异常修复

## 问题描述

在区块链事件监听服务中出现了 `UndeliverableException` 异常，该异常表明在 RxJava 流已经被取消或释放后，仍然有异常试图传递给消费者。

## 异常堆栈

```
Exception in thread "main" io.reactivex.exceptions.UndeliverableException: The exception could not be delivered to the consumer because it has already canceled/disposed the flow or the exception has nowhere to go to begin with.
```

## 修复方案

### 1. 改进异常处理机制

在 `BlockchainEventPublisher.stopAllListening()` 方法中添加了 try-catch 异常处理：

```java
public void stopAllListening() {
    subscriptions.forEach((key, subscription) -> {
        try {
            if (subscription != null && !subscription.isDisposed()) {
                subscription.dispose();
                log.debug("已停止监听: {}", key);
            }
        } catch (Exception e) {
            log.warn("停止监听时发生异常: key={}, error={}", key, e.getMessage());
        }
    });
    subscriptions.clear();
    log.info("停止所有监听");
}
```

### 2. 增强监听方法的错误处理

在所有监听方法中添加了完整的错误处理机制：

- `BsinBlockChainEventListonService.listenContractEvent()`
- `BsinBlockChainEventListonService.listenBlockEvent()`
- `BsinBlockChainBlockListonService.listenNewBlocks()`
- `BsinBlockChainBlockListonService.listenPendingTransactions()`

每个方法都包含：
- 数据处理的 try-catch 包装
- 错误流的处理
- 初始化失败时的优雅降级

### 3. 添加全局 RxJava 错误处理器

创建了 `RxJavaErrorHandler` 配置类，用于处理全局的 RxJava 异常：

```java
@Configuration
public class RxJavaErrorHandler {
    
    @PostConstruct
    public void initRxJavaErrorHandler() {
        RxJavaPlugins.setErrorHandler(throwable -> {
            if (throwable instanceof io.reactivex.exceptions.UndeliverableException) {
                log.warn("RxJava 流中发生未传递的异常: {}", throwable.getMessage());
            } else {
                log.error("RxJava 流中发生未处理的异常", throwable);
            }
        });
    }
}
```

## 修复效果

1. **异常隔离**：监听过程中的异常不会影响其他监听流
2. **优雅降级**：初始化失败时返回空的 Disposable，避免阻塞应用启动
3. **全局处理**：未捕获的 RxJava 异常会被全局处理器捕获并记录
4. **资源管理**：改进了 Disposable 的释放逻辑，避免重复释放

## 使用建议

1. 确保在应用启动时加载 `RxJavaErrorHandler` 配置
2. 在监听服务销毁时调用 `stopAllListening()` 方法
3. 监控日志中的异常信息，及时处理网络连接等问题

## 相关文件

- `BlockchainEventPublisher.java` - 事件发布器
- `BsinBlockChainEventListonService.java` - 事件监听服务
- `BsinBlockChainBlockListonService.java` - 区块监听服务
- `RxJavaErrorHandler.java` - 全局错误处理器
