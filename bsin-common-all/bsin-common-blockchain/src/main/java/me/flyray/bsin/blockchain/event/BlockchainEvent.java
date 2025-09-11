package me.flyray.bsin.blockchain.event;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigInteger;

/**
 * 区块链事件基类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public abstract class BlockchainEvent {
    private String chainName;
    private long timestamp;
    private String eventId;
}

/**
 * 交易确认事件
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
class TransactionConfirmedEvent extends BlockchainEvent {
    private String transactionHash;
    private String fromAddress;
    private String toAddress;
    private BigInteger amount;
    private long blockNumber;
    private String contractAddress;
    
    public TransactionConfirmedEvent(String chainName, String transactionHash, String fromAddress, 
                                   String toAddress, BigInteger amount, long blockNumber, String contractAddress) {
        super(chainName, System.currentTimeMillis(), "tx_confirmed_" + transactionHash);
        this.transactionHash = transactionHash;
        this.fromAddress = fromAddress;
        this.toAddress = toAddress;
        this.amount = amount;
        this.blockNumber = blockNumber;
        this.contractAddress = contractAddress;
    }
}

/**
 * 交易失败事件
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
class TransactionFailedEvent extends BlockchainEvent {
    private String transactionHash;
    private String fromAddress;
    private String toAddress;
    private BigInteger amount;
    private String errorMessage;
    private String contractAddress;
    
    public TransactionFailedEvent(String chainName, String transactionHash, String fromAddress, 
                                String toAddress, BigInteger amount, String errorMessage, String contractAddress) {
        super(chainName, System.currentTimeMillis(), "tx_failed_" + transactionHash);
        this.transactionHash = transactionHash;
        this.fromAddress = fromAddress;
        this.toAddress = toAddress;
        this.amount = amount;
        this.errorMessage = errorMessage;
        this.contractAddress = contractAddress;
    }
}

/**
 * Gas 费用不足事件
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
class InsufficientGasEvent extends BlockchainEvent {
    private String address;
    private String requiredGas;
    private String currentGas;
    
    public InsufficientGasEvent(String chainName, String address, String requiredGas, String currentGas) {
        super(chainName, System.currentTimeMillis(), "insufficient_gas_" + address);
        this.address = address;
        this.requiredGas = requiredGas;
        this.currentGas = currentGas;
    }
}
