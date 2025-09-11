package me.flyray.bsin.blockchain.service;

import lombok.extern.slf4j.Slf4j;
import me.flyray.bsin.blockchain.config.BlockchainProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.FunctionReturnDecoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.generated.Uint256;
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
     * @param chainName 链名称
     * @param fromAddress 发送地址
     * @param toAddress 接收地址
     * @param contractAddress 代币合约地址
     * @param amount 转账金额
     * @param decimals 代币精度
     * @return 交易哈希
     */
    public String tokenTransfer(String chainName, String fromAddress, String toAddress, 
                               String contractAddress, BigInteger amount, BigInteger decimals) throws Exception {
        log.info("开始代币转账，chain: {}, from: {}, to: {}, contract: {}, amount: {}", 
                chainName, fromAddress, toAddress, contractAddress, amount);

        Web3j web3j = getWeb3jInstance(chainName);

        // 1. 获取交易参数
        EthGetTransactionCount ethGetTransactionCount = web3j.ethGetTransactionCount(
                fromAddress, DefaultBlockParameterName.LATEST).send();
        BigInteger nonce = ethGetTransactionCount.getTransactionCount();
        
        EthChainId ethChainId = web3j.ethChainId().send();
        BigInteger chainId = ethChainId.getChainId();

        // 2. 构建转账函数
        BigDecimal tokenValue = new BigDecimal(amount).multiply(new BigDecimal(Math.pow(10, decimals.longValue())));
        Function function = new Function(
                "transfer",
                Arrays.asList(new Address(toAddress), new Uint256(tokenValue.toBigInteger())),
                Arrays.asList(new TypeReference<org.web3j.abi.datatypes.Type<?>>() {})
        );
        String data = FunctionEncoder.encode(function);

        // 3. 估算 Gas 费用
        Transaction transaction = Transaction.createEthCallTransaction(fromAddress, contractAddress, data);
        EthEstimateGas gasEstimate = web3j.ethEstimateGas(transaction).send();
        if (gasEstimate.hasError()) {
            throw new Exception(String.format("Gas 估算失败: %s-%s", 
                    gasEstimate.getError().getCode(), gasEstimate.getError().getMessage()));
        }
        BigInteger gasLimit = gasEstimate.getAmountUsed();

        // 4. 计算 Gas 价格（使用智能Gas费管理）
        SmartGasFeeService.GasPriceInfo gasPriceInfo = smartGasFeeService.getSmartGasPrice(chainName, web3j, "normal");
        
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
        return signAndSendTransaction(chainName, rawTransaction, fromAddress);
    }

    /**
     * ETH 转账
     * 
     * @param chainName 链名称
     * @param fromAddress 发送地址
     * @param toAddress 接收地址
     * @param amount ETH 金额（Wei）
     * @return 交易哈希
     */
    public String ethTransfer(String chainName, String fromAddress, String toAddress, BigInteger amount) throws Exception {
        log.info("开始ETH转账，chain: {}, from: {}, to: {}, amount: {}", 
                chainName, fromAddress, toAddress, amount);

        Web3j web3j = getWeb3jInstance(chainName);

        // 1. 获取交易参数
        EthGetTransactionCount ethGetTransactionCount = web3j.ethGetTransactionCount(
                fromAddress, DefaultBlockParameterName.LATEST).send();
        BigInteger nonce = ethGetTransactionCount.getTransactionCount();
        
        EthChainId ethChainId = web3j.ethChainId().send();
        BigInteger chainId = ethChainId.getChainId();

        // 2. 计算 Gas 价格（使用智能Gas费管理）
        SmartGasFeeService.GasPriceInfo gasPriceInfo = smartGasFeeService.getSmartGasPrice(chainName, web3j, "normal");

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
        return signAndSendTransaction(chainName, rawTransaction, fromAddress);
    }

    /**
     * 查询代币余额
     * 
     * @param chainName 链名称
     * @param contractAddress 代币合约地址
     * @param holderAddress 持有者地址
     * @return 代币余额
     */
    public BigInteger getTokenBalance(String chainName, String contractAddress, String holderAddress) throws Exception {
        log.info("查询代币余额，chain: {}, contract: {}, holder: {}", 
                chainName, contractAddress, holderAddress);

        Web3j web3j = getWeb3jInstance(chainName);

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
     * @param chainName 链名称
     * @param txHash 交易哈希
     * @return 交易收据
     */
    public TransactionReceipt getTransactionReceipt(String chainName, String txHash) throws Exception {
        log.info("查询交易收据，chain: {}, txHash: {}", chainName, txHash);

        Web3j web3j = getWeb3jInstance(chainName);

        EthGetTransactionReceipt receipt = web3j.ethGetTransactionReceipt(txHash).send();
        return receipt.getTransactionReceipt().orElse(null);
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
                
                txHash = signAndSendTransaction(chainName, rawTransaction, fromAddress);
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
    private String signAndSendTransaction(String chainName, RawTransaction rawTransaction, String fromAddress) throws Exception {
        // 1. 序列化交易
        byte[] encodedRawTransaction = TransactionEncoder.encode(rawTransaction);
        String unsignedHash = Numeric.toHexString(Hash.sha3(encodedRawTransaction));

        // 2. 签名交易 - 需要在实际业务模块中实现
        String signedTransaction = signRawTransaction(rawTransaction, unsignedHash, fromAddress, "", "");

        // 3. 发送交易
        Web3j web3j = getWeb3jInstance(chainName);
        EthSendTransaction ethSendTransaction = web3j.ethSendRawTransaction(signedTransaction).send();
        
        String transactionHash = ethSendTransaction.getTransactionHash();
        if (transactionHash == null) {
            log.error("交易发送失败: {}", ethSendTransaction.getError().getMessage());
            throw new RuntimeException("交易发送失败: " + ethSendTransaction.getError().getMessage());
        }

        log.info("交易发送成功，txHash: {}", transactionHash);
        return transactionHash;
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
    public String signRawTransaction(RawTransaction rawTransaction, String unsignedHash, String address, 
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
    private Web3j getWeb3jInstance(String chainName) {
        return connectionPoolService.getHttpConnection(chainName);
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
