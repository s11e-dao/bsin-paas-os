package me.flyray.bsin.blockchain.service;

import io.reactivex.disposables.Disposable;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 区块链事件发布器
 * 提供区块链事件的发布和监听管理功能
 */
@Slf4j
@Service
public class BlockchainEventPublisher {
    
    @Autowired
    private ApplicationEventPublisher eventPublisher;
    
    @Autowired
    private BlockchainBaseService blockchainBaseService;
    
    // 存储订阅对象，用于管理监听
    private final Map<String, Disposable> subscriptions = new ConcurrentHashMap<>();
    
    /**
     * 发布交易确认事件
     */
    public void publishTransactionConfirmed(String chainName, String txHash, String fromAddress, 
                                          String toAddress, BigInteger amount, long blockNumber, String contractAddress) {
        // 这里可以发布自定义的区块链事件
        log.info("发布交易确认事件: chain={}, txHash={}", chainName, txHash);
    }
    
    /**
     * 发布交易失败事件
     */
    public void publishTransactionFailed(String chainName, String txHash, String fromAddress, 
                                       String toAddress, BigInteger amount, String errorMessage, String contractAddress) {
        // 这里可以发布自定义的区块链事件
        log.info("发布交易失败事件: chain={}, txHash={}, error={}", chainName, txHash, errorMessage);
    }
    
    /**
     * 发布 Gas 费用不足事件
     */
    public void publishInsufficientGas(String chainName, String address, String requiredGas, String currentGas) {
        // 这里可以发布自定义的区块链事件
        log.info("发布 Gas 费用不足事件: chain={}, address={}, required={}, current={}", 
                chainName, address, requiredGas, currentGas);
    }
    
    /**
     * 发布自定义区块链事件
     */
    public void publishBlockchainEvent(String chainName, String eventId, Object eventData) {
        // 这里可以发布自定义的区块链事件
        log.info("发布自定义区块链事件: chain={}, eventId={}", chainName, eventId);
    }
    
    /**
     * 开始监听合约事件
     */
    public void startContractEventListening(String chainName, String contractAddress, String eventName) {
        String key = chainName + "_" + contractAddress + "_" + eventName;
        
        if (subscriptions.containsKey(key)) {
            log.warn("合约事件监听已存在: {}", key);
            return;
        }
        
        Disposable subscription = blockchainBaseService.listenContractEvent(chainName, contractAddress, eventName, eventLog -> {
            log.info("监听到合约事件: chain={}, contract={}, event={}, txHash={}", 
                    chainName, contractAddress, eventName, eventLog.getTransactionHash());
            
            // 发布合约事件
            publishBlockchainEvent(chainName, "contract_event_" + eventLog.getTransactionHash(), eventLog);
        });
        
        subscriptions.put(key, subscription);
        log.info("开始监听合约事件: {}", key);
    }
    
    /**
     * 开始监听区块事件
     */
    public void startBlockEventListening(String chainName) {
        String key = chainName + "_block";
        
        if (subscriptions.containsKey(key)) {
            log.warn("区块事件监听已存在: {}", key);
            return;
        }
        
        Disposable subscription = blockchainBaseService.listenNewBlocks(chainName, blockNumber -> {
            log.info("监听到新区块: chain={}, blockNumber={}", chainName, blockNumber);
            
            // 发布区块事件
            publishBlockchainEvent(chainName, "new_block_" + blockNumber, blockNumber);
        });
        
        subscriptions.put(key, subscription);
        log.info("开始监听区块事件: {}", key);
    }
    
    /**
     * 停止监听
     */
    public void stopListening(String key) {
        Disposable subscription = subscriptions.remove(key);
        if (subscription != null && !subscription.isDisposed()) {
            try {
                subscription.dispose();
                log.info("停止监听: {}", key);
            } catch (Exception e) {
                log.warn("停止监听时发生异常: key={}, error={}", key, e.getMessage());
            }
        }
    }
    
    /**
     * 停止所有监听
     */
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
    
    /**
     * 获取当前监听的订阅数量
     */
    public int getActiveSubscriptionCount() {
        return subscriptions.size();
    }
    
    /**
     * 获取所有监听的键
     */
    public java.util.Set<String> getActiveSubscriptionKeys() {
        return subscriptions.keySet();
    }
}
