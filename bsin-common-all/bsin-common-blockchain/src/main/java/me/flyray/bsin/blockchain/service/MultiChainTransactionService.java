package me.flyray.bsin.blockchain.service;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.extern.slf4j.Slf4j;
import me.flyray.bsin.blockchain.BsinBlockChainEngine;
import me.flyray.bsin.blockchain.utils.Java2ContractTypeParameter;
import me.flyray.bsin.blockchain.exception.*;
import me.flyray.bsin.blockchain.config.BlockchainConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.cache.annotation.Cacheable;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * 多链交易服务：方便业务实现封装 实际交互依赖 BsinBlockChainEngine
 * 支持多种区块链网络的交易操作
 * 使用现有的 bsin-common-blockchain 模块
 */
@Slf4j
@Service
public class MultiChainTransactionService {
    
    @Autowired
    private BsinBlockChainEngineFactory blockchainEngineFactory;
    
    @Autowired
    private BlockchainMetrics blockchainMetrics;
    
    @Autowired
    private BlockchainEventPublisher eventPublisher;
    
    @Autowired
    private MeterRegistry meterRegistry;
    
    @Autowired
    private BlockchainConfig blockchainConfig;
    
    // 常量定义
    private static final String TRANSFER_METHOD = "transfer";
    private static final String BALANCE_OF_METHOD = "balanceOf";
    
    // 错误消息常量
    private static final String INVALID_ADDRESS_MSG = "无效的以太坊地址";
    private static final String INVALID_AMOUNT_MSG = "转账金额必须大于0";
    private static final String INVALID_CONTRACT_MSG = "无效的合约地址";
    private static final String UNSUPPORTED_CHAIN_MSG = "不支持的区块链";
    private static final String TRANSFER_FAILED_MSG = "转账失败";
    private static final String BALANCE_QUERY_FAILED_MSG = "余额查询失败";
    private static final String TRANSACTION_QUERY_FAILED_MSG = "交易查询失败";
    
    /**
     * 获取指定链的区块链引擎
     */
    private BsinBlockChainEngine getEngine(String chainName) {
        if (!blockchainConfig.isChainEnabled(chainName)) {
            throw BlockchainException.unsupportedChain(UNSUPPORTED_CHAIN_MSG + ": " + chainName + " 未启用");
        }
        return blockchainEngineFactory.getBsinBlockChainEngineInstance(chainName);
    }
    
    /**
     * 获取链特定配置
     */
    private BlockchainConfig.ChainConfig getChainConfig(String chainName) {
        return blockchainConfig.getChainConfig(chainName);
    }
    
    /**
     * 代币转账
     */
    public String tokenTransfer(@Valid @NotNull String chainName,
                               @Valid @NotNull String fromAddress,
                               @Valid @NotNull String toAddress,
                               @Valid @NotNull String contractAddress,
                               @Valid @Positive BigInteger amount,
                               @Valid @Min(0) @Max(18) BigInteger decimals) {

        // 输入验证
        validateInputs(chainName, fromAddress, toAddress, contractAddress, amount, decimals);
        
        // 开始计时
        var timer = blockchainMetrics.startTransactionTimer(chainName);
        
        try {
            // 获取区块链引擎和配置
            BsinBlockChainEngine engine = getEngine(chainName);
            BlockchainConfig.ChainConfig config = getChainConfig(chainName);
            
            // 检查余额
            BigInteger balance = getTokenBalance(chainName, fromAddress, contractAddress, decimals);
            BigInteger transferAmount = amount.multiply(BigInteger.TEN.pow(decimals.intValue()));
            
            if (balance.compareTo(transferAmount) < 0) {
                throw BlockchainException.insufficientBalance("余额不足: 需要 " + transferAmount + ", 可用 " + balance);
            }
            
            // 构建合约参数
            Java2ContractTypeParameter inputParams = new Java2ContractTypeParameter.Builder()
                .addValue("address", List.of(toAddress))  // 接收方地址
                .addParameter()
                .addValue("uint256", List.of(transferAmount.toString()))  // 转账金额
                .addParameter()
                .build();
            
            Java2ContractTypeParameter returnType = new Java2ContractTypeParameter.Builder()
                .addValue("bool", List.of(""))  // 返回类型
                .addParameter()
                .build();
            
            // 调用合约方法 - 使用链特定配置
            Map<String, Object> result = engine.contractWrite(
                chainName,                    // 链环境
                getPrivateKey(fromAddress),   // 私钥
                config.getGasPrice(),         // 链特定 Gas 价格
                config.getGasLimit(),         // 链特定 Gas 限制
                "0",                         // 发送金额
                contractAddress,              // 合约地址
                TRANSFER_METHOD,              // 方法名
                returnType,                   // 返回类型
                inputParams,                  // 输入参数
                blockchainConfig.getDefaultTimeout()  // 超时时间
            );
            
            if (result != null && result.containsKey("transactionHash")) {
                String txHash = (String) result.get("transactionHash");
                
                // 记录成功指标
                blockchainMetrics.recordTransactionSuccess(chainName, contractAddress);
                
                // 发布交易确认事件
                eventPublisher.publishTransactionConfirmed(chainName, txHash, fromAddress, 
                        toAddress, amount, 0L, contractAddress);
                
                log.info("代币转账成功: chain={}, txHash={}, amount={}", chainName, txHash, amount);
                return txHash;
            } else {
                throw BlockchainException.transactionFailed(TRANSFER_FAILED_MSG + ": " + result);
            }
            
        } catch (BlockchainException e) {
            // 记录失败指标
            blockchainMetrics.recordTransactionFailure(chainName, e.getCode());
            throw e;
        } catch (Exception e) {
            log.error("{} 链代币转账失败", chainName, e);
            blockchainMetrics.recordTransactionFailure(chainName, "UNKNOWN_ERROR");
            throw BlockchainException.transactionFailed(TRANSFER_FAILED_MSG + ": " + e.getMessage(), e);
        } finally {
            // 记录交易时间
            timer.stop(Timer.builder("blockchain.transaction.duration")
                .tag("chain", chainName)
                .register(meterRegistry));
        }
    }
    
    /**
     * 获取私钥（从钱包账户或MPC服务）
     */
    private String getPrivateKey(String fromAddress) {
        // 这里应该从安全的存储中获取私钥，或者使用MPC服务
        // 暂时返回一个占位符，实际实现需要根据业务需求
        return "private_key_placeholder";
    }
    
    /**
     * 获取代币余额
     */
    private BigInteger getTokenBalance(String chainName, String address, String contractAddress, BigInteger decimals) {
        try {
            BsinBlockChainEngine engine = getEngine(chainName);
            
            Java2ContractTypeParameter inputParams = new Java2ContractTypeParameter.Builder()
                .addValue("address", List.of(address))
                .addParameter()
                .build();
            
            Java2ContractTypeParameter returnType = new Java2ContractTypeParameter.Builder()
                .addValue("uint256", List.of(""))
                .addParameter()
                .build();
            
            Map<String, Object> result = engine.contractRead(
                chainName,
                contractAddress,
                BALANCE_OF_METHOD,
                returnType,
                inputParams,
                blockchainConfig.getDefaultTimeout()
            );
            
            if (result != null && result.containsKey("result")) {
                String balanceStr = (String) result.get("result");
                return new BigInteger(balanceStr);
            }
            
            return BigInteger.ZERO;
        } catch (Exception e) {
            log.error("获取代币余额失败: chain={}, address={}, contract={}", chainName, address, contractAddress, e);
            throw BlockchainException.transactionFailed(BALANCE_QUERY_FAILED_MSG + ": " + e.getMessage(), e);
        }
    }
    
    /**
     * 原生代币转账
     */
    public String nativeTokenTransfer(String chainName, String fromAddress, String toAddress, BigInteger amount) {
        // 输入验证
        validateNativeTransferInputs(chainName, fromAddress, toAddress, amount);
        
        // 开始计时
        var timer = blockchainMetrics.startTransactionTimer(chainName);
        
        try {
            // 获取区块链引擎和配置
            BsinBlockChainEngine engine = getEngine(chainName);
            BlockchainConfig.ChainConfig config = getChainConfig(chainName);
            
            // 检查余额
            BigInteger balance = getNativeBalance(chainName, fromAddress);
            BigInteger gasFee = new BigInteger(config.getGasPrice()).multiply(new BigInteger(config.getNativeGasLimit()));
            BigInteger totalRequired = amount.add(gasFee);
            
            if (balance.compareTo(totalRequired) < 0) {
                throw BlockchainException.insufficientBalance("余额不足: 需要 " + totalRequired + ", 可用 " + balance);
            }
            
            // 调用原生转账 - 使用链特定配置
            Map<String, Object> result = engine.transfer(
                chainName,                    // 链环境
                getPrivateKey(fromAddress),   // 私钥
                config.getGasPrice(),         // 链特定 Gas 价格
                config.getNativeGasLimit(),   // 链特定 Gas 限制
                amount.toString(),            // 转账金额
                toAddress,                    // 接收方地址
                blockchainConfig.getDefaultTimeout()  // 超时时间
            );
            
            if (result != null && result.containsKey("transactionHash")) {
                String txHash = (String) result.get("transactionHash");
                
                // 记录成功指标
                blockchainMetrics.recordTransactionSuccess(chainName, "native");
                
                // 记录 Gas 费用
                blockchainMetrics.recordGasFee(chainName, gasFee.toString());
                
                // 发布交易确认事件
                eventPublisher.publishTransactionConfirmed(chainName, txHash, fromAddress, 
                        toAddress, amount, 0L, null);
                
                log.info("原生代币转账成功: chain={}, txHash={}, amount={}", chainName, txHash, amount);
                return txHash;
            } else {
                throw BlockchainException.transactionFailed(TRANSFER_FAILED_MSG + ": " + result);
            }
            
        } catch (BlockchainException e) {
            // 记录失败指标
            blockchainMetrics.recordTransactionFailure(chainName, e.getCode());
            throw e;
        } catch (Exception e) {
            log.error("{} 链原生代币转账失败", chainName, e);
            blockchainMetrics.recordTransactionFailure(chainName, "UNKNOWN_ERROR");
            throw BlockchainException.transactionFailed(TRANSFER_FAILED_MSG + ": " + e.getMessage(), e);
        } finally {
            // 记录交易时间
            timer.stop(Timer.builder("blockchain.transaction.duration")
                .tag("chain", chainName)
                .register(meterRegistry));
        }
    }
    
    /**
     * 获取原生代币余额
     */
    private BigInteger getNativeBalance(String chainName, String address) {
        try {
            BsinBlockChainEngine engine = getEngine(chainName);
            String balanceStr = engine.getBalance(chainName, address);
            return new BigInteger(balanceStr);
        } catch (Exception e) {
            log.error("获取原生代币余额失败: chain={}, address={}", chainName, address, e);
            throw BlockchainException.transactionFailed(BALANCE_QUERY_FAILED_MSG + ": " + e.getMessage(), e);
        }
    }
    
    /**
     * 获取账户余额
     */
    @Cacheable(value = "blockchain_balance", key = "#chainName + '_' + #address")
    public String getBalance(String chainName, String address) {
        // 输入验证
        if (!isValidEthereumAddress(address)) {
            throw BlockchainException.invalidAddress(INVALID_ADDRESS_MSG);
        }
        
        try {
            BsinBlockChainEngine engine = getEngine(chainName);
            String balance = engine.getBalance(chainName, address);
            
            // 记录余额指标（暂时注释，因为方法不存在）
            // blockchainMetrics.recordBalance(chainName, address, new BigInteger(balance));
            
            return balance;
        } catch (Exception e) {
            log.error("获取账户余额失败: chain={}, address={}", chainName, address, e);
            throw BlockchainException.transactionFailed(BALANCE_QUERY_FAILED_MSG + ": " + e.getMessage(), e);
        }
    }
    
    /**
     * 获取交易信息
     */
    public Map<String, Object> getTransaction(String chainName, String txHash) {
        // 输入验证
        if (txHash == null || txHash.trim().isEmpty()) {
            throw new IllegalArgumentException("交易哈希不能为空");
        }
        
        try {
            BsinBlockChainEngine engine = getEngine(chainName);
            return engine.getTransaction(chainName, txHash, blockchainConfig.getDefaultTimeout());
        } catch (Exception e) {
            log.error("获取交易信息失败: chain={}, txHash={}", chainName, txHash, e);
            throw BlockchainException.transactionFailed(TRANSACTION_QUERY_FAILED_MSG + ": " + e.getMessage(), e);
        }
    }
    
    /**
     * 批量代币转账
     */
    public CompletableFuture<List<String>> batchTokenTransfer(String chainName, List<TransferRequest> requests) {
        List<CompletableFuture<String>> futures = requests.stream()
            .map(request -> CompletableFuture.supplyAsync(() -> 
                tokenTransfer(chainName, request.getFromAddress(), request.getToAddress(), 
                    request.getContractAddress(), request.getAmount(), request.getDecimals())))
            .collect(java.util.stream.Collectors.toList());
        
        return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
            .thenApply(v -> futures.stream()
                .map(CompletableFuture::join)
                .collect(java.util.stream.Collectors.toList()));
    }
    
    /**
     * 获取代币余额（公开方法）
     */
    public BigInteger getTokenBalancePublic(String chainName, String address, String contractAddress, BigInteger decimals) {
        // 输入验证
        validateInputs(chainName, address, address, contractAddress, BigInteger.ONE, decimals);
        
        return getTokenBalance(chainName, address, contractAddress, decimals);
    }
    
    /**
     * 估算 Gas 费用
     */
    public BigInteger estimateGas(String chainName, String from, String to, BigInteger value, String data) {
        try {
            // 这里需要根据具体的引擎实现来估算 Gas
            // 暂时返回链特定配置的默认值
            BlockchainConfig.ChainConfig config = getChainConfig(chainName);
            return new BigInteger(config.getGasLimit());
        } catch (Exception e) {
            log.error("估算 Gas 失败: chain={}, from={}, to={}", chainName, from, to, e);
            throw BlockchainException.transactionFailed("估算 Gas 失败: " + e.getMessage(), e);
        }
    }
    
    /**
     * 输入验证
     */
    private void validateInputs(String chainName, String fromAddress, String toAddress,
                               String contractAddress, BigInteger amount, BigInteger decimals) {
        
        // 地址格式验证
        if (!isValidEthereumAddress(fromAddress) || !isValidEthereumAddress(toAddress)) {
            throw BlockchainException.invalidAddress(INVALID_ADDRESS_MSG);
        }
        
        // 金额验证
        if (amount.compareTo(BigInteger.ZERO) <= 0) {
            throw BlockchainException.invalidAmount(INVALID_AMOUNT_MSG);
        }
        
        // 合约地址验证
        if (!isValidContractAddress(contractAddress)) {
            throw BlockchainException.invalidContract(INVALID_CONTRACT_MSG);
        }
        
        // 链支持验证
        if (!blockchainConfig.isChainEnabled(chainName)) {
            throw BlockchainException.unsupportedChain(UNSUPPORTED_CHAIN_MSG + ": " + chainName);
        }
    }
    
    /**
     * 原生转账输入验证
     */
    private void validateNativeTransferInputs(String chainName, String fromAddress, String toAddress, BigInteger amount) {
        // 地址格式验证
        if (!isValidEthereumAddress(fromAddress) || !isValidEthereumAddress(toAddress)) {
            throw BlockchainException.invalidAddress(INVALID_ADDRESS_MSG);
        }
        
        // 金额验证
        if (amount.compareTo(BigInteger.ZERO) <= 0) {
            throw BlockchainException.invalidAmount(INVALID_AMOUNT_MSG);
        }
        
        // 链支持验证
        if (!blockchainConfig.isChainEnabled(chainName)) {
            throw BlockchainException.unsupportedChain(UNSUPPORTED_CHAIN_MSG + ": " + chainName);
        }
    }
    
    /**
     * 验证以太坊地址格式
     */
    private boolean isValidEthereumAddress(String address) {
        return address != null && address.matches("^0x[a-fA-F0-9]{40}$");
    }
    
    /**
     * 验证合约地址
     */
    private boolean isValidContractAddress(String contractAddress) {
        return isValidEthereumAddress(contractAddress);
    }
    
    /**
     * 获取链信息
     */
    public Map<String, Object> getChainInfo(String chainName) {
        try {
            BsinBlockChainEngine engine = getEngine(chainName);
            BlockchainConfig.ChainConfig config = getChainConfig(chainName);
            
            Map<String, Object> chainInfo = new java.util.HashMap<>();
            chainInfo.put("chainName", chainName);
            chainInfo.put("chainId", engine.getChainId(chainName));
            chainInfo.put("blockHeight", engine.getBlockHeight(chainName));
            chainInfo.put("clientVersion", engine.getWeb3ClientVersion(chainName));
            chainInfo.put("gasPrice", config.getGasPrice());
            chainInfo.put("gasLimit", config.getGasLimit());
            chainInfo.put("confirmationBlocks", config.getConfirmationBlocks());
            chainInfo.put("enabled", config.isEnabled());
            chainInfo.put("rpcUrl", config.getRpcUrl());
            chainInfo.put("explorerUrl", config.getExplorerUrl());
            
            return chainInfo;
        } catch (Exception e) {
            log.error("获取链信息失败: chain={}", chainName, e);
            throw BlockchainException.transactionFailed("获取链信息失败: " + e.getMessage(), e);
        }
    }
    
    /**
     * 获取链的 nonce 值
     */
    public BigInteger getNonce(String chainName, String address) {
        try {
            BsinBlockChainEngine engine = getEngine(chainName);
            return engine.getNonce(chainName, address);
        } catch (Exception e) {
            log.error("获取 nonce 失败: chain={}, address={}", chainName, address, e);
            throw BlockchainException.transactionFailed("获取 nonce 失败: " + e.getMessage(), e);
        }
    }
    
    /**
     * 创建钱包
     */
    public Map<String, Object> createWallet(String chainName, String password) {
        try {
            BsinBlockChainEngine engine = getEngine(chainName);
            return engine.createWallet(password, chainName);
        } catch (Exception e) {
            log.error("创建钱包失败: chain={}", chainName, e);
            throw BlockchainException.transactionFailed("创建钱包失败: " + e.getMessage(), e);
        }
    }
    
    /**
     * 根据私钥获取地址
     */
    public String getAddressFromPrivateKey(String chainName, String privateKey) {
        try {
            BsinBlockChainEngine engine = getEngine(chainName);
            return engine.getAddress(chainName, privateKey);
        } catch (Exception e) {
            log.error("根据私钥获取地址失败: chain={}", chainName, e);
            throw BlockchainException.transactionFailed("根据私钥获取地址失败: " + e.getMessage(), e);
        }
    }
    
    /**
     * 转账请求类
     */
    public static class TransferRequest {
        private String fromAddress;
        private String toAddress;
        private String contractAddress;
        private BigInteger amount;
        private BigInteger decimals;
        
        // 构造函数
        public TransferRequest() {}
        
        public TransferRequest(String fromAddress, String toAddress, String contractAddress, 
                             BigInteger amount, BigInteger decimals) {
            this.fromAddress = fromAddress;
            this.toAddress = toAddress;
            this.contractAddress = contractAddress;
            this.amount = amount;
            this.decimals = decimals;
        }
        
        // Getter 和 Setter 方法
        public String getFromAddress() { return fromAddress; }
        public void setFromAddress(String fromAddress) { this.fromAddress = fromAddress; }
        
        public String getToAddress() { return toAddress; }
        public void setToAddress(String toAddress) { this.toAddress = toAddress; }
        
        public String getContractAddress() { return contractAddress; }
        public void setContractAddress(String contractAddress) { this.contractAddress = contractAddress; }
        
        public BigInteger getAmount() { return amount; }
        public void setAmount(BigInteger amount) { this.amount = amount; }
        
        public BigInteger getDecimals() { return decimals; }
        public void setDecimals(BigInteger decimals) { this.decimals = decimals; }
    }
}
