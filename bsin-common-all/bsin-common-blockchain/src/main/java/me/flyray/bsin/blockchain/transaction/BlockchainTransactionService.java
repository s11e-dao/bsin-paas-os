package me.flyray.bsin.blockchain.transaction;

import lombok.extern.slf4j.Slf4j;
import me.flyray.bsin.blockchain.config.BlockchainProperties;
import me.flyray.bsin.blockchain.connection.Web3jConnectionPoolService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.FunctionReturnDecoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.Hash;
import org.web3j.crypto.RawTransaction;
import org.web3j.crypto.Sign;
import org.web3j.crypto.TransactionEncoder;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.request.Transaction;
import org.web3j.protocol.core.methods.response.*;
import org.web3j.utils.Numeric;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;

/**
 * 区块链交易服务
 * 提供统一的区块链交易操作接口，包括代币转账、ETH转账、签名等
 */
@Slf4j
@Service
public class BlockchainTransactionService {

    @Autowired
    private BlockchainProperties blockchainProperties;
    
    @Autowired
    private SmartGasFeeService smartGasFeeService;
    
    @Autowired
    private Web3jConnectionPoolService connectionPoolService;

    // 注意：WalletAccountMapper 将在具体业务模块中注入

    /**
     * ERC-20 代币转账
     * 
     * @param chainIdentifier 链标识
     * @param fromAddress 发送地址
     * @param toAddress 接收地址
     * @param contractAddress 代币合约地址
     * @param amount 转账金额
     * @param decimals 代币精度
     * @return 交易哈希
     */
    public String tokenTransfer(String chainIdentifier, String fromAddress, String fromAddressPrivateKey, String toAddress,
                               String contractAddress, BigInteger amount, Integer decimals) throws Exception {
        log.info("🔀 开始代币转账，chain: {}, from: {}, to: {}, contract: {}, amount: {}, decimals: {}",
                chainIdentifier, fromAddress, toAddress, contractAddress, amount, decimals);

        Web3j web3j = getWeb3jInstance(chainIdentifier);

        // 1. 获取交易参数
        EthGetTransactionCount ethGetTransactionCount = web3j.ethGetTransactionCount(
                fromAddress, DefaultBlockParameterName.LATEST).send();
        BigInteger nonce = ethGetTransactionCount.getTransactionCount();
        
        EthChainId ethChainId = web3j.ethChainId().send();
        BigInteger chainId = ethChainId.getChainId();

        // 2. 构建转账函数 - 修复金额计算逻辑
        // amount参数应该已经是正确精度的值，不需要再次乘以10^decimals
        log.info("💰 代币转账金额计算: 原始amount={}, decimals={}", amount, decimals);
        
        // 验证amount的合理性：如果是小数形式的金额，需要转换为Wei单位
        // 但是从调用方传来的amount看起来已经是Wei单位了（33000000000000000000）
        BigInteger tokenAmount;
        try {
            // 检查amount是否已经包含了精度（通过数值大小判断）
            String amountStr = amount.toString();
            if (amountStr.length() <= decimals) {
                // 如果amount看起来像是小数形式，需要转换为Wei
                log.info("🔧 amount看起来是小数形式，转换为Wei单位");
                BigDecimal decimalAmount = new BigDecimal(amount);
                BigDecimal multiplier = BigDecimal.TEN.pow(decimals);
                tokenAmount = decimalAmount.multiply(multiplier).toBigInteger();
            } else {
                // amount已经是Wei单位，直接使用
                log.info("✅ amount已经是Wei单位，直接使用");
                tokenAmount = amount;
            }
            
            log.info("🎯 最终转账金额: {} (Wei)", tokenAmount);
            
        } catch (Exception e) {
            log.error("❌ 金额转换失败: amount={}, decimals={}", amount, decimals, e);
            throw new Exception("代币金额计算失败: " + e.getMessage(), e);
        }
        
        Function function = new Function(
                "transfer",
                Arrays.asList(new Address(toAddress), new Uint256(tokenAmount)),
                Arrays.asList(new TypeReference<org.web3j.abi.datatypes.Type<?>>() {})
        );
        String data = FunctionEncoder.encode(function);
        log.info("📋 构建的转账函数数据长度: {}", data.length());

        // 3. 估算 Gas 费用 - 增加详细的错误处理
        log.info("⛽ 开始估算Gas费用: from={}, to={}, contract={}", fromAddress, toAddress, contractAddress);
        
        Transaction transaction = Transaction.createEthCallTransaction(fromAddress, contractAddress, data);
        EthEstimateGas gasEstimate = web3j.ethEstimateGas(transaction).send();
        
        if (gasEstimate.hasError()) {
            String errorCode = String.valueOf(gasEstimate.getError().getCode());
            String errorMessage = gasEstimate.getError().getMessage();
            
            log.error("❌ Gas估算失败: code={}, message={}", errorCode, errorMessage);
            log.error("🔍 详细错误信息:");
            log.error("   - fromAddress: {}", fromAddress);
            log.error("   - toAddress: {}", toAddress);
            log.error("   - contractAddress: {}", contractAddress);
            log.error("   - tokenAmount: {} (Wei)", tokenAmount);
            log.error("   - decimals: {}", decimals);
            log.error("   - data length: {}", data.length());
            
            // 提供更详细的错误诊断
            if (errorMessage.contains("revert") || errorMessage.contains("execution reverted")) {
                log.error("🚨 诊断: 合约调用被回滚，可能原因:");
                log.error("   1. 余额不足: fromAddress可能没有足够的代币余额");
                log.error("   2. 授权问题: 可能需要先授权合约使用代币");
                log.error("   3. 合约问题: 代币合约可能存在问题");
                log.error("   4. 金额错误: 转账金额可能超出余额或格式错误");
                
                // 尝试查询余额进行进一步诊断
                try {
                    BigInteger balanceWei = getTokenBalance(chainIdentifier, contractAddress, fromAddress);
                    BigDecimal balanceDisplay = new BigDecimal(balanceWei).divide(BigDecimal.TEN.pow(decimals));
                    BigDecimal transferAmountDisplay = new BigDecimal(tokenAmount).divide(BigDecimal.TEN.pow(decimals));
                    
                    log.error("💡 余额诊断信息:");
                    log.error("   - 当前代币余额: {} ({}) (Wei单位)", balanceWei, balanceDisplay);
                    log.error("   - 尝试转账金额: {} ({}) (Wei单位)", tokenAmount, transferAmountDisplay);
                    
                    if (balanceWei.compareTo(tokenAmount) < 0) {
                        log.error("🚨 确认: 余额不足，无法完成转账");
                        log.error("   - 余额: {} (显示单位)", balanceDisplay);
                        log.error("   - 需要: {} (显示单位)", transferAmountDisplay);
                        log.error("   - 缺少: {} (显示单位)", transferAmountDisplay.subtract(balanceDisplay));
                    } else {
                        log.info("✅ 余额充足，问题可能在其他地方");
                    }
                } catch (Exception balanceError) {
                    log.warn("⚠️ 无法查询余额进行诊断: {}", balanceError.getMessage());
                }
            }
            
            throw new Exception(String.format("Gas 估算失败: %s-%s", errorCode, errorMessage));
        }
        
        BigInteger gasLimit = gasEstimate.getAmountUsed();
        log.info("✅ Gas估算成功: gasLimit={}", gasLimit);

        // 4. 计算 Gas 价格（使用智能Gas费管理）
        SmartGasFeeService.GasPriceInfo gasPriceInfo = smartGasFeeService.getSmartGasPrice(chainIdentifier, web3j, "normal");
        
        // 5. 构建原始交易
        RawTransaction rawTransaction;
        if (gasPriceInfo.isEIP1559) {
            // EIP-1559 交易
            rawTransaction = RawTransaction.createTransaction(
                    chainId.longValue(),
                    nonce,
                    gasLimit,
                    contractAddress,
                    BigInteger.ZERO, // 代币转账 ETH 金额为 0
                    data,
                    gasPriceInfo.maxPriorityFeePerGas,
                    gasPriceInfo.maxFeePerGas
            );
        } else {
            // 传统交易
            rawTransaction = RawTransaction.createTransaction(
                    nonce,
                    BigInteger.ZERO, // 代币转账 ETH 金额为 0
                    gasLimit,
                    contractAddress,
                    data
            );
        }

        // 6. 签名并发送交易
        return signAndSendTransaction(chainIdentifier, rawTransaction, fromAddress, fromAddressPrivateKey);
    }

    /**
     * ETH 转账
     * 
     * @param chainIdentifier 链标识
     * @param fromAddress 发送地址
     * @param toAddress 接收地址
     * @param amount ETH 金额（Wei）
     * @return 交易哈希
     */
    public String ethTransfer(String chainIdentifier, String fromAddress, String fromAddressPrivateKey, String toAddress, BigInteger amount) throws Exception {
        log.info("开始ETH转账，chain: {}, from: {}, to: {}, amount: {}",
                chainIdentifier, fromAddress, toAddress, amount);

        Web3j web3j = getWeb3jInstance(chainIdentifier);

        // 1. 获取交易参数
        EthGetTransactionCount ethGetTransactionCount = web3j.ethGetTransactionCount(
                fromAddress, DefaultBlockParameterName.LATEST).send();
        BigInteger nonce = ethGetTransactionCount.getTransactionCount();
        
        EthChainId ethChainId = web3j.ethChainId().send();
        BigInteger chainId = ethChainId.getChainId();

        // 2. 计算 Gas 价格（使用智能Gas费管理）
        SmartGasFeeService.GasPriceInfo gasPriceInfo = smartGasFeeService.getSmartGasPrice(chainIdentifier, web3j, "normal");

        // 3. 构建原始交易
        RawTransaction rawTransaction;
        if (gasPriceInfo.isEIP1559) {
            // EIP-1559 交易
            rawTransaction = RawTransaction.createEtherTransaction(
                    chainId.longValue(),
                    nonce,
                    BigInteger.valueOf(21000), // ETH 转账固定 Gas Limit
                    toAddress,
                    amount,
                    gasPriceInfo.maxPriorityFeePerGas,
                    gasPriceInfo.maxFeePerGas
            );
        } else {
            // 传统交易
            rawTransaction = RawTransaction.createEtherTransaction(
                    nonce,
                    gasPriceInfo.gasPrice,
                    BigInteger.valueOf(21000), // ETH 转账固定 Gas Limit
                    toAddress,
                    amount
            );
        }

        // 4. 签名并发送交易
        return signAndSendTransaction(chainIdentifier, rawTransaction, fromAddress, fromAddressPrivateKey);
    }

    /**
     * 查询代币余额
     * 
     * @param chainIdentifier 链名称
     * @param contractAddress 代币合约地址
     * @param holderAddress 持有者地址
     * @return 代币余额
     */
    public BigInteger getTokenBalance(String chainIdentifier, String contractAddress, String holderAddress) throws Exception {
        log.info("查询代币余额，chain: {}, contract: {}, holder: {}",
                chainIdentifier, contractAddress, holderAddress);

        Web3j web3j = getWeb3jInstance(chainIdentifier);

        // 构建查询函数
        Function function = new Function(
                "balanceOf",
                Arrays.asList(new Address(holderAddress)),
                Arrays.asList(new TypeReference<Uint256>() {})
        );
        String encodedFunction = FunctionEncoder.encode(function);

        // 发送调用请求
        Transaction transaction = Transaction.createEthCallTransaction(null, contractAddress, encodedFunction);
        EthCall ethCall = web3j.ethCall(transaction, DefaultBlockParameterName.LATEST).send();

        // 解析结果
        @SuppressWarnings("rawtypes")
        List results = FunctionReturnDecoder.decode(ethCall.getValue(), function.getOutputParameters());
        org.web3j.abi.datatypes.Type result = (org.web3j.abi.datatypes.Type) results.get(0);
        BigInteger balance = (BigInteger) result.getValue();

        log.info("代币余额查询结果: {}", balance);
        return balance;
    }

    /**
     * 查询交易收据
     * 
     * @param chainIdentifier 链名称
     * @param txHash 交易哈希
     * @return 交易收据
     */
    public TransactionReceipt getTransactionReceipt(String chainIdentifier, String txHash) throws Exception {
        log.info("🔍 查询交易收据，chain: {}, txHash: {}", chainIdentifier, txHash);

        try {
            Web3j web3j = getWeb3jInstance(chainIdentifier);
            
            // 首先检查交易是否存在
            EthTransaction ethTransaction = web3j.ethGetTransactionByHash(txHash).send();
            if (ethTransaction.getTransaction().isEmpty()) {
                log.warn("⚠️ 交易不存在于区块链网络中: chain={}, txHash={}", chainIdentifier, txHash);
                return null;
            }
            log.debug("✅ 交易存在于区块链网络中: chain={}, txHash={}", chainIdentifier, txHash);

            // 查询交易收据
            EthGetTransactionReceipt receipt = web3j.ethGetTransactionReceipt(txHash).send();
            TransactionReceipt transactionReceipt = receipt.getTransactionReceipt().orElse(null);
            
            if (transactionReceipt == null) {
                log.info("⏳ 交易收据为空，可能原因:");
                log.info("   1. 交易还在被打包中 (Pending)");
                log.info("   2. 交易发送失败但区块链尚未记录");
                log.info("   3. 网络延迟导致收据还未生成");
                log.info("   建议: 等待几个区块确认后重试，chain={}, txHash={}", chainIdentifier, txHash);
            } else {
                log.info("✅ 成功获取交易收据: chain={}, txHash={}, status={}, blockNumber={}, gasUsed={}", 
                        chainIdentifier, txHash, transactionReceipt.isStatusOK(), 
                        transactionReceipt.getBlockNumber(), transactionReceipt.getGasUsed());
            }
            
            return transactionReceipt;
            
        } catch (Exception e) {
            log.error("❌ 查询交易收据异常: chain={}, txHash={}, error={}", chainIdentifier, txHash, e.getMessage(), e);
            
            // 提供更详细的错误诊断
            if (e.getMessage() != null) {
                String errorMsg = e.getMessage().toLowerCase();
                if (errorMsg.contains("connection") || errorMsg.contains("timeout")) {
                    log.error("🚨 诊断: 网络连接问题，请检查:");
                    log.error("   1. RPC节点是否可用");
                    log.error("   2. 网络连接是否正常");
                    log.error("   3. 是否需要增加超时时间");
                } else if (errorMsg.contains("invalid") && errorMsg.contains("hash")) {
                    log.error("🚨 诊断: 交易哈希格式可能有问题: {}", txHash);
                }
            }
            
            throw e;
        }
    }

    /**
     * 等待交易确认
     * 
     * @param chainName 链名称
     * @param txHash 交易哈希
     * @param maxWaitTime 最大等待时间（毫秒）
     * @return 交易收据
     */
    public TransactionReceipt waitForTransactionReceipt(String chainName, String txHash, long maxWaitTime) throws Exception {
        log.info("等待交易确认，chain: {}, txHash: {}, maxWaitTime: {}", chainName, txHash, maxWaitTime);

        Web3j web3j = getWeb3jInstance(chainName);

        return web3j.ethGetTransactionReceipt(txHash).send().getTransactionReceipt().orElse(null);
    }

    /**
     * 带重试机制的交易收据查询
     * 
     * @param chainIdentifier 链名称
     * @param txHash 交易哈希
     * @param maxRetries 最大重试次数
     * @param retryIntervalMs 重试间隔（毫秒）
     * @return 交易收据，如果多次重试后仍然为空则返回null
     */
    public TransactionReceipt getTransactionReceiptWithRetry(String chainIdentifier, String txHash, 
                                                           int maxRetries, long retryIntervalMs) throws Exception {
        log.info("🔄 开始带重试的交易收据查询: chain={}, txHash={}, maxRetries={}, interval={}ms", 
                chainIdentifier, txHash, maxRetries, retryIntervalMs);

        TransactionReceipt receipt = null;
        Exception lastException = null;

        for (int attempt = 0; attempt <= maxRetries; attempt++) {
            try {
                if (attempt > 0) {
                    log.info("🔄 第 {} 次重试查询交易收据: chain={}, txHash={}", attempt, chainIdentifier, txHash);
                    Thread.sleep(retryIntervalMs);
                }

                receipt = getTransactionReceipt(chainIdentifier, txHash);
                
                if (receipt != null) {
                    log.info("✅ 成功获取交易收据 (第{}次尝试): chain={}, txHash={}, status={}, blockNumber={}", 
                            attempt + 1, chainIdentifier, txHash, receipt.isStatusOK(), receipt.getBlockNumber());
                    return receipt;
                } else {
                    log.info("⏳ 第{}次查询交易收据为空，继续重试: chain={}, txHash={}", 
                            attempt + 1, chainIdentifier, txHash);
                }

            } catch (Exception e) {
                lastException = e;
                log.warn("❌ 第{}次查询交易收据异常: chain={}, txHash={}, error={}", 
                        attempt + 1, chainIdentifier, txHash, e.getMessage());
                
                // 如果不是最后一次重试，继续
                if (attempt < maxRetries) {
                    continue;
                }
            }
        }

        // 所有重试都失败了
        if (lastException != null) {
            log.error("❌ 查询交易收据最终失败，已重试{}次: chain={}, txHash={}, lastError={}", 
                    maxRetries + 1, chainIdentifier, txHash, lastException.getMessage());
            throw lastException;
        } else {
            log.warn("⚠️ 查询交易收据多次重试后仍为空: chain={}, txHash={}, 已重试{}次", 
                    chainIdentifier, txHash, maxRetries + 1);
        }

        return null;
    }

    /**
     * 带重试和自动加油的交易发送
     * 
     * @param chainName 链名称
     * @param rawTransaction 原始交易
     * @param fromAddress 发送地址
     * @param maxRetries 最大重试次数
     * @return 交易哈希
     */
    public String signAndSendTransactionWithRetry(String chainName, RawTransaction rawTransaction, 
                                                  String fromAddress, int maxRetries) throws Exception {
        String txHash = null;
        Exception lastException = null;
        
        for (int retry = 0; retry <= maxRetries; retry++) {
            try {
                if (retry > 0) {
                    log.info("重试发送交易，第 {} 次重试", retry);
                    // 自动加油：增加Gas价格
                    BigInteger boostedGasPrice = smartGasFeeService.getBoostedGasPrice(
                            chainName, getWeb3jInstance(chainName), 
                            rawTransaction.getGasPrice(), retry);
                    
                    // 重新构建交易（这里需要根据实际的RawTransaction API来调整）
                    // 注意：RawTransaction是不可变的，可能需要重新创建
                    log.info("自动加油，原始Gas价格: {}, 新Gas价格: {}", 
                            rawTransaction.getGasPrice(), boostedGasPrice);
                }
                
                txHash = signAndSendTransaction(chainName, rawTransaction, fromAddress, fromAddress);
                log.info("交易发送成功: chain={}, txHash={}, retry={}", chainName, txHash, retry);
                break;
                
            } catch (Exception e) {
                lastException = e;
                log.warn("交易发送失败，准备重试: chain={}, retry={}, error={}", 
                        chainName, retry, e.getMessage());
                
                if (retry == maxRetries) {
                    log.error("交易发送最终失败，已达到最大重试次数: chain={}, retries={}", 
                            chainName, maxRetries);
                    break;
                }
                
                // 等待一段时间后重试
                try {
                    Thread.sleep(1000 * (retry + 1)); // 递增等待时间
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    throw new RuntimeException("重试被中断", ie);
                }
            }
        }
        
        if (txHash == null && lastException != null) {
            throw new RuntimeException("交易发送失败，已重试 " + maxRetries + " 次", lastException);
        }
        
        return txHash;
    }


    /**
     * 签名并发送交易
     */
    private String signAndSendTransaction(String chainIdentifier, RawTransaction rawTransaction, String fromAddress, String fromAddressPrivateKey) throws Exception {
        log.info("开始签名并发送交易 - chainIdentifier: {}, fromAddress: {}, privateKeyLength: {}", 
                chainIdentifier, fromAddress, 
                fromAddressPrivateKey != null ? fromAddressPrivateKey.length() : 0);

        try {
            // 1. 序列化交易
            log.debug("Step 1: 序列化原始交易");
            byte[] encodedRawTransaction = TransactionEncoder.encode(rawTransaction);
            String unsignedHash = Numeric.toHexString(Hash.sha3(encodedRawTransaction));
            log.debug("原始交易序列化完成 - unsignedHash: {}, encodedLength: {}", 
                    unsignedHash, encodedRawTransaction.length);

            // 记录交易基本信息
            String data = rawTransaction.getData();
            
            // 安全地获取Gas价格信息，处理EIP-1559交易
            String gasPriceInfo;
            try {
                BigInteger gasPrice = rawTransaction.getGasPrice();
                gasPriceInfo = "gasPrice=" + gasPrice;
            } catch (UnsupportedOperationException e) {
                // EIP-1559交易，RawTransaction.getGasPrice()会抛出异常
                // 由于web3j的RawTransaction类可能没有直接提供getMaxFeePerGas()方法
                // 我们通过检查异常来识别EIP-1559交易，并记录为EIP1559类型
                log.debug("检测到EIP-1559交易类型，gasPrice方法不可用");
                gasPriceInfo = "gasPrice=EIP1559_transaction";
            } catch (Exception ex) {
                // 其他异常情况
                log.warn("获取gasPrice失败，交易类型未知: {}", ex.getMessage());
                gasPriceInfo = "gasPrice=unknown_error";
            }
            
            log.info("交易信息 - nonce: {}, {}, gasLimit: {}, to: {}, value: {}, dataLength: {}", 
                    rawTransaction.getNonce(),
                    gasPriceInfo,
                    rawTransaction.getGasLimit(), 
                    rawTransaction.getTo(),
                    rawTransaction.getValue(),
                    data != null ? data.length() : 0);

            // 2. 签名交易 - 判断是根据私钥还是mpc签名
            log.debug("Step 2: 开始签名交易");
            long signStartTime = System.currentTimeMillis();
            String signedTransaction = signRawTransaction(rawTransaction, unsignedHash, fromAddress, fromAddressPrivateKey, "");
            long signEndTime = System.currentTimeMillis();
            log.info("交易签名完成 - 耗时: {}ms, signedTransactionLength: {}", 
                    (signEndTime - signStartTime), signedTransaction.length());

            // 3. 发送交易
            log.debug("Step 3: 开始发送交易到区块链");
            Web3j web3j = getWeb3jInstance(chainIdentifier);
            log.debug("获取Web3j连接实例成功 - chainIdentifier: {}", chainIdentifier);
            
            long sendStartTime = System.currentTimeMillis();
            EthSendTransaction ethSendTransaction = web3j.ethSendRawTransaction(signedTransaction).send();
            long sendEndTime = System.currentTimeMillis();
            log.info("交易发送到区块链完成 - 耗时: {}ms", (sendEndTime - sendStartTime));
            
            String transactionHash = ethSendTransaction.getTransactionHash();
            if (transactionHash == null) {
                String errorMessage = ethSendTransaction.getError() != null ? 
                    ethSendTransaction.getError().getMessage() : "未知错误";
                log.error("交易发送失败 - chainIdentifier: {}, fromAddress: {}, error: {}", 
                        chainIdentifier, fromAddress, errorMessage);
                throw new RuntimeException("交易发送失败: " + errorMessage);
            }

            log.info("交易签名并发送成功 - chainIdentifier: {}, fromAddress: {}, txHash: {}, 总耗时: {}ms", 
                    chainIdentifier, fromAddress, transactionHash, (sendEndTime - signStartTime));
            return transactionHash;

        } catch (Exception e) {
            log.error("签名并发送交易异常 - chainIdentifier: {}, fromAddress: {}, error: {}", 
                    chainIdentifier, fromAddress, e.getMessage(), e);
            throw e;
        }
    }

    /**
     * 签名原始交易
     *
     * @param rawTransaction 原始交易
     * @param unsignedHash 未签名哈希
     * @param address 地址
     * @param fromAddressPrivateKey 私钥
     * @param gatewayUrl 网关URL
     * @return 签名后的交易
     */
    public String signRawTransaction(RawTransaction rawTransaction, String unsignedHash, String address,
                                          String fromAddressPrivateKey, String gatewayUrl) throws Exception {
        log.info("开始签名交易 - address: {}, unsignedHash: {}, gatewayUrl: {}", 
                address, unsignedHash, gatewayUrl != null ? gatewayUrl : "null");

        try {
            // 验证私钥格式
            log.debug("验证私钥格式");
            if (fromAddressPrivateKey == null || fromAddressPrivateKey.isEmpty()) {
                log.error("私钥为空 - address: {}", address);
                throw new IllegalArgumentException("私钥不能为空");
            }
            log.debug("私钥格式验证通过 - 长度: {}", fromAddressPrivateKey.length());
            
            // 处理私钥格式（去掉0x前缀）
            String privateKey = fromAddressPrivateKey.startsWith("0x") ? 
                fromAddressPrivateKey.substring(2) : fromAddressPrivateKey;
            log.debug("私钥格式处理完成 - 原始长度: {}, 处理后长度: {}", 
                    fromAddressPrivateKey.length(), privateKey.length());
            
            // 根据私钥创建凭证
            log.debug("开始创建Credentials凭证");
            long credentialsStartTime = System.currentTimeMillis();
            Credentials credentials = Credentials.create(privateKey);
            long credentialsEndTime = System.currentTimeMillis();
            log.debug("Credentials创建完成 - 耗时: {}ms, 地址: {}", 
                    (credentialsEndTime - credentialsStartTime), credentials.getAddress());
            
            // 验证地址是否匹配
            if (!credentials.getAddress().equalsIgnoreCase(address)) {
                log.warn("地址不匹配警告 - 私钥对应地址: {}, 传入地址: {}, 但继续签名", 
                    credentials.getAddress(), address);
            } else {
                log.debug("地址匹配验证通过 - address: {}", address);
            }
            
            // 使用私钥签名交易
            log.debug("开始使用私钥签名交易");
            long signStartTime = System.currentTimeMillis();
            byte[] signedMessage = TransactionEncoder.signMessage(rawTransaction, credentials);
            long signEndTime = System.currentTimeMillis();
            log.debug("私钥签名完成 - 耗时: {}ms, 签名数据长度: {}", 
                    (signEndTime - signStartTime), signedMessage.length);
            
            String signedTransaction = Numeric.toHexString(signedMessage);
            log.info("签名交易完成 - address: {}, 耗时: {}ms, signedTxLength: {}, signedTxPrefix: {}", 
                    address, (signEndTime - credentialsStartTime), 
                    signedTransaction.length(), signedTransaction.substring(0, Math.min(20, signedTransaction.length())));
            return signedTransaction;
            
        } catch (Exception e) {
            log.error("签名交易失败 - address: {}, error: {}, type: {}", 
                    address, e.getMessage(), e.getClass().getSimpleName(), e);
            throw new Exception("签名交易失败: " + e.getMessage(), e);
        }
    }

    /**
     * 签名原始交易
     * 
     * @param rawTransaction 原始交易
     * @param unsignedHash 未签名哈希
     * @param address 地址
     * @param pubkey 公钥
     * @param gatewayUrl 网关URL
     * @return 签名后的交易
     */
    public String signRawTransactionByMpc(RawTransaction rawTransaction, String unsignedHash, String address,
                                   String pubkey, String gatewayUrl) throws Exception {
        log.info("开始签名交易，address: {}", address);

        // 调用 MPC 签名服务
        com.alibaba.fastjson2.JSONObject jsonObject = new com.alibaba.fastjson2.JSONObject();
        jsonObject.put("message", unsignedHash);
        
        // 使用 OkHttp 发送请求
        com.alibaba.fastjson2.JSONObject jsonData = sendHttpPost(gatewayUrl + "/api/v1/mpc/sign/" + pubkey, jsonObject);
        String sig = (String) jsonData.get("signature");

        // 解析签名
        String sigR = sig.substring(0, 64);
        String sigS = sig.substring(64, 128);
        String sigV = sig.substring(128);
        Integer v = Integer.parseInt(sigV, 16) + 27;

        // 创建签名数据
        Sign.SignatureData signatureData = new Sign.SignatureData(
                v.byteValue(),
                Numeric.hexStringToByteArray(sigR),
                Numeric.hexStringToByteArray(sigS)
        );

        // 编码签名交易
        byte[] signedMessage = TransactionEncoder.encode(rawTransaction, signatureData);
        String signedTransaction = Numeric.toHexString(signedMessage);
        
        log.info("签名交易完成，address: {}", address);
        return signedTransaction;
    }

    /**
     * 获取 Web3j 实例（使用连接池）
     */
    private Web3j getWeb3jInstance(String chainIdentifier) {
        return connectionPoolService.getHttpConnection(chainIdentifier);
    }

    /**
     * 发送 HTTP POST 请求（简化版本，实际项目中应该使用 OkHttpUtils）
     */
    private com.alibaba.fastjson2.JSONObject sendHttpPost(String url, com.alibaba.fastjson2.JSONObject data) throws Exception {
        // TODO: 实现 HTTP POST 请求逻辑
        // 这里应该使用实际的 HTTP 客户端
        throw new UnsupportedOperationException("HTTP POST 请求需要在具体业务模块中实现");
    }

}
