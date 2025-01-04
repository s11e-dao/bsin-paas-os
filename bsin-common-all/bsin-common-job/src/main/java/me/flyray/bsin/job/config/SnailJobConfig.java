package me.flyray.bsin.job.config;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.ILoggingEvent;
import com.aizuda.snailjob.client.common.appender.SnailLog4j2Appender;
import com.aizuda.snailjob.client.common.appender.SnailLogbackAppender;
import com.aizuda.snailjob.client.common.event.SnailClientStartingEvent;
import com.aizuda.snailjob.client.starter.EnableSnailJob;
import org.apache.logging.log4j.core.Layout;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * SnailJob基础配置
 */
@AutoConfiguration
@ConditionalOnProperty(prefix = "snail-job", name = "enabled", havingValue = "true",matchIfMissing = true)
@EnableScheduling
@EnableSnailJob
public class SnailJobConfig {

    /**
     * Logback配置类
     */
    @Configuration
    @ConditionalOnClass(value = {ch.qos.logback.classic.LoggerContext.class,SnailLogbackAppender.class})
    public static class LogbackConfiguration {
        
        @EventListener(SnailClientStartingEvent.class)
        public void onStarting(SnailClientStartingEvent event) {
            LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();
            SnailLogbackAppender<ILoggingEvent> appender = new SnailLogbackAppender<>();
            appender.setName("snail_log_appender");
            appender.setContext(context);
            appender.start();
            Logger rootLogger = context.getLogger(Logger.ROOT_LOGGER_NAME);
            rootLogger.addAppender(appender);
        }
    }

    /**
     * Log4j2配置类
     */
    @Configuration
    @ConditionalOnClass(value = {org.apache.logging.log4j.core.LoggerContext.class,SnailLog4j2Appender.class})
    public static class Log4j2Configuration {
        
        @EventListener(SnailClientStartingEvent.class)
        public void onStarting(SnailClientStartingEvent event) {
            org.apache.logging.log4j.core.LoggerContext context = 
                (org.apache.logging.log4j.core.LoggerContext) org.apache.logging.log4j.LogManager.getContext(false);
            org.apache.logging.log4j.core.config.Configuration config = context.getConfiguration();
            Layout<?> layout = org.apache.logging.log4j.core.layout.PatternLayout.createDefaultLayout(config);
            SnailLog4j2Appender snailAppender = SnailLog4j2Appender.create("snail_log_appender",
                null,
                layout,
                "true",
                null,
                null
            );
            snailAppender.start();
            config.addAppender(snailAppender);
            config.getRootLogger().addAppender(snailAppender, org.apache.logging.log4j.Level.INFO, null);
            context.updateLoggers();
        }
    }
}