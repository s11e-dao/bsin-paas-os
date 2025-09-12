package me.flyray.bsin.blockchain.core;

import io.reactivex.disposables.Disposable;
import me.flyray.bsin.blockchain.core.BsinBlockChainEngine;
import me.flyray.bsin.blockchain.enums.ChainType;
import org.web3j.protocol.core.methods.response.Log;

import java.math.BigInteger;
import java.util.List;
import java.util.function.Consumer;

/**
 * 区块链服务统一接口
 * 整合所有区块链相关的基础服务
 */
public interface BlockchainService {
    
    // ========== 事件监听相关 ==========
    
    /**
     * 监听合约事件
     */
    Disposable listenContractEvent(String chainEnv, String contractAddress, String eventName, Consumer<Log> eventHandler);
    
    /**
     * 监听区块事件
     */
    Disposable listenBlockEvent(String chainEnv, Consumer<BigInteger> blockHandler);
    
    /**
     * 监听新区块
     */
    Disposable listenNewBlocks(String chainEnv, Consumer<BigInteger> blockHandler);
    
    /**
     * 监听待确认交易
     */
    Disposable listenPendingTransactions(String chainEnv, Consumer<String> transactionHandler);
    
    // ========== 事件发布相关 ==========
    
    /**
     * 发布交易确认事件
     */
    void publishTransactionConfirmed(String chainName, String txHash, String fromAddress, 
                                   String toAddress, BigInteger amount, long blockNumber, String contractAddress);
    
    /**
     * 发布交易失败事件
     */
    void publishTransactionFailed(String chainName, String txHash, String fromAddress, 
                                String toAddress, BigInteger amount, String errorMessage, String contractAddress);
    
    /**
     * 发布 Gas 费用不足事件
     */
    void publishInsufficientGas(String chainName, String address, String requiredGas, String currentGas);
    
    // ========== 监听管理相关 ==========
    
    /**
     * 启动合约事件监听
     */
    void startContractEventListening(String chainName, String contractAddress, String eventName);
    
    /**
     * 启动区块事件监听
     */
    void startBlockEventListening(String chainName);
    
    /**
     * 停止监听
     */
    void stopListening(String key);
    
    /**
     * 停止所有监听
     */
    void stopAllListening();
    
    // ========== 监控指标相关 ==========
    
    /**
     * 记录交易成功
     */
    void recordTransactionSuccess(String chainName, String contractAddress);
    
    /**
     * 记录交易失败
     */
    void recordTransactionFailure(String chainName, String errorType);
    
    /**
     * 开始交易计时
     */
    io.micrometer.core.instrument.Timer.Sample startTransactionTimer(String chainName);
    
    /**
     * 记录 Gas 费用
     */
    void recordGasFee(String chainName, String gasFee);
    
    /**
     * 记录交易确认时间
     */
    void recordConfirmationTime(String chainName, long confirmationTimeMs);
    
    // ========== 多链管理相关 ==========
    
    /**
     * 获取指定链的区块链引擎
     */
    BsinBlockChainEngine getBlockchainEngine(String chainName);
    
    /**
     * 获取所有启用的链名称
     */
    List<String> getEnabledChains();
    
    /**
     * 检查指定链是否启用
     */
    boolean isChainEnabled(String chainName);
    
    /**
     * 重新初始化指定链
     */
    void reinitializeChain(String chainName);
    
    /**
     * 获取链类型信息
     */
    ChainType getChainType(String chainName);
    
    /**
     * 获取所有支持的链类型
     */
    List<ChainType> getSupportedChainTypes();
}
