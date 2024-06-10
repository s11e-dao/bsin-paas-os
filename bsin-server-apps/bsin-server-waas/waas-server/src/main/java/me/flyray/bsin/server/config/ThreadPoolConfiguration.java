package me.flyray.bsin.server.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.ThreadPoolExecutor;

@EnableAsync
@Configuration
public class ThreadPoolConfiguration {

    @Bean("taskExecutor")
    public ThreadPoolTaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
        //设置线程池参数信息
        taskExecutor.setCorePoolSize(10);           // 核心线程数
        taskExecutor.setMaxPoolSize(10);            // 最大线程数
        taskExecutor.setQueueCapacity(200);         // 设置任务队列的容量
        taskExecutor.setKeepAliveSeconds(60);
        taskExecutor.setThreadNamePrefix("myExecutor--");
        taskExecutor.setWaitForTasksToCompleteOnShutdown(true);
        taskExecutor.setAwaitTerminationSeconds(60);
        //修改拒绝策略为使用当前线程执行
        taskExecutor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        //初始化线程池
        taskExecutor.initialize();
        return taskExecutor;
    }
}
