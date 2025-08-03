package me.flyray.bsin.oss;

import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 阿里云OSS STS配置类
 * 基于FileStorageProperties配置，支持STS token获取
 * 
 * @author flyray
 * @since 2024-01-01
 */
@Data
@Component
public class AliOssStsConfig {
    
    @Autowired
    private FileStorageProperties fileStorageProperties;

    /**
     * 获取区域ID（从endpoint解析）
     */
    public String getRegionId() {
        FileStorageProperties.AliOssConfig config = getFirstConfig();
        if (config != null && config.getEndPoint() != null) {
            // 从endpoint解析region，例如：oss-cn-hangzhou.aliyuncs.com -> cn-hangzhou
            String endpoint = config.getEndPoint();
            if (endpoint.contains("oss-") && endpoint.contains(".aliyuncs.com")) {
                String region = endpoint.substring(endpoint.indexOf("oss-") + 4, endpoint.indexOf(".aliyuncs.com"));
                return region;
            }
        }
        return "cn-hangzhou"; // 默认区域
    }
    
    /**
     * 获取访问密钥ID
     */
    public String getAccessKey() {
        FileStorageProperties.AliOssConfig config = getFirstConfig();
        return config != null ? config.getAccessKey() : null;
    }
    
    /**
     * 获取访问密钥Secret
     */
    public String getAccessKeySecret() {
        FileStorageProperties.AliOssConfig config = getFirstConfig();
        return config != null ? config.getSecretKey() : null;
    }
    
    /**
     * 获取STS服务端点
     */
    public String getEndpoint() {
        String regionId = getRegionId();
        return "sts." + regionId + ".aliyuncs.com";
    }
    
    /**
     * 获取角色ARN
     */
    public String getArn() {
        FileStorageProperties.AliOssConfig config = getFirstConfig();
        if (config != null && config.getSts() != null) {
            return config.getSts().getArn();
        }
        return null;
    }
    
    /**
     * 获取角色会话名称
     */
    public String getRoleSessionName() {
        FileStorageProperties.AliOssConfig config = getFirstConfig();
        if (config != null && config.getSts() != null) {
            return config.getSts().getRoleSessionName();
        }
        return "bsin-oss-session";
    }
    
    /**
     * 获取策略文档
     */
    public String getPolicy() {
        FileStorageProperties.AliOssConfig config = getFirstConfig();
        if (config != null && config.getSts() != null) {
            return config.getSts().getPolicy();
        }
        return null;
    }
    
    /**
     * 获取临时凭证有效期（秒）
     */
    public Long getDurationSeconds() {
        FileStorageProperties.AliOssConfig config = getFirstConfig();
        if (config != null && config.getSts() != null) {
            return config.getSts().getDurationSeconds();
        }
        return 3600L; // 默认1小时
    }
    
    /**
     * 检查是否启用STS
     */
    public Boolean isStsEnabled() {
        FileStorageProperties.AliOssConfig config = getFirstConfig();
        if (config != null && config.getSts() != null) {
            return config.getSts().getEnabled();
        }
        return false;
    }
    
    /**
     * 获取第一个OSS配置
     */
    private FileStorageProperties.AliOssConfig getFirstConfig() {
        if (fileStorageProperties != null && 
            fileStorageProperties.getAliyunOss() != null && 
            !fileStorageProperties.getAliyunOss().isEmpty()) {
            return fileStorageProperties.getAliyunOss().get(0);
        }
        return null;
    }
    
    /**
     * 获取OSS域名
     */
    public String getOssDomain() {
        FileStorageProperties.AliOssConfig config = getFirstConfig();
        return config != null ? config.getDomain() : null;
    }
    
    /**
     * 获取OSS端点
     */
    public String getOssEndpoint() {
        FileStorageProperties.AliOssConfig config = getFirstConfig();
        return config != null ? config.getEndPoint() : null;
    }

} 