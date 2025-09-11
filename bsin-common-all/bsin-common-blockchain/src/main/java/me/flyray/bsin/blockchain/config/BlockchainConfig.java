package me.flyray.bsin.blockchain.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

/**
 * 区块链配置管理
 * 支持不同链的特定配置
 */
@Data
@Component
@ConfigurationProperties(prefix = "bsin.blockchain")
public class BlockchainConfig {
    
    private Map<String, ChainConfig> chains = new HashMap<>();
    private int defaultTimeout = 30000;
    private int maxRetries = 3;
    private boolean enableMetrics = true;
    private boolean enableEventPublishing = true;
    
    @Data
    public static class ChainConfig {
        private String rpcUrl;
        private String wsUrl;
        private String gasPrice;
        private String gasLimit;
        private String nativeGasLimit;
        private int confirmationBlocks;
        private boolean enabled = true;
        private Map<String, String> customParams = new HashMap<>();
        
        // 链特定参数
        private String chainId;
        private String symbol;
        private int decimals;
        private String explorerUrl;
        private boolean supportEIP1559 = false;
        private String maxFeePerGas;
        private String maxPriorityFeePerGas;
    }
    
    /**
     * 获取指定链的配置
     */
    public ChainConfig getChainConfig(String chainName) {
        return chains.getOrDefault(chainName, new ChainConfig());
    }
    
    /**
     * 检查链是否启用
     */
    public boolean isChainEnabled(String chainName) {
        ChainConfig config = chains.get(chainName);
        return config != null && config.isEnabled();
    }
    
    /**
     * 获取链的 Gas 价格
     */
    public String getGasPrice(String chainName) {
        ChainConfig config = getChainConfig(chainName);
        return config.getGasPrice() != null ? config.getGasPrice() : "20000000000";
    }
    
    /**
     * 获取链的 Gas 限制
     */
    public String getGasLimit(String chainName) {
        ChainConfig config = getChainConfig(chainName);
        return config.getGasLimit() != null ? config.getGasLimit() : "100000";
    }
    
    /**
     * 获取链的原生 Gas 限制
     */
    public String getNativeGasLimit(String chainName) {
        ChainConfig config = getChainConfig(chainName);
        return config.getNativeGasLimit() != null ? config.getNativeGasLimit() : "21000";
    }
    
    /**
     * 获取链的确认块数
     */
    public int getConfirmationBlocks(String chainName) {
        ChainConfig config = getChainConfig(chainName);
        return config.getConfirmationBlocks() > 0 ? config.getConfirmationBlocks() : 1;
    }
}
