package me.flyray.bsin.blockchain.service;

import io.reactivex.disposables.Disposable;
import io.reactivex.disposables.Disposables;
import lombok.extern.slf4j.Slf4j;
import me.flyray.bsin.blockchain.config.BlockchainProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.request.EthFilter;
import org.web3j.protocol.core.methods.response.Log;
import org.web3j.protocol.http.HttpService;
import org.web3j.protocol.websocket.WebSocketService;

import java.math.BigInteger;
import java.util.function.Consumer;

/**
 * 区块链交易监听服务
 * 统一管理链上交易监听功能
 */
@Slf4j
@Service
public class BsinBlockChainTransactionListenService {
    
    @Autowired
    private BlockchainProperties blockchainProperties;
    
    /**
     * 监听合约交易事件
     * @param chainName 链名称
     * @param contractAddress 合约地址
     * @param transactionHandler 交易处理器
     * @return 订阅对象，用于取消订阅
     */
    public Disposable listenContractTransactions(String chainName, String contractAddress, Consumer<Log> transactionHandler) {
        try {
            BlockchainProperties.ChainConfig chainConfig = blockchainProperties.getChainConfig(chainName);
            if (chainConfig == null) {
                log.warn("链 {} 未配置，跳过交易监听", chainName);
                return Disposables.empty();
            }
            
            if (!chainConfig.isTransactionListeningEnabled()) {
                log.info("链 {} 的交易监听已禁用", chainName);
                return Disposables.empty();
            }
            
            // 使用 WebSocket 连接进行实时监听
            WebSocketService ws = new WebSocketService(getWebSocketUrl(chainConfig), true);
            ws.connect();
            Web3j web3jWs = Web3j.build(ws);
            
            // 设置过滤条件，监听最新的N个区块
            BigInteger blockNumber = web3jWs.ethBlockNumber().send().getBlockNumber()
                    .subtract(new BigInteger(String.valueOf(chainConfig.getTransactionListenHistoryBlocks())));
            EthFilter ethFilter = new EthFilter(DefaultBlockParameter.valueOf(blockNumber),
                    DefaultBlockParameterName.LATEST, contractAddress);
            
            log.info("开始监听链 {} 的合约 {} 交易事件", chainName, contractAddress);
            
            return web3jWs.ethLogFlowable(ethFilter).subscribe(
                logEvent -> {
                    log.debug("监听到交易事件: chain={}, contract={}, txHash={}", 
                            chainName, contractAddress, logEvent.getTransactionHash());
                    transactionHandler.accept(logEvent);
                },
                error -> {
                    log.error("监听合约交易发生错误: chain={}, contract={}, error={}", 
                            chainName, contractAddress, error.getMessage(), error);
                }
            );
            
        } catch (Exception e) {
            log.error("初始化合约交易监听失败: chain={}, contract={}", chainName, contractAddress, e);
            return Disposables.empty();
        }
    }
    
    /**
     * 监听多个合约的交易事件
     * @param chainName 链名称
     * @param contractAddresses 合约地址列表
     * @param transactionHandler 交易处理器
     * @return 订阅对象列表
     */
    public java.util.List<Disposable> listenMultipleContractTransactions(String chainName, 
            java.util.List<String> contractAddresses, Consumer<Log> transactionHandler) {
        java.util.List<Disposable> disposables = new java.util.ArrayList<>();
        
        for (String contractAddress : contractAddresses) {
            Disposable disposable = listenContractTransactions(chainName, contractAddress, transactionHandler);
            if (!disposable.isDisposed()) {
                disposables.add(disposable);
            }
        }
        
        return disposables;
    }
    
    /**
     * 获取 WebSocket URL
     */
    private String getWebSocketUrl(BlockchainProperties.ChainConfig chainConfig) {
        // 优先使用配置的 WebSocket URL
        if (chainConfig.getWebSocketUrl() != null && !chainConfig.getWebSocketUrl().isEmpty()) {
            return chainConfig.getWebSocketUrl();
        }
        
        // 如果没有配置 WebSocket URL，尝试将 HTTP URL 转换为 WebSocket URL
        String rpcUrl = chainConfig.getRpcUrl();
        if (rpcUrl != null && rpcUrl.startsWith("http://")) {
            return rpcUrl.replace("http://", "ws://");
        } else if (rpcUrl != null && rpcUrl.startsWith("https://")) {
            return rpcUrl.replace("https://", "wss://");
        }
        
        // 如果都无法转换，返回原 RPC URL（可能会失败，但至少不会报错）
        return rpcUrl;
    }
    
    /**
     * 获取 HTTP Web3j 实例
     */
    public Web3j getHttpWeb3jInstance(String chainName) {
        BlockchainProperties.ChainConfig chainConfig = blockchainProperties.getChainConfig(chainName);
        if (chainConfig == null) {
            throw new IllegalArgumentException("链 " + chainName + " 未配置");
        }
        return Web3j.build(new HttpService(chainConfig.getRpcUrl()));
    }
    
    /**
     * 获取 WebSocket Web3j 实例
     */
    public Web3j getWebSocketWeb3jInstance(String chainName) {
        BlockchainProperties.ChainConfig chainConfig = blockchainProperties.getChainConfig(chainName);
        if (chainConfig == null) {
            throw new IllegalArgumentException("链 " + chainName + " 未配置");
        }
        
        try {
            WebSocketService ws = new WebSocketService(getWebSocketUrl(chainConfig), true);
            ws.connect();
            return Web3j.build(ws);
        } catch (Exception e) {
            log.error("创建 WebSocket 连接失败: chain={}", chainName, e);
            throw new RuntimeException("创建 WebSocket 连接失败", e);
        }
    }
}
