package me.flyray.bsin.blockchain.service;

import io.reactivex.disposables.Disposable;
import org.web3j.protocol.core.methods.response.Log;

import java.math.BigInteger;
import java.util.function.Consumer;

/**
 * 区块链基础服务接口
 * 提供事件监听、区块监听等基础功能
 */
public interface BlockchainBaseService {
    
    /**
     * 监听合约事件
     * @param chainEnv 链环境
     * @param contractAddress 合约地址
     * @param eventName 事件名称
     * @param eventHandler 事件处理器
     * @return 订阅对象，用于取消订阅
     */
    Disposable listenContractEvent(String chainEnv, String contractAddress, String eventName, Consumer<Log> eventHandler);
    
    /**
     * 监听区块事件
     * @param chainEnv 链环境
     * @param blockHandler 区块处理器
     * @return 订阅对象，用于取消订阅
     */
    Disposable listenBlockEvent(String chainEnv, Consumer<BigInteger> blockHandler);
    
    /**
     * 监听新区块
     * @param chainEnv 链环境
     * @param blockHandler 区块处理器
     * @return 订阅对象，用于取消订阅
     */
    Disposable listenNewBlocks(String chainEnv, Consumer<BigInteger> blockHandler);
    
    /**
     * 监听待确认交易
     * @param chainEnv 链环境
     * @param transactionHandler 交易处理器
     * @return 订阅对象，用于取消订阅
     */
    Disposable listenPendingTransactions(String chainEnv, Consumer<String> transactionHandler);
}
