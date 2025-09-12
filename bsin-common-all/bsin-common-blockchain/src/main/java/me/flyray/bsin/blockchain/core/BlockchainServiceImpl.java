package me.flyray.bsin.blockchain.core;

import io.micrometer.core.instrument.Timer;
import io.reactivex.disposables.Disposable;
import lombok.extern.slf4j.Slf4j;
import me.flyray.bsin.blockchain.enums.ChainType;
import me.flyray.bsin.blockchain.listener.BlockchainEventPublisher;
import me.flyray.bsin.blockchain.metrics.BlockchainMetrics;
import me.flyray.bsin.blockchain.connection.BsinBlockChainEngineFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.web3j.protocol.core.methods.response.Log;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * 区块链服务统一实现
 * 整合所有区块链相关的基础服务
 */
@Slf4j
@Service
public class BlockchainServiceImpl implements BlockchainService {
    
    @Autowired
    private BlockchainBaseService blockchainBaseService;
    
    @Autowired
    private BlockchainEventPublisher eventPublisher;
    
    @Autowired
    private BlockchainMetrics blockchainMetrics;
    
    @Autowired
    private BsinBlockChainEngineFactory blockchainEngineFactory;
    
    // ========== 事件监听相关 ==========
    
    @Override
    public Disposable listenContractEvent(String chainEnv, String contractAddress, String eventName, Consumer<Log> eventHandler) {
        return blockchainBaseService.listenContractEvent(chainEnv, contractAddress, eventName, eventHandler);
    }
    
    @Override
    public Disposable listenBlockEvent(String chainEnv, Consumer<BigInteger> blockHandler) {
        return blockchainBaseService.listenBlockEvent(chainEnv, blockHandler);
    }
    
    @Override
    public Disposable listenNewBlocks(String chainEnv, Consumer<BigInteger> blockHandler) {
        return blockchainBaseService.listenNewBlocks(chainEnv, blockHandler);
    }
    
    @Override
    public Disposable listenPendingTransactions(String chainEnv, Consumer<String> transactionHandler) {
        return blockchainBaseService.listenPendingTransactions(chainEnv, transactionHandler);
    }
    
    // ========== 事件发布相关 ==========
    
    @Override
    public void publishTransactionConfirmed(String chainName, String txHash, String fromAddress, 
                                          String toAddress, BigInteger amount, long blockNumber, String contractAddress) {
        eventPublisher.publishTransactionConfirmed(chainName, txHash, fromAddress, toAddress, amount, blockNumber, contractAddress);
    }
    
    @Override
    public void publishTransactionFailed(String chainName, String txHash, String fromAddress, 
                                       String toAddress, BigInteger amount, String errorMessage, String contractAddress) {
        eventPublisher.publishTransactionFailed(chainName, txHash, fromAddress, toAddress, amount, errorMessage, contractAddress);
    }
    
    @Override
    public void publishInsufficientGas(String chainName, String address, String requiredGas, String currentGas) {
        eventPublisher.publishInsufficientGas(chainName, address, requiredGas, currentGas);
    }
    
    // ========== 监听管理相关 ==========
    
    @Override
    public void startContractEventListening(String chainName, String contractAddress, String eventName) {
        eventPublisher.startContractEventListening(chainName, contractAddress, eventName);
    }
    
    @Override
    public void startBlockEventListening(String chainName) {
        eventPublisher.startBlockEventListening(chainName);
    }
    
    @Override
    public void stopListening(String key) {
        eventPublisher.stopListening(key);
    }
    
    @Override
    public void stopAllListening() {
        eventPublisher.stopAllListening();
    }
    
    // ========== 监控指标相关 ==========
    
    @Override
    public void recordTransactionSuccess(String chainName, String contractAddress) {
        blockchainMetrics.recordTransactionSuccess(chainName, contractAddress);
    }
    
    @Override
    public void recordTransactionFailure(String chainName, String errorType) {
        blockchainMetrics.recordTransactionFailure(chainName, errorType);
    }
    
    @Override
    public Timer.Sample startTransactionTimer(String chainName) {
        return blockchainMetrics.startTransactionTimer(chainName);
    }
    
    @Override
    public void recordGasFee(String chainName, String gasFee) {
        blockchainMetrics.recordGasFee(chainName, gasFee);
    }
    
    @Override
    public void recordConfirmationTime(String chainName, long confirmationTimeMs) {
        blockchainMetrics.recordConfirmationTime(chainName, confirmationTimeMs);
    }
    
    // ========== 多链管理相关 ==========
    
    @Override
    public BsinBlockChainEngine getBlockchainEngine(String chainName) {
        return blockchainEngineFactory.getBsinBlockChainEngineInstance(chainName);
    }
    
    @Override
    public List<String> getEnabledChains() {
        // 返回支持的链类型列表
        return blockchainEngineFactory.getSupportedChainTypes();
    }
    
    @Override
    public boolean isChainEnabled(String chainName) {
        // 检查链类型是否被支持
        return blockchainEngineFactory.isChainTypeSupported(chainName);
    }
    
    @Override
    public void reinitializeChain(String chainName) {
        // 清除缓存并重新初始化
        blockchainEngineFactory.clearCache();
        blockchainEngineFactory.getBsinBlockChainEngineInstance(chainName);
    }
    
    @Override
    public ChainType getChainType(String chainName) {
        try {
            return ChainType.valueOf(chainName.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("不支持的链类型: " + chainName);
        }
    }
    
    @Override
    public List<ChainType> getSupportedChainTypes() {
        List<ChainType> supportedTypes = new ArrayList<>();
        for (String chainName : blockchainEngineFactory.getSupportedChainTypes()) {
            try {
                supportedTypes.add(ChainType.valueOf(chainName.toUpperCase()));
            } catch (IllegalArgumentException e) {
                // 跳过不支持的链类型
            }
        }
        return supportedTypes;
    }
}
