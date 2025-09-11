package me.flyray.bsin.blockchain.service;

import io.reactivex.disposables.Disposable;
import lombok.extern.slf4j.Slf4j;
import me.flyray.bsin.blockchain.config.BlockchainProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;

import java.math.BigInteger;
import java.util.function.Consumer;

/**
 * @author bolei
 * @date 2024/3/14
 * @desc 链上区块链监听
 */
@Slf4j
@Service
public class BsinBlockChainBlockListonService {
    
    @Autowired
    private BlockchainProperties blockchainProperties;

    /**
     * 监听新区块
     * @param chainEnv 链环境
     * @param blockHandler 区块处理器
     * @return 订阅对象，用于取消订阅
     */
    public Disposable listenNewBlocks(String chainEnv, Consumer<BigInteger> blockHandler) {
        try {
            Web3j web3j = getWeb3jInstance(chainEnv);
            return web3j.blockFlowable(false).subscribe(
                block -> {
                    BigInteger blockNumber = block.getBlock().getNumber();
                    log.info("监听到新区块: chain={}, blockNumber={}", chainEnv, blockNumber);
                    blockHandler.accept(blockNumber);
                },
                error -> {
                    log.error("监听新区块发生错误: chain={}, error={}", chainEnv, error.getMessage(), error);
                    // 不抛出异常，优雅降级
                }
            );
        } catch (Exception e) {
            log.error("监听新区块初始化失败: chain={}", chainEnv, e);
            // 返回一个空的 Disposable，避免阻塞应用启动
            return io.reactivex.disposables.Disposables.empty();
        }
    }
    
    /**
     * 监听待确认交易
     * @param chainEnv 链环境
     * @param transactionHandler 交易处理器
     * @return 订阅对象，用于取消订阅
     */
    public Disposable listenPendingTransactions(String chainEnv, Consumer<String> transactionHandler) {
        try {
            Web3j web3j = getWeb3jInstance(chainEnv);
            return web3j.pendingTransactionFlowable().subscribe(
                txHash -> {
                    log.info("监听到待确认交易: chain={}, txHash={}", chainEnv, txHash);
                    transactionHandler.accept(String.valueOf(txHash));
                },
                error -> {
                    log.error("监听待确认交易发生错误: chain={}, error={}", chainEnv, error.getMessage(), error);
                    // 不抛出异常，优雅降级
                }
            );
        } catch (Exception e) {
            log.error("监听待确认交易初始化失败: chain={}", chainEnv, e);
            // 返回一个空的 Disposable，避免阻塞应用启动
            return io.reactivex.disposables.Disposables.empty();
        }
    }
    
    /**
     * 根据链环境获取Web3j实例
     */
    private Web3j getWeb3jInstance(String chainEnv) {
        try {
            String rpcUrl = getRpcUrl(chainEnv);
            if (rpcUrl == null || rpcUrl.trim().isEmpty()) {
                throw new IllegalArgumentException("链 " + chainEnv + " 的 RPC 端点未配置");
            }
            
            log.info("使用 RPC 端点: chain={}, url={}", chainEnv, rpcUrl);
            return Web3j.build(new HttpService(rpcUrl));
        } catch (Exception e) {
            log.error("创建 Web3j 实例失败: chain={}", chainEnv, e);
            throw new RuntimeException("无法连接到链 " + chainEnv, e);
        }
    }
    
    /**
     * 根据链环境获取 RPC 端点
     */
    private String getRpcUrl(String chainEnv) {
        // 优先从配置文件中获取
        BlockchainProperties.ChainConfig chainConfig = blockchainProperties.getChainConfig(chainEnv);
        if (chainConfig != null && chainConfig.getRpcUrl() != null && !chainConfig.getRpcUrl().trim().isEmpty()) {
            return chainConfig.getRpcUrl();
        }
        
        // 如果配置文件中没有，则使用默认值
        log.warn("链 {} 未在配置文件中配置 RPC 端点，使用默认值", chainEnv);
        switch (chainEnv.toLowerCase()) {
            case "conflux":
                return "https://test.confluxrpc.com";
            case "bsc":
                return "https://bsc-testnet.public.blastapi.io";
            case "polygon":
                return "https://polygon-mumbai.public.blastapi.io";
            default:
                log.error("未知的链环境: {}", chainEnv);
                return null;
        }
    }
}
