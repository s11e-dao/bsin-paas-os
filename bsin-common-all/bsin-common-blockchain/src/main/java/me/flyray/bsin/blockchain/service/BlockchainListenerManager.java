package me.flyray.bsin.blockchain.service;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import me.flyray.bsin.blockchain.config.BlockchainProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.util.Set;

/**
 * 区块链监听管理器
 * 统一管理所有区块链监听服务
 */
@Slf4j
@Service
@ConditionalOnProperty(prefix = "bsin.blockchain", name = "enabled", havingValue = "true", matchIfMissing = true)
public class BlockchainListenerManager {
    
    @Autowired
    private BlockchainEventPublisher eventPublisher;
    
    @Autowired
    private BsinBlockChainEngineFactory blockchainEngineFactory;
    
    @Autowired
    private BlockchainProperties blockchainProperties;
    
    /**
     * 初始化监听服务
     */
    @PostConstruct
    public void initializeListeners() {
        if (!blockchainProperties.isBlockchainEnabled()) {
            log.info("区块链监听服务已禁用，跳过初始化");
            return;
        }
        
        log.info("开始初始化区块链监听服务...");
        
        try {
            // 从配置中获取启用的链
            Set<String> enabledChains = blockchainProperties.getEnabledChains();
            
            if (enabledChains.isEmpty()) {
                log.warn("没有配置启用的区块链，跳过监听初始化");
                return;
            }
            
            int successCount = 0;
            int totalCount = enabledChains.size();
            
            for (String chainName : enabledChains) {
                try {
                    // 检查监听器配置
                    if (!isListeningEnabled(chainName)) {
                        log.info("链 {} 的监听功能已禁用，跳过", chainName);
                        continue;
                    }
                    
                    // 尝试创建区块链引擎实例来验证链是否可用
                    blockchainEngineFactory.getBsinBlockChainEngineInstance(chainName);
                    
                    // 开始监听区块事件
                    if (isListeningEnabled(chainName)) {
                        eventPublisher.startBlockEventListening(chainName);
                        log.info("已启动 {} 链的区块监听", chainName);
                    }
                    
                    successCount++;
                } catch (Exception e) {
                    log.warn("链 {} 不可用，跳过监听初始化: {}", chainName, e.getMessage());
                    // 根据配置决定是否继续
                    if (!blockchainProperties.isContinueOnFailure()) {
                        log.error("链 {} 初始化失败，且配置为不继续运行，停止初始化", chainName);
                        break;
                    }
                }
            }
            
            if (successCount == 0) {
                if (blockchainProperties.isContinueOnFailure()) {
                    log.warn("所有区块链监听服务初始化失败，但应用将继续启动");
                } else {
                    log.error("所有区块链监听服务初始化失败，且配置为不继续运行");
                    throw new RuntimeException("区块链监听服务初始化失败");
                }
            } else {
                log.info("区块链监听服务初始化完成，成功启动 {}/{} 个链的监听", successCount, totalCount);
            }
            
        } catch (Exception e) {
            log.error("初始化区块链监听服务时发生错误", e);
            if (!blockchainProperties.isContinueOnFailure()) {
                throw new RuntimeException("区块链监听服务初始化失败", e);
            }
        }
    }
    
    /**
     * 检查指定链的监听功能是否启用
     */
    private boolean isListeningEnabled(String chainName) {
        // 检查链特定配置
        BlockchainProperties.ChainConfig chainConfig = blockchainProperties.getChainConfig(chainName);
        if (chainConfig == null) {
            log.debug("链 {} 未配置，默认禁用监听", chainName);
            return false; // 未配置时默认禁用
        }
        
        // 检查链是否启用且区块监听是否启用
        return chainConfig.isEnabled() && chainConfig.isBlockListeningEnabled();
    }
    
    /**
     * 检查指定链的交易监听是否启用
     */
    public boolean isTransactionListeningEnabled(String chainName) {
        BlockchainProperties.ChainConfig chainConfig = blockchainProperties.getChainConfig(chainName);
        if (chainConfig == null) {
            return false; // 未配置时默认禁用
        }
        
        return chainConfig.isEnabled() && chainConfig.isTransactionListeningEnabled();
    }
    
    /**
     * 检查指定链的合约事件监听是否启用
     */
    public boolean isContractEventListeningEnabled(String chainName) {
        BlockchainProperties.ChainConfig chainConfig = blockchainProperties.getChainConfig(chainName);
        if (chainConfig == null) {
            return false; // 未配置时默认禁用
        }
        
        return chainConfig.isEnabled() && chainConfig.isContractEventListeningEnabled();
    }
    
    /**
     * 启动指定合约的事件监听
     */
    public void startContractListening(String chainName, String contractAddress, String eventName) {
        try {
            eventPublisher.startContractEventListening(chainName, contractAddress, eventName);
            log.info("已启动合约事件监听: chain={}, contract={}, event={}", chainName, contractAddress, eventName);
        } catch (Exception e) {
            log.error("启动合约事件监听失败: chain={}, contract={}, event={}", chainName, contractAddress, eventName, e);
        }
    }
    
    /**
     * 停止指定合约的事件监听
     */
    public void stopContractListening(String chainName, String contractAddress, String eventName) {
        String key = chainName + "_" + contractAddress + "_" + eventName;
        eventPublisher.stopListening(key);
        log.info("已停止合约事件监听: {}", key);
    }
    
    /**
     * 停止指定链的区块监听
     */
    public void stopBlockListening(String chainName) {
        String key = chainName + "_block";
        eventPublisher.stopListening(key);
        log.info("已停止区块监听: {}", key);
    }
    
    /**
     * 获取当前监听的订阅数量
     */
    public int getActiveSubscriptionCount() {
        return eventPublisher.getActiveSubscriptionCount();
    }
    
    /**
     * 获取所有监听的键
     */
    public java.util.Set<String> getActiveSubscriptionKeys() {
        return eventPublisher.getActiveSubscriptionKeys();
    }
    
    /**
     * 销毁时停止所有监听
     */
    @PreDestroy
    public void destroy() {
        log.info("正在停止所有区块链监听服务...");
        eventPublisher.stopAllListening();
        log.info("所有区块链监听服务已停止");
    }
}
