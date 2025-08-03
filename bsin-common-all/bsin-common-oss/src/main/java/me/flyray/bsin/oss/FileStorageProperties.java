package me.flyray.bsin.oss;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 阿里云OSS配置属性类
 * 支持多个OSS平台配置
 */
@Data
@Component
@ConfigurationProperties(prefix = "dromara.x-file-storage")
public class FileStorageProperties {

    /**
     * 默认使用的存储平台
     */
    private String defaultPlatform;

    /**
     * 阿里云OSS配置列表
     */
    private List<AliOssConfig> aliyunOss;

    /**
     * 阿里云OSS单个平台配置
     */
    @Data
    public static class AliOssConfig {

        /**
         * 存储平台标识
         */
        private String platform;

        /**
         * 是否启用存储
         */
        private Boolean enableStorage = true;

        /**
         * 访问密钥ID
         */
        private String accessKey;

        /**
         * 访问密钥Secret
         */
        private String secretKey;

        /**
         * OSS服务的Endpoint
         */
        private String endPoint;

        /**
         * 存储桶名称
         */
        private String bucketName;

        /**
         * 访问域名，注意"/"结尾
         * 例如：https://abc.oss-cn-shanghai.aliyuncs.com/
         */
        private String domain;

        /**
         * 基础路径，例如：test/
         */
        private String basePath;
        
        /**
         * STS配置
         */
        private StsConfig sts;
    }
    
    /**
     * STS配置
     */
    @Data
    public static class StsConfig {
        
        /**
         * 是否启用STS
         */
        private Boolean enabled = false;
        
        /**
         * 角色ARN
         */
        private String arn;
        
        /**
         * 角色会话名称
         */
        private String roleSessionName = "bsin-oss-session";
        
        /**
         * 策略文档
         */
        private String policy;
        
        /**
         * 临时凭证有效期（秒）
         */
        private Long durationSeconds = 3600L;
    }

}

