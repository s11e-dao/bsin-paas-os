package me.flyray.bsin.blockchain.exception;

import lombok.extern.slf4j.Slf4j;
import me.flyray.bsin.exception.BusinessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

/**
 * 区块链异常全局处理器
 */
@Slf4j
@RestControllerAdvice
public class BlockchainExceptionHandler {
    
    @ExceptionHandler(BlockchainException.class)
    public ResponseEntity<Map<String, Object>> handleBlockchainException(BlockchainException e) {
        log.error("区块链操作异常: {}", e.getMessage(), e);
        
        // 根据错误码确定HTTP状态码
        HttpStatus status = getHttpStatus(e.getCode());
        
        return ResponseEntity.status(status)
                .body(createErrorResponse(e.getCode(), e.getMessage()));
    }
    
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<Map<String, Object>> handleBusinessException(BusinessException e) {
        log.error("业务异常: {}", e.getMessage(), e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(createErrorResponse(e.getCode(), e.getMessage()));
    }
    
    /**
     * 根据错误码确定HTTP状态码
     */
    private HttpStatus getHttpStatus(String code) {
        switch (code) {
            case BlockchainException.INVALID_ADDRESS:
            case BlockchainException.INVALID_AMOUNT:
            case BlockchainException.INVALID_CONTRACT:
            case BlockchainException.UNSUPPORTED_CHAIN:
            case BlockchainException.INSUFFICIENT_BALANCE:
                return HttpStatus.BAD_REQUEST;
            case BlockchainException.TRANSACTION_FAILED:
            case BlockchainException.BLOCKCHAIN_ERROR:
            default:
                return HttpStatus.INTERNAL_SERVER_ERROR;
        }
    }
    
    private Map<String, Object> createErrorResponse(String code, String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put("code", code);
        response.put("message", message);
        response.put("timestamp", System.currentTimeMillis());
        return response;
    }
}
