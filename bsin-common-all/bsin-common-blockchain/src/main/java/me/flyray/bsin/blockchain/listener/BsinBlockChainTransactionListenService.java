package me.flyray.bsin.blockchain.listener;

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
import org.web3j.protocol.core.methods.response.EthLog;
import org.web3j.protocol.core.methods.response.Log;
import org.web3j.protocol.http.HttpService;
import org.web3j.protocol.websocket.WebSocketService;

import java.math.BigInteger;
import java.util.List;
import java.util.function.Consumer;

/**
 * 区块链交易监听服务或合约监听服务
 * 统一管理链上合约交易监听功能
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
            log.info("开始初始化交易监听: chain={}, contract={}", chainName, contractAddress);
            
            BlockchainProperties.ChainConfig chainConfig = blockchainProperties.getChainConfig(chainName);
            if (chainConfig == null) {
                log.warn("链 {} 未配置，跳过交易监听", chainName);
                return Disposables.empty();
            }
            
            if (!chainConfig.isTransactionListeningEnabled()) {
                log.info("链 {} 的交易监听已禁用", chainName);
                return Disposables.empty();
            }
            
            log.debug("链配置检查通过: chain={}, rpcUrl={}, wsUrl={}", 
                    chainName, chainConfig.getRpcUrl(), chainConfig.getWebSocketUrl());
            
            // 使用 WebSocket 连接进行实时监听
            String wsUrl = getWebSocketUrl(chainConfig);
            log.info("正在建立 WebSocket 连接: chain={}, wsUrl={}", chainName, wsUrl);
            
            WebSocketService ws = new WebSocketService(wsUrl, true);
            ws.connect();
            
            // 等待连接建立
            try {
                Thread.sleep(2000); // 等待连接建立
                log.debug("WebSocket 连接等待完成: chain={}", chainName);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                log.error("等待 WebSocket 连接时被中断: chain={}", chainName, e);
                throw new RuntimeException("WebSocket 连接等待被中断", e);
            }
            
            Web3j web3jWs = Web3j.build(ws);
            log.debug("WebSocket 连接建立完成: chain={}", chainName);
            
            // 设置过滤条件，监听最新的N个区块
            log.debug("正在获取当前区块号: chain={}", chainName);
            BigInteger currentBlock = getCurrentBlockNumberWithRetry(web3jWs, chainName);
            log.info("成功获取当前区块号: chain={}, 当前区块={}", chainName, currentBlock);
            
            // 扩大监听范围，确保能监听到更多历史交易
            BigInteger historyBlocks = new BigInteger("10000"); // 监听最近10000个区块
            BigInteger startBlock = currentBlock.subtract(historyBlocks);
            
            // 如果计算出的起始区块小于0，则从0开始
            if (startBlock.compareTo(BigInteger.ZERO) < 0) {
                startBlock = BigInteger.ZERO;
            }
            
            log.info("设置监听范围: chain={}, 当前区块={}, 历史区块数={}, 起始区块={}", 
                    chainName, currentBlock, historyBlocks, startBlock);
            
            EthFilter ethFilter = new EthFilter(DefaultBlockParameter.valueOf(startBlock),
                    DefaultBlockParameterName.LATEST, contractAddress);
            
            log.info("开始监听链 {} 的合约 {} 交易事件，监听范围: {} - latest", 
                    chainName, contractAddress, startBlock);
            
            log.debug("正在建立事件流订阅: chain={}, contract={}", chainName, contractAddress);
            Disposable subscription = web3jWs.ethLogFlowable(ethFilter).subscribe(
                logEvent -> {
                    log.info("🎯 监听到交易事件: chain={}, contract={}, txHash={}, blockNumber={}, logIndex={}, topics={}", 
                            chainName, contractAddress, logEvent.getTransactionHash(), 
                            logEvent.getBlockNumber(), logEvent.getLogIndex(), logEvent.getTopics());
                    
                    // 打印完整的日志信息用于调试
                    log.debug("完整日志信息: {}", logEvent);
                    
                    try {
                        transactionHandler.accept(logEvent);
                        log.info("✅ 交易事件处理完成: chain={}, txHash={}", chainName, logEvent.getTransactionHash());
                    } catch (Exception e) {
                        log.error("❌ 处理交易事件时发生异常: chain={}, txHash={}, error={}", 
                                chainName, logEvent.getTransactionHash(), e.getMessage(), e);
                    }
                },
                error -> {
                    log.error("监听合约交易发生错误: chain={}, contract={}, error={}", 
                            chainName, contractAddress, error.getMessage(), error);
                    
                    // 检查是否是 WebSocket 连接问题
                    if (error.getMessage() != null && 
                        (error.getMessage().contains("WebsocketNotConnectedException") || 
                         error.getMessage().contains("WebSocket connection closed"))) {
                        log.warn("检测到 WebSocket 连接断开，建议重新建立连接: chain={}, contract={}", 
                                chainName, contractAddress);
                    }
                }
            );
            
            log.info("事件流订阅建立成功: chain={}, contract={}, subscription={}", 
                    chainName, contractAddress, subscription.isDisposed() ? "已释放" : "活跃");
            
            return subscription;
            
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
        log.info("开始监听多个合约交易: chain={}, 合约数量={}", chainName, contractAddresses.size());
        
        java.util.List<Disposable> disposables = new java.util.ArrayList<>();
        int successCount = 0;
        int failCount = 0;
        
        for (String contractAddress : contractAddresses) {
            log.debug("正在为合约 {} 建立交易监听", contractAddress);
            
            try {
                Disposable disposable = listenContractTransactions(chainName, contractAddress, transactionHandler);
                if (!disposable.isDisposed()) {
                    disposables.add(disposable);
                    successCount++;
                    log.debug("合约 {} 交易监听建立成功", contractAddress);
                } else {
                    failCount++;
                    log.warn("合约 {} 交易监听建立失败，返回的 Disposable 已释放", contractAddress);
                }
            } catch (Exception e) {
                failCount++;
                log.error("为合约 {} 建立交易监听时发生异常: {}", contractAddress, e.getMessage(), e);
            }
        }
        
        log.info("多合约交易监听完成: chain={}, 成功={}, 失败={}, 总计={}", 
                chainName, successCount, failCount, contractAddresses.size());
        
        return disposables;
    }
    
    /**
     * 获取 WebSocket URL
     */
    private String getWebSocketUrl(BlockchainProperties.ChainConfig chainConfig) {
        log.debug("开始获取 WebSocket URL: rpcUrl={}, wsUrl={}", 
                chainConfig.getRpcUrl(), chainConfig.getWebSocketUrl());
        
        // 优先使用配置的 WebSocket URL
        if (chainConfig.getWebSocketUrl() != null && !chainConfig.getWebSocketUrl().isEmpty()) {
            log.debug("使用配置的 WebSocket URL: {}", chainConfig.getWebSocketUrl());
            return chainConfig.getWebSocketUrl();
        }
        
        // 如果没有配置 WebSocket URL，尝试将 HTTP URL 转换为 WebSocket URL
        String rpcUrl = chainConfig.getRpcUrl();
        if (rpcUrl != null && rpcUrl.startsWith("http://")) {
            String wsUrl = rpcUrl.replace("http://", "ws://");
            log.debug("从 HTTP URL 转换为 WebSocket URL: {} -> {}", rpcUrl, wsUrl);
            return wsUrl;
        } else if (rpcUrl != null && rpcUrl.startsWith("https://")) {
            String wsUrl = rpcUrl.replace("https://", "wss://");
            log.debug("从 HTTPS URL 转换为 WebSocket URL: {} -> {}", rpcUrl, wsUrl);
            return wsUrl;
        }
        
        // 如果都无法转换，返回原 RPC URL（可能会失败，但至少不会报错）
        log.warn("无法获取有效的 WebSocket URL，使用原 RPC URL: {}", rpcUrl);
        return rpcUrl;
    }
    
    /**
     * 获取 HTTP Web3j 实例
     */
    public Web3j getHttpWeb3jInstance(String chainName) {
        log.debug("开始获取 HTTP Web3j 实例: chain={}", chainName);
        
        BlockchainProperties.ChainConfig chainConfig = blockchainProperties.getChainConfig(chainName);
        if (chainConfig == null) {
            log.error("链 {} 未配置，无法获取 HTTP Web3j 实例", chainName);
            throw new IllegalArgumentException("链 " + chainName + " 未配置");
        }
        
        String rpcUrl = chainConfig.getRpcUrl();
        log.debug("使用 RPC URL 创建 HTTP Web3j 实例: chain={}, rpcUrl={}", chainName, rpcUrl);
        
        Web3j web3j = Web3j.build(new HttpService(rpcUrl));
        log.debug("HTTP Web3j 实例创建成功: chain={}", chainName);
        
        return web3j;
    }
    
    /**
     * 获取 WebSocket Web3j 实例
     */
    public Web3j getWebSocketWeb3jInstance(String chainName) {
        log.debug("开始获取 WebSocket Web3j 实例: chain={}", chainName);
        
        BlockchainProperties.ChainConfig chainConfig = blockchainProperties.getChainConfig(chainName);
        if (chainConfig == null) {
            log.error("链 {} 未配置，无法获取 WebSocket Web3j 实例", chainName);
            throw new IllegalArgumentException("链 " + chainName + " 未配置");
        }
        
        try {
            String wsUrl = getWebSocketUrl(chainConfig);
            log.debug("使用 WebSocket URL 创建连接: chain={}, wsUrl={}", chainName, wsUrl);
            
            WebSocketService ws = new WebSocketService(wsUrl, true);
            ws.connect();
            
            log.debug("WebSocket 连接建立成功，创建 Web3j 实例: chain={}", chainName);
            Web3j web3j = Web3j.build(ws);
            
            log.debug("WebSocket Web3j 实例创建成功: chain={}", chainName);
            return web3j;
        } catch (Exception e) {
            log.error("创建 WebSocket 连接失败: chain={}, error={}", chainName, e.getMessage(), e);
            throw new RuntimeException("创建 WebSocket 连接失败", e);
        }
    }
    
    /**
     * 带重试机制的获取当前区块号
     */
    private BigInteger getCurrentBlockNumberWithRetry(Web3j web3j, String chainName) {
        int maxRetries = 3;
        int retryCount = 0;
        
        while (retryCount < maxRetries) {
            try {
                log.debug("尝试获取当前区块号: chain={}, 尝试次数={}", chainName, retryCount + 1);
                BigInteger blockNumber = web3j.ethBlockNumber().send().getBlockNumber();
                log.debug("成功获取当前区块号: chain={}, 区块号={}", chainName, blockNumber);
                return blockNumber;
            } catch (Exception e) {
                retryCount++;
                log.warn("获取当前区块号失败: chain={}, 尝试次数={}, 错误={}", 
                        chainName, retryCount, e.getMessage());
                
                if (retryCount >= maxRetries) {
                    log.error("获取当前区块号最终失败: chain={}, 最大重试次数={}", chainName, maxRetries);
                    throw new RuntimeException("无法获取当前区块号", e);
                }
                
                // 等待后重试
                try {
                    Thread.sleep(2000 * retryCount); // 递增等待时间
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    throw new RuntimeException("获取区块号时被中断", ie);
                }
            }
        }
        
        throw new RuntimeException("获取当前区块号失败，已达到最大重试次数");
    }
    
    /**
     * 检查链环境信息
     */
    public void checkChainInfo(String chainName) {
        try {
            log.info("🔍 检查链环境信息: chain={}", chainName);
            
            Web3j web3j = getHttpWeb3jInstance(chainName);
            BigInteger currentBlock = web3j.ethBlockNumber().send().getBlockNumber();
            
            log.info("📊 链环境信息: chain={}, 当前区块={}", chainName, currentBlock);
            
            // 检查网络ID
            try {
                String networkId = web3j.netVersion().send().getResult();
                log.info("🌐 网络ID: {}", networkId);
                
                // 根据网络ID判断链类型
                switch (networkId) {
                    case "59144":
                        log.info("✅ 这是 Linea Sepolia 测试网");
                        break;
                    case "11155111":
                        log.info("✅ 这是 Ethereum Sepolia 测试网");
                        break;
                    case "1":
                        log.info("✅ 这是 Ethereum 主网");
                        break;
                    default:
                        log.warn("⚠️ 未知的网络ID: {}", networkId);
                }
            } catch (Exception e) {
                log.warn("无法获取网络ID: {}", e.getMessage());
            }
            
            // 检查特定区块是否存在
            try {
                BigInteger testBlock = new BigInteger("9186639");
                if (currentBlock.compareTo(testBlock) >= 0) {
                    log.info("✅ 目标区块 {} 在当前链上存在", testBlock);
                } else {
                    log.warn("⚠️ 目标区块 {} 不在当前链上，当前最高区块: {}", testBlock, currentBlock);
                }
            } catch (Exception e) {
                log.warn("检查目标区块失败: {}", e.getMessage());
            }
            
        } catch (Exception e) {
            log.error("❌ 检查链环境信息失败: chain={}, error={}", chainName, e.getMessage(), e);
        }
    }
    
    /**
     * 测试监听是否正常工作
     * 通过查询历史日志来验证监听范围
     */
    public void testListening(String chainName, String contractAddress) {
        try {
            log.info("🧪 开始测试监听功能: chain={}, contract={}", chainName, contractAddress);
            
            Web3j web3j = getHttpWeb3jInstance(chainName);
            
            // 查询包含您最新交易区块的范围 (9186639)
            BigInteger targetBlock = new BigInteger("9186639");
            BigInteger startBlock = targetBlock.subtract(new BigInteger("10")); // 从目标区块前10个区块开始
            BigInteger endBlock = targetBlock.add(new BigInteger("10")); // 到目标区块后10个区块结束
            
            log.info("🔍 查询历史日志: chain={}, 从区块 {} 到 {}, 目标区块={}", 
                    chainName, startBlock, endBlock, targetBlock);
            
            EthFilter ethFilter = new EthFilter(DefaultBlockParameter.valueOf(startBlock),
                    DefaultBlockParameter.valueOf(endBlock), contractAddress);
            
            List<EthLog.LogResult> logs = web3j.ethGetLogs(ethFilter).send().getLogs();
            log.info("📊 查询结果: 找到 {} 条历史日志", logs.size());
            
            for (int i = 0; i < logs.size(); i++) {
                EthLog.LogObject logObject = (EthLog.LogObject) logs.get(i).get();
                log.info("📝 历史日志 {}: txHash={}, blockNumber={}, topics={}", 
                        i + 1, logObject.getTransactionHash(), logObject.getBlockNumber(), logObject.getTopics());
            }
            
        } catch (Exception e) {
            log.error("❌ 测试监听功能失败: chain={}, contract={}, error={}", 
                    chainName, contractAddress, e.getMessage(), e);
        }
    }
    
    /**
     * 直接查询特定交易
     */
    public void querySpecificTransaction(String chainName, String txHash) {
        try {
            log.info("🔍 查询特定交易: chain={}, txHash={}", chainName, txHash);
            
            Web3j web3j = getHttpWeb3jInstance(chainName);
            
            // 查询交易详情
            var tx = web3j.ethGetTransactionByHash(txHash).send();
            if (tx.getTransaction().isPresent()) {
                var transaction = tx.getTransaction().get();
                log.info("📝 交易详情: blockNumber={}, from={}, to={}, value={}", 
                        transaction.getBlockNumber(), transaction.getFrom(), 
                        transaction.getTo(), transaction.getValue());
            } else {
                log.warn("⚠️ 未找到交易: {}", txHash);
            }
            
            // 查询交易回执
            var receipt = web3j.ethGetTransactionReceipt(txHash).send();
            if (receipt.getTransactionReceipt().isPresent()) {
                var txReceipt = receipt.getTransactionReceipt().get();
                log.info("📋 交易回执: blockNumber={}, status={}, logsCount={}", 
                        txReceipt.getBlockNumber(), txReceipt.getStatus(), 
                        txReceipt.getLogs().size());
                
                // 打印所有日志
                for (int i = 0; i < txReceipt.getLogs().size(); i++) {
                    var logEvent = txReceipt.getLogs().get(i);
                    log.info("📄 日志 {}: address={}, topics={}, data={}", 
                            i + 1, logEvent.getAddress(), logEvent.getTopics(), logEvent.getData());
                }
            } else {
                log.warn("⚠️ 未找到交易回执: {}", txHash);
            }
            
        } catch (Exception e) {
            log.error("❌ 查询特定交易失败: chain={}, txHash={}, error={}", 
                    chainName, txHash, e.getMessage(), e);
        }
    }
}
