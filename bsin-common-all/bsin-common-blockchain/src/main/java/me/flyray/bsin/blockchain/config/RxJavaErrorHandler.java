package me.flyray.bsin.blockchain.config;

import io.reactivex.plugins.RxJavaPlugins;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;

import jakarta.annotation.PostConstruct;

/**
 * RxJava 全局错误处理器配置
 * 用于处理 RxJava 流中的未处理异常，避免 UndeliverableException
 * 
 * @author bolei
 * @date 2024/3/14
 */
@Slf4j
@Configuration
public class RxJavaErrorHandler {
    
    @PostConstruct
    public void initRxJavaErrorHandler() {
        // 设置全局错误处理器，处理未捕获的异常
        RxJavaPlugins.setErrorHandler(throwable -> {
            if (throwable instanceof io.reactivex.exceptions.UndeliverableException) {
                // 对于 UndeliverableException，只记录警告日志，不抛出异常
                log.warn("RxJava 流中发生未传递的异常: {}", throwable.getMessage());
            } else {
                // 对于其他异常，记录错误日志
                log.error("RxJava 流中发生未处理的异常", throwable);
            }
        });
        
        log.info("RxJava 全局错误处理器已初始化");
    }
}
