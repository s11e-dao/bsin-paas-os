package me.flyray.bsin.blockchain.service;

import lombok.extern.slf4j.Slf4j;
import me.flyray.bsin.blockchain.config.BlockchainProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.response.EthBlock;
import org.web3j.protocol.core.methods.response.EthGasPrice;
import org.web3j.utils.Convert;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 智能Gas费管理服务
 * 提供动态Gas价格估算和自动加油功能
 */
@Slf4j
@Service
public class SmartGasFeeService {

    @Autowired
    private BlockchainProperties blockchainProperties;

    // Gas价格缓存，避免频繁查询
    private final ConcurrentHashMap<String, GasPriceCache> gasPriceCache = new ConcurrentHashMap<>();
    
    // 缓存有效期（秒）
    private static final long CACHE_EXPIRE_SECONDS = 30;

    /**
     * Gas价格信息
     */
    public static class GasPriceInfo {
        public final BigInteger gasPrice;           // 传统Gas价格
        public final BigInteger maxPriorityFeePerGas; // EIP-1559 优先费
        public final BigInteger maxFeePerGas;       // EIP-1559 最大费用
        public final BigInteger gasLimit;           // Gas限制
        public final boolean isEIP1559;            // 是否支持EIP-1559

        public GasPriceInfo(BigInteger gasPrice, BigInteger maxPriorityFeePerGas, 
                          BigInteger maxFeePerGas, BigInteger gasLimit, boolean isEIP1559) {
            this.gasPrice = gasPrice;
            this.maxPriorityFeePerGas = maxPriorityFeePerGas;
            this.maxFeePerGas = maxFeePerGas;
            this.gasLimit = gasLimit;
            this.isEIP1559 = isEIP1559;
        }
    }

    /**
     * Gas价格缓存
     */
    private static class GasPriceCache {
        public final GasPriceInfo gasPriceInfo;
        public final LocalDateTime timestamp;

        public GasPriceCache(GasPriceInfo gasPriceInfo) {
            this.gasPriceInfo = gasPriceInfo;
            this.timestamp = LocalDateTime.now();
        }

        public boolean isExpired() {
            return ChronoUnit.SECONDS.between(timestamp, LocalDateTime.now()) > CACHE_EXPIRE_SECONDS;
        }
    }

    /**
     * 获取智能Gas价格
     * 
     * @param chainName 链名称
     * @param web3j Web3j实例
     * @param transactionType 交易类型（fast/normal/slow）
     * @return Gas价格信息
     */
    public GasPriceInfo getSmartGasPrice(String chainName, Web3j web3j, String transactionType) {
        try {
            String cacheKey = chainName + "_" + transactionType;
            GasPriceCache cached = gasPriceCache.get(cacheKey);
            
            // 检查缓存是否有效
            if (cached != null && !cached.isExpired()) {
                log.debug("使用缓存的Gas价格: chain={}, type={}", chainName, transactionType);
                return cached.gasPriceInfo;
            }

            // 获取实时Gas价格
            GasPriceInfo gasPriceInfo = calculateDynamicGasPrice(web3j, transactionType);
            
            // 更新缓存
            gasPriceCache.put(cacheKey, new GasPriceCache(gasPriceInfo));
            
            log.info("获取智能Gas价格: chain={}, type={}, gasPrice={}, maxFeePerGas={}", 
                    chainName, transactionType, gasPriceInfo.gasPrice, gasPriceInfo.maxFeePerGas);
            
            return gasPriceInfo;
            
        } catch (Exception e) {
            log.error("获取智能Gas价格失败: chain={}, type={}", chainName, transactionType, e);
            return getFallbackGasPrice(chainName, transactionType);
        }
    }

    /**
     * 计算动态Gas价格
     */
    private GasPriceInfo calculateDynamicGasPrice(Web3j web3j, String transactionType) throws Exception {
        // 1. 获取当前区块信息
        EthBlock.Block latestBlock = web3j.ethGetBlockByNumber(DefaultBlockParameterName.LATEST, false).send().getBlock();
        
        // 2. 获取当前Gas价格
        EthGasPrice ethGasPrice = web3j.ethGasPrice().send();
        BigInteger currentGasPrice = ethGasPrice.getGasPrice();

        // 3. 判断是否支持EIP-1559
        boolean isEIP1559 = latestBlock.getBaseFeePerGas() != null;
        
        if (isEIP1559) {
            return calculateEIP1559GasPrice(latestBlock, currentGasPrice, transactionType);
        } else {
            return calculateLegacyGasPrice(currentGasPrice, transactionType);
        }
    }

    /**
     * 计算EIP-1559 Gas价格
     */
    private GasPriceInfo calculateEIP1559GasPrice(EthBlock.Block latestBlock, BigInteger currentGasPrice, String transactionType) {
        BigInteger baseFeePerGas = latestBlock.getBaseFeePerGas();
        
        // 根据交易类型设置不同的优先费
        BigInteger maxPriorityFeePerGas;
        BigDecimal multiplier;
        
        switch (transactionType.toLowerCase()) {
            case "fast":
                maxPriorityFeePerGas = Convert.toWei("50", Convert.Unit.GWEI).toBigInteger(); // 50 Gwei
                multiplier = new BigDecimal("2.5"); // 2.5倍基础费用
                break;
            case "slow":
                maxPriorityFeePerGas = Convert.toWei("10", Convert.Unit.GWEI).toBigInteger(); // 10 Gwei
                multiplier = new BigDecimal("1.2"); // 1.2倍基础费用
                break;
            default: // normal
                maxPriorityFeePerGas = Convert.toWei("30", Convert.Unit.GWEI).toBigInteger(); // 30 Gwei
                multiplier = new BigDecimal("2.0"); // 2.0倍基础费用
                break;
        }
        
        // 计算最大费用
        BigDecimal maxFeePerGas = new BigDecimal(baseFeePerGas)
                .multiply(multiplier)
                .add(new BigDecimal(maxPriorityFeePerGas));
        
        // 设置Gas限制
        BigInteger gasLimit = getGasLimitForTransactionType(transactionType);
        
        return new GasPriceInfo(
                currentGasPrice, // 传统Gas价格（向后兼容）
                maxPriorityFeePerGas,
                maxFeePerGas.toBigInteger(),
                gasLimit,
                true
        );
    }

    /**
     * 计算传统Gas价格
     */
    private GasPriceInfo calculateLegacyGasPrice(BigInteger currentGasPrice, String transactionType) {
        BigDecimal multiplier;
        
        switch (transactionType.toLowerCase()) {
            case "fast":
                multiplier = new BigDecimal("1.5"); // 1.5倍
                break;
            case "slow":
                multiplier = new BigDecimal("0.8"); // 0.8倍
                break;
            default: // normal
                multiplier = new BigDecimal("1.2"); // 1.2倍
                break;
        }
        
        BigInteger adjustedGasPrice = new BigDecimal(currentGasPrice)
                .multiply(multiplier)
                .toBigInteger();
        
        BigInteger gasLimit = getGasLimitForTransactionType(transactionType);
        
        return new GasPriceInfo(
                adjustedGasPrice,
                BigInteger.ZERO, // 传统模式不支持优先费
                BigInteger.ZERO, // 传统模式不支持最大费用
                gasLimit,
                false
        );
    }

    /**
     * 根据交易类型获取Gas限制
     */
    private BigInteger getGasLimitForTransactionType(String transactionType) {
        switch (transactionType.toLowerCase()) {
            case "fast":
                return BigInteger.valueOf(300000); // 30万
            case "slow":
                return BigInteger.valueOf(200000); // 20万
            default: // normal
                return BigInteger.valueOf(250000); // 25万
        }
    }

    /**
     * 获取备用Gas价格（当实时获取失败时）
     */
    private GasPriceInfo getFallbackGasPrice(String chainName, String transactionType) {
        log.warn("使用备用Gas价格: chain={}, type={}", chainName, transactionType);
        
        // 根据链配置获取默认值
        blockchainProperties.getChainConfig(chainName);
        
        BigInteger defaultGasPrice = BigInteger.valueOf(20_000_000_000L); // 20 Gwei
        BigInteger gasLimit = getGasLimitForTransactionType(transactionType);
        
        return new GasPriceInfo(
                defaultGasPrice,
                BigInteger.ZERO,
                BigInteger.ZERO,
                gasLimit,
                false
        );
    }

    /**
     * 自动加油功能 - 在交易失败时自动增加Gas价格重试
     * 
     * @param chainName 链名称
     * @param web3j Web3j实例
     * @param originalGasPrice 原始Gas价格
     * @param retryCount 重试次数
     * @return 调整后的Gas价格
     */
    public BigInteger getBoostedGasPrice(String chainName, Web3j web3j, BigInteger originalGasPrice, int retryCount) {
        try {
            // 获取当前Gas价格作为参考
            GasPriceInfo currentGasPrice = getSmartGasPrice(chainName, web3j, "fast");
            BigInteger currentPrice = currentGasPrice.isEIP1559 ? currentGasPrice.maxFeePerGas : currentGasPrice.gasPrice;
            
            // 计算调整倍数：重试次数越多，倍数越高
            BigDecimal boostMultiplier = new BigDecimal("1.0")
                    .add(new BigDecimal(retryCount).multiply(new BigDecimal("0.2"))); // 每次重试增加20%
            
            // 使用当前价格和原始价格中的较大值作为基础
            BigInteger basePrice = currentPrice.max(originalGasPrice);
            
            BigInteger boostedPrice = new BigDecimal(basePrice)
                    .multiply(boostMultiplier)
                    .toBigInteger();
            
            log.info("自动加油: chain={}, retryCount={}, originalPrice={}, boostedPrice={}", 
                    chainName, retryCount, originalGasPrice, boostedPrice);
            
            return boostedPrice;
            
        } catch (Exception e) {
            log.error("自动加油失败，使用原始价格: chain={}, retryCount={}", chainName, retryCount, e);
            
            // 失败时简单增加20%
            return new BigDecimal(originalGasPrice)
                    .multiply(new BigDecimal("1.2"))
                    .toBigInteger();
        }
    }

    /**
     * 清理过期缓存
     */
    public void cleanExpiredCache() {
        gasPriceCache.entrySet().removeIf(entry -> entry.getValue().isExpired());
        log.debug("清理过期Gas价格缓存，剩余缓存数量: {}", gasPriceCache.size());
    }

    /**
     * 获取缓存统计信息
     */
    public String getCacheStats() {
        return String.format("Gas价格缓存统计: 总数=%d, 活跃=%d", 
                gasPriceCache.size(),
                gasPriceCache.values().stream().mapToLong(cache -> cache.isExpired() ? 0 : 1).sum());
    }
}
