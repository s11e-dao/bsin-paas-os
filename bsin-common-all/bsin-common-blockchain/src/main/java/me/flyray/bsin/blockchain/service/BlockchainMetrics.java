package me.flyray.bsin.blockchain.service;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tags;
import io.micrometer.core.instrument.Timer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * 区块链监控指标
 */
@Slf4j
@Component
public class BlockchainMetrics {
    
    private final MeterRegistry meterRegistry;
    private final Map<String, Timer> transactionTimers = new HashMap<>();
    private final Map<String, Counter> successCounters = new HashMap<>();
    private final Map<String, Counter> failureCounters = new HashMap<>();
    
    public BlockchainMetrics(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
    }
    
    /**
     * 记录交易成功
     */
    public void recordTransactionSuccess(String chainName, String contractAddress) {
        String key = chainName + "_success";
        successCounters.computeIfAbsent(key, k -> 
            Counter.builder("blockchain.transaction.success")
                .description("区块链交易成功次数")
                .tag("chain", chainName)
                .tag("contract", contractAddress)
                .register(meterRegistry))
            .increment();
        
        log.debug("记录交易成功指标: chain={}, contract={}", chainName, contractAddress);
    }
    
    /**
     * 记录交易失败
     */
    public void recordTransactionFailure(String chainName, String errorType) {
        String key = chainName + "_failure";
        failureCounters.computeIfAbsent(key, k -> 
            Counter.builder("blockchain.transaction.failure")
                .description("区块链交易失败次数")
                .tag("chain", chainName)
                .tag("error_type", errorType)
                .register(meterRegistry))
            .increment();
        
        log.debug("记录交易失败指标: chain={}, errorType={}", chainName, errorType);
    }
    
    /**
     * 开始交易计时
     */
    public Timer.Sample startTransactionTimer(String chainName) {
        String key = chainName + "_timer";
        Timer timer = transactionTimers.computeIfAbsent(key, k -> 
            Timer.builder("blockchain.transaction.duration")
                .description("区块链交易执行时间")
                .tag("chain", chainName)
                .register(meterRegistry));
        
        return Timer.start(meterRegistry);
    }
    
    /**
     * 记录 Gas 费用
     */
    public void recordGasFee(String chainName, String gasFee) {
        Counter.builder("blockchain.gas.fee")
            .description("区块链 Gas 费用")
            .tag("chain", chainName)
            .register(meterRegistry)
            .increment(Double.parseDouble(gasFee));
        
        log.debug("记录 Gas 费用指标: chain={}, gasFee={}", chainName, gasFee);
    }
    
    /**
     * 记录交易确认时间
     */
    public void recordConfirmationTime(String chainName, long confirmationTimeMs) {
        Timer.builder("blockchain.transaction.confirmation.time")
            .description("区块链交易确认时间")
            .tag("chain", chainName)
            .register(meterRegistry)
            .record(confirmationTimeMs, java.util.concurrent.TimeUnit.MILLISECONDS);
        
        log.debug("记录交易确认时间指标: chain={}, time={}ms", chainName, confirmationTimeMs);
    }
}
