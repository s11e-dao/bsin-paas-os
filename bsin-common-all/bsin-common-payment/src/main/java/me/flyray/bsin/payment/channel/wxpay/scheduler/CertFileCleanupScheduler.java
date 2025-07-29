package me.flyray.bsin.payment.channel.wxpay.scheduler;

import lombok.extern.slf4j.Slf4j;
import me.flyray.bsin.payment.channel.wxpay.model.WxPayNormalMchParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 证书文件清理定时任务
 * 定期清理过期的临时证书文件
 * 
 * @author leonard
 * @date 2025-01-XX
 */
@Slf4j
@Component
public class CertFileCleanupScheduler {

    @Autowired
    private WxPayNormalMchParams wxPayNormalMchParams;

    /**
     * 每天凌晨2点执行清理任务
     * 清理超过24小时的临时证书文件
     */
    @Scheduled(cron = "0 0 2 * * ?")
    public void cleanupExpiredCertFiles() {
        try {
            log.info("开始执行证书文件清理任务");
            
            // 清理过期的临时文件
            wxPayNormalMchParams.cleanupExpiredFiles();
            
            log.info("证书文件清理任务执行完成");
        } catch (Exception e) {
            log.error("执行证书文件清理任务时发生错误", e);
        }
    }

    /**
     * 每小时执行一次清理任务（可选，用于更频繁的清理）
     */
    @Scheduled(cron = "0 0 * * * ?")
    public void hourlyCleanup() {
        try {
            log.debug("执行小时级证书文件清理任务");
            
            // 这里可以添加更频繁的清理逻辑
            // 比如清理超过1小时的文件等
            
        } catch (Exception e) {
            log.error("执行小时级清理任务时发生错误", e);
        }
    }
}