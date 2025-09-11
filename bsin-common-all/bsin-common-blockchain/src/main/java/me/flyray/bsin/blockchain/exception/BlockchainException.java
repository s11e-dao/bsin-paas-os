package me.flyray.bsin.blockchain.exception;

import me.flyray.bsin.exception.BusinessException;

/**
 * 统一的区块链异常类
 * 通过错误码区分不同类型的错误
 */
public class BlockchainException extends BusinessException {
    
    // 错误码常量
    public static final String INVALID_ADDRESS = "INVALID_ADDRESS";
    public static final String INVALID_AMOUNT = "INVALID_AMOUNT";
    public static final String INVALID_CONTRACT = "INVALID_CONTRACT";
    public static final String UNSUPPORTED_CHAIN = "UNSUPPORTED_CHAIN";
    public static final String INSUFFICIENT_BALANCE = "INSUFFICIENT_BALANCE";
    public static final String TRANSACTION_FAILED = "TRANSACTION_FAILED";
    public static final String BLOCKCHAIN_ERROR = "BLOCKCHAIN_ERROR";
    
    public BlockchainException(String code, String message) {
        super(code, message);
    }
    
    public BlockchainException(String code, String message, Throwable cause) {
        super(code, message, cause);
    }
    
    // 便捷方法
    public static BlockchainException invalidAddress(String message) {
        return new BlockchainException(INVALID_ADDRESS, message);
    }
    
    public static BlockchainException invalidAmount(String message) {
        return new BlockchainException(INVALID_AMOUNT, message);
    }
    
    public static BlockchainException invalidContract(String message) {
        return new BlockchainException(INVALID_CONTRACT, message);
    }
    
    public static BlockchainException unsupportedChain(String message) {
        return new BlockchainException(UNSUPPORTED_CHAIN, message);
    }
    
    public static BlockchainException insufficientBalance(String message) {
        return new BlockchainException(INSUFFICIENT_BALANCE, message);
    }
    
    public static BlockchainException transactionFailed(String message) {
        return new BlockchainException(TRANSACTION_FAILED, message);
    }
    
    public static BlockchainException transactionFailed(String message, Throwable cause) {
        return new BlockchainException(TRANSACTION_FAILED, message, cause);
    }
}

