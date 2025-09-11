package me.flyray.bsin.blockchain.config;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 区块链配置属性
 * 用于控制区块链监听服务的启用状态和配置
 */
@Slf4j
@Data
@Component
@ConfigurationProperties(prefix = "bsin.blockchain")
public class BlockchainProperties {
    
    /**
     * 是否启用区块链监听服务
     */
    private boolean enabled = false;
    
    /**
     * 是否在启动失败时继续运行应用
     */
    private boolean continueOnFailure = true;
    
    /**
     * 支持的链列表（从配置中读取，避免硬编码）
     */
    private List<String> supportedChains = new ArrayList<>();

    /**
     * MPC 网关 URL
     */
    private String gatewayUrl = "http://127.0.0.1:8125";

    /**
     * Gas 费用配置
     */
    private GasFeeConfig gasFee = new GasFeeConfig();

    /**
     * 各链的 RPC 端点配置
     */
    private Map<String, ChainConfig> chains = new HashMap<>();

    @Data
    public static class GasFeeConfig {
        /**
         * Gas 费用账户地址
         */
        private String address = "0x5d90A41098954fd90eb70805b3E9442AF9E91625";

        /**
         * Gas 费用金额
         */
        private Integer amount = 1000;
    }
    
    @Data
    public static class ChainConfig {
        /**
         * RPC 端点 URL
         */
        private String rpcUrl;
        
        /**
         * 是否启用该链
         */
        private boolean enabled = false;
        
        /**
         * 是否启用区块监听
         */
        private boolean blockListeningEnabled = false;
        
        /**
         * 是否启用交易监听
         */
        private boolean transactionListeningEnabled = false;
        
        /**
         * 是否启用合约事件监听
         */
        private boolean contractEventListeningEnabled = false;
        
        /**
         * API 密钥（如果需要）
         */
        private String apiKey;
        
        /**
         * 链 ID
         */
        private Long chainId;
        
        /**
         * 链名称
         */
        private String name;
        
        /**
         * 链符号
         */
        private String symbol;
        
        /**
         * 是否为主网
         */
        private boolean mainnet = false;
        
        /**
         * 区块确认数
         */
        private int confirmations = 12;
        
            /**
             * 连接池大小
             */
            private int connectionPoolSize = 10;

            /**
             * 连接超时时间（毫秒）
             */
            private long connectionTimeoutMs = 10000;

            /**
             * 读取超时时间（毫秒）
             */
            private long readTimeoutMs = 30000;

            /**
             * 重试次数
             */
            private int retryCount = 3;

            /**
             * 重试间隔（毫秒）
             */
            private long retryIntervalMs = 5000;

            /**
             * WebSocket 连接 URL（用于实时监听）
             */
            private String webSocketUrl;

            /**
             * 交易监听历史区块数（默认监听最近100个区块）
             */
            private int transactionListenHistoryBlocks = 100;

            /**
             * 是否启用交易监听缓存防重复
             */
            private boolean enableTransactionCache = true;

            /**
             * 交易缓存过期时间（秒）
             */
            private int transactionCacheExpireSeconds = 300;
    }
    
    /**
     * 获取启用的链列表
     */
    public Set<String> getEnabledChains() {
        return chains.entrySet().stream()
                .filter(entry -> entry.getValue().isEnabled())
                .map(Map.Entry::getKey)
                .collect(java.util.stream.Collectors.toSet());
    }
    
    /**
     * 检查是否启用区块链服务
     */
    public boolean isBlockchainEnabled() {
        return enabled && !chains.isEmpty();
    }
    
    /**
     * 获取指定链的配置
     */
    public ChainConfig getChainConfig(String chainName) {
        return chains.get(chainName);
    }
    
    /**
     * 检查指定链是否启用
     */
    public boolean isChainEnabled(String chainName) {
        ChainConfig config = getChainConfig(chainName);
        return config != null && config.isEnabled();
    }
    
    /**
     * 获取支持的链列表
     * 如果配置文件中没有指定，则返回空列表（默认不开启任何链）
     */
    public List<String> getSupportedChains() {
        if (supportedChains.isEmpty()) {
            log.info("未配置支持的链列表，默认不开启任何链");
            return new ArrayList<>();
        }
        return new ArrayList<>(supportedChains);
    }
    
    /**
     * 检查链是否在支持列表中
     */
    public boolean isChainSupported(String chainName) {
        return getSupportedChains().contains(chainName.toLowerCase());
    }
    
    /**
     * 获取链的智能默认配置
     * 根据链名称返回合理的默认配置
     */
    public ChainConfig getDefaultChainConfig(String chainName) {
        ChainConfig config = new ChainConfig();
        
        // 根据链名称设置默认配置
        switch (chainName.toLowerCase()) {
            case "bsc":
                config.setName("BSC Testnet");
                config.setSymbol("BNB");
                config.setMainnet(false);
                config.setConfirmations(3);
                config.setConnectionPoolSize(15);
                break;
            case "conflux":
                config.setName("Conflux Testnet");
                config.setSymbol("CFX");
                config.setMainnet(false);
                config.setConfirmations(12);
                config.setConnectionPoolSize(10);
                break;
            case "polygon":
                config.setName("Polygon Mumbai");
                config.setSymbol("MATIC");
                config.setMainnet(false);
                config.setConfirmations(5);
                config.setConnectionPoolSize(12);
                break;
            case "ethereum":
                config.setName("Ethereum Testnet");
                config.setSymbol("ETH");
                config.setMainnet(false);
                config.setConfirmations(12);
                config.setConnectionPoolSize(10);
                break;
            default:
                // 通用默认配置
                config.setName(chainName + " Network");
                config.setSymbol(chainName.toUpperCase());
                config.setMainnet(false);
                config.setConfirmations(12);
                config.setConnectionPoolSize(10);
        }
        
        return config;
    }
    
    /**
     * 获取或创建链配置
     * 如果链配置不存在，返回默认配置
     */
    public ChainConfig getOrCreateChainConfig(String chainName) {
        ChainConfig config = chains.get(chainName);
        if (config == null) {
            config = getDefaultChainConfig(chainName);
            chains.put(chainName, config);
        }
        return config;
    }
}
