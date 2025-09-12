package me.flyray.bsin.blockchain.transaction;

import lombok.extern.slf4j.Slf4j;
import me.flyray.bsin.blockchain.config.BlockchainProperties;
import me.flyray.bsin.blockchain.listener.BsinBlockChainTransactionListenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.Log;
import org.web3j.protocol.core.methods.response.EthGetTransactionReceipt;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.protocol.core.methods.response.Transaction;
import org.web3j.utils.Numeric;

import java.math.BigInteger;
import java.util.Optional;
import java.util.function.Consumer;

/**
 * 区块链交易处理器
 * 处理链上交易数据解析和业务逻辑
 */
@Slf4j
@Service
public class BsinBlockChainTransactionProcessor {
    
    @Autowired
    private BlockchainProperties blockchainProperties;
    
    @Autowired
    private BsinBlockChainTransactionListenService transactionListenService;
    
    /**
     * 处理交易事件
     * @param chainName 链名称
     * @param ethLog 事件日志
     * @param transactionHandler 交易处理器
     */
    public void processTransactionEvent(String chainName, Log ethLog, Consumer<ProcessedTransaction> transactionHandler) {
        try {
            String txHash = ethLog.getTransactionHash();
            log.debug("开始处理交易事件: chain={}, txHash={}", chainName, txHash);
            
            // 获取链配置
            BlockchainProperties.ChainConfig chainConfig = blockchainProperties.getChainConfig(chainName);
            if (chainConfig == null) {
                log.warn("链 {} 未配置，跳过交易处理", chainName);
                return;
            }
            
            // 检查是否启用交易缓存防重复
            if (chainConfig.isEnableTransactionCache()) {
                // TODO: 实现 Redis 缓存检查
                // 这里需要集成缓存服务来检查交易是否已处理
            }
            
            // 获取 Web3j 实例
            Web3j web3j = transactionListenService.getHttpWeb3jInstance(chainName);
            
            // 获取交易详情
            Optional<Transaction> transactionOpt = web3j.ethGetTransactionByHash(txHash).send().getTransaction();
            if (!transactionOpt.isPresent()) {
                log.warn("无法获取交易详情: txHash={}", txHash);
                return;
            }
            
            Transaction transaction = transactionOpt.get();
            
            // 获取交易回执
            EthGetTransactionReceipt receiptResponse = web3j.ethGetTransactionReceipt(txHash).send();
            if (!receiptResponse.getTransactionReceipt().isPresent()) {
                log.warn("无法获取交易回执: txHash={}", txHash);
                return;
            }
            
            TransactionReceipt receipt = receiptResponse.getTransactionReceipt().get();
            
            // 解析交易数据
            ProcessedTransaction processedTransaction = parseTransaction(chainName, transaction, receipt, ethLog);
            
            // 调用处理器
            transactionHandler.accept(processedTransaction);
            
            log.debug("交易事件处理完成: chain={}, txHash={}", chainName, txHash);
            
        } catch (Exception e) {
            log.error("处理交易事件失败: chain={}, txHash={}", chainName, ethLog.getTransactionHash(), e);
        }
    }
    
    /**
     * 解析交易数据
     */
    private ProcessedTransaction parseTransaction(String chainName, Transaction transaction, 
            TransactionReceipt receipt, Log ethLog) {
        
        ProcessedTransaction processed = new ProcessedTransaction();
        
        // 基本信息
        processed.setChainName(chainName);
        processed.setTxHash(transaction.getHash());
        processed.setFromAddress(transaction.getFrom());
        processed.setToAddress(transaction.getTo());
        processed.setContractAddress(ethLog.getAddress());
        processed.setBlockNumber(ethLog.getBlockNumber());
        processed.setTransactionIndex(ethLog.getTransactionIndex());
        
        // 交易状态
        processed.setStatus("0x1".equals(receipt.getStatus()) ? "SUCCESS" : "FAIL");
        processed.setGasUsed(receipt.getCumulativeGasUsed());
        
        // 解析合约方法调用
        String input = transaction.getInput();
        if (input != null && !input.equals("0x")) {
            ContractMethodInfo methodInfo = parseContractMethod(input);
            processed.setContractMethod(methodInfo.getMethodName());
            processed.setMethodId(methodInfo.getMethodId());
            processed.setMethodInvokeWay(methodInfo.getInvokeWay());
            processed.setTokenAmount(methodInfo.getTokenAmount());
            
            // 如果是 ERC-20 转账，更新接收地址
            if ("0xa9059cbb".equals(methodInfo.getMethodId())) {
                String receiverAddress = "0x" + Numeric.cleanHexPrefix(input.substring(34, 74));
                processed.setToAddress(receiverAddress);
                log.debug("检测到 ERC-20 转账，接收地址: {}", receiverAddress);
            }
        }
        
        return processed;
    }
    
    /**
     * 解析合约方法调用
     */
    private ContractMethodInfo parseContractMethod(String input) {
        ContractMethodInfo info = new ContractMethodInfo();
        
        if (input.length() < 10) {
            info.setMethodId("unknown");
            info.setMethodName("unknown");
            return info;
        }
        
        String methodId = input.substring(2, 10);
        info.setMethodId(methodId);
        
        // 检查是否是 ERC-20 标准方法
        if ("0xa9059cbb".equals(methodId)) {
            info.setMethodName("transfer");
            info.setInvokeWay(1); // 合约方法
            // 解析代币数量
            if (input.length() >= 74) {
                String amountHex = input.substring(input.length() - 64);
                info.setTokenAmount(Numeric.toBigInt(amountHex));
            }
        } else if ("0x23b872dd".equals(methodId)) {
            info.setMethodName("transferFrom");
            info.setInvokeWay(1); // 合约方法
            // 解析代币数量
            if (input.length() >= 138) {
                String amountHex = input.substring(138, 202);
                info.setTokenAmount(Numeric.toBigInt(amountHex));
            }
        } else {
            info.setMethodName(methodId);
            info.setInvokeWay(2); // 未知方法
        }
        
        return info;
    }
    
    /**
     * 处理后的交易信息
     */
    public static class ProcessedTransaction {
        private String chainName;
        private String txHash;
        private String fromAddress;
        private String toAddress;
        private String contractAddress;
        private BigInteger blockNumber;
        private BigInteger transactionIndex;
        private String status;
        private BigInteger gasUsed;
        private String contractMethod;
        private String methodId;
        private Integer methodInvokeWay;
        private BigInteger tokenAmount;
        
        // Getters and Setters
        public String getChainName() { return chainName; }
        public void setChainName(String chainName) { this.chainName = chainName; }
        
        public String getTxHash() { return txHash; }
        public void setTxHash(String txHash) { this.txHash = txHash; }
        
        public String getFromAddress() { return fromAddress; }
        public void setFromAddress(String fromAddress) { this.fromAddress = fromAddress; }
        
        public String getToAddress() { return toAddress; }
        public void setToAddress(String toAddress) { this.toAddress = toAddress; }
        
        public String getContractAddress() { return contractAddress; }
        public void setContractAddress(String contractAddress) { this.contractAddress = contractAddress; }
        
        public BigInteger getBlockNumber() { return blockNumber; }
        public void setBlockNumber(BigInteger blockNumber) { this.blockNumber = blockNumber; }
        
        public BigInteger getTransactionIndex() { return transactionIndex; }
        public void setTransactionIndex(BigInteger transactionIndex) { this.transactionIndex = transactionIndex; }
        
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        
        public BigInteger getGasUsed() { return gasUsed; }
        public void setGasUsed(BigInteger gasUsed) { this.gasUsed = gasUsed; }
        
        public String getContractMethod() { return contractMethod; }
        public void setContractMethod(String contractMethod) { this.contractMethod = contractMethod; }
        
        public String getMethodId() { return methodId; }
        public void setMethodId(String methodId) { this.methodId = methodId; }
        
        public Integer getMethodInvokeWay() { return methodInvokeWay; }
        public void setMethodInvokeWay(Integer methodInvokeWay) { this.methodInvokeWay = methodInvokeWay; }
        
        public BigInteger getTokenAmount() { return tokenAmount; }
        public void setTokenAmount(BigInteger tokenAmount) { this.tokenAmount = tokenAmount; }
    }
    
    /**
     * 合约方法信息
     */
    private static class ContractMethodInfo {
        private String methodId;
        private String methodName;
        private Integer invokeWay;
        private BigInteger tokenAmount;
        
        public String getMethodId() { return methodId; }
        public void setMethodId(String methodId) { this.methodId = methodId; }
        
        public String getMethodName() { return methodName; }
        public void setMethodName(String methodName) { this.methodName = methodName; }
        
        public Integer getInvokeWay() { return invokeWay; }
        public void setInvokeWay(Integer invokeWay) { this.invokeWay = invokeWay; }
        
        public BigInteger getTokenAmount() { return tokenAmount; }
        public void setTokenAmount(BigInteger tokenAmount) { this.tokenAmount = tokenAmount; }
    }
}
