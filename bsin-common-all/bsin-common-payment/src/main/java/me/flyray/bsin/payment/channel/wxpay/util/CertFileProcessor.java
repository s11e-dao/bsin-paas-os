package me.flyray.bsin.payment.channel.wxpay.util;

import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import me.flyray.bsin.payment.channel.wxpay.model.WxPayNormalMchParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 证书文件处理工具类
 * 提供统一的证书文件处理接口
 * 
 * @author leonard
 * @date 2025-01-XX
 */
@Slf4j
@Component
public class CertFileProcessor {

    @Autowired
    private WxPayNormalMchParams wxPayNormalMchParams;

    /**
     * 处理微信支付证书文件
     * 如果证书文件为Base64编码，则转换为文件并保存到临时目录
     * 
     * @param params 微信支付参数
     * @return 处理后的参数
     */
    public WxPayNormalMchParams processWxPayCertFiles(WxPayNormalMchParams params) {
        if (params == null) {
            return null;
        }

        try {
            // 复制参数对象
            WxPayNormalMchParams processedParams = new WxPayNormalMchParams();
            // 复制所有属性
            copyProperties(params, processedParams);
            
            // 处理Base64证书文件
            processedParams.processBase64CertFiles();
            
            log.info("微信支付证书文件处理完成");
            return processedParams;
            
        } catch (Exception e) {
            log.error("处理微信支付证书文件时发生错误", e);
            return params; // 返回原始参数
        }
    }

    /**
     * 复制属性
     */
    private void copyProperties(WxPayNormalMchParams source, WxPayNormalMchParams target) {
        target.setAppId(source.getAppId());
        target.setAppSecret(source.getAppSecret());
        target.setMchId(source.getMchId());
        target.setOauth2Url(source.getOauth2Url());
        target.setKey(source.getKey());
        target.setApiVersion(source.getApiVersion());
        target.setApiV3Key(source.getApiV3Key());
        target.setSerialNo(source.getSerialNo());
        target.setCert(source.getCert());
        target.setApiClientCert(source.getApiClientCert());
        target.setApiClientKey(source.getApiClientKey());
    }

    /**
     * 获取证书文件路径（自动处理Base64转换）
     * 
     * @param params 微信支付参数
     * @param certType 证书类型：cert, apiClientCert, apiClientKey
     * @return 文件路径
     */
    public String getCertFilePath(WxPayNormalMchParams params, String certType) {
        if (params == null || StrUtil.isBlank(certType)) {
            return null;
        }

        try {
            switch (certType.toLowerCase()) {
                case "cert":
                    return params.getCertPath();
                case "apiclientcert":
                    return params.getApiClientCertPath();
                case "apiclientkey":
                    return params.getApiClientKeyPath();
                default:
                    log.warn("未知的证书类型: {}", certType);
                    return null;
            }
        } catch (Exception e) {
            log.error("获取证书文件路径时发生错误: {}", certType, e);
            return null;
        }
    }

    /**
     * 检查证书文件是否存在
     * 
     * @param filePath 文件路径
     * @return 是否存在
     */
    public boolean isCertFileExists(String filePath) {
        if (StrUtil.isBlank(filePath)) {
            return false;
        }

        try {
            java.io.File file = new java.io.File(filePath);
            return file.exists() && file.isFile();
        } catch (Exception e) {
            log.error("检查证书文件是否存在时发生错误: {}", filePath, e);
            return false;
        }
    }

    /**
     * 验证证书文件的有效性
     * 
     * @param filePath 文件路径
     * @return 是否有效
     */
    public boolean validateCertFile(String filePath) {
        if (!isCertFileExists(filePath)) {
            return false;
        }

        try {
            java.io.File file = new java.io.File(filePath);
            
            // 检查文件大小（证书文件通常不会太大）
            long fileSize = file.length();
            if (fileSize < 100 || fileSize > 1024 * 1024) { // 100字节到1MB
                log.warn("证书文件大小异常: {} bytes", fileSize);
                return false;
            }

            // 检查文件权限
            if (!file.canRead()) {
                log.warn("证书文件无法读取: {}", filePath);
                return false;
            }

            return true;
        } catch (Exception e) {
            log.error("验证证书文件时发生错误: {}", filePath, e);
            return false;
        }
    }

    /**
     * 清理所有临时证书文件
     */
    public void cleanupAllCertFiles() {
        try {
            wxPayNormalMchParams.cleanupExpiredFiles();
            log.info("所有临时证书文件清理完成");
        } catch (Exception e) {
            log.error("清理临时证书文件时发生错误", e);
        }
    }

    /**
     * 获取证书文件信息
     * 
     * @param filePath 文件路径
     * @return 文件信息
     */
    public CertFileInfo getCertFileInfo(String filePath) {
        if (!isCertFileExists(filePath)) {
            return null;
        }

        try {
            java.io.File file = new java.io.File(filePath);
            return CertFileInfo.builder()
                    .name(file.getName())
                    .path(file.getAbsolutePath())
                    .size(file.length())
                    .lastModified(file.lastModified())
                    .canRead(file.canRead())
                    .canWrite(file.canWrite())
                    .build();
        } catch (Exception e) {
            log.error("获取证书文件信息时发生错误: {}", filePath, e);
            return null;
        }
    }

    /**
     * 证书文件信息类
     */
    public static class CertFileInfo {
        private String name;
        private String path;
        private long size;
        private long lastModified;
        private boolean canRead;
        private boolean canWrite;

        // 使用Builder模式
        public static CertFileInfoBuilder builder() {
            return new CertFileInfoBuilder();
        }

        // Getter和Setter方法
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getPath() { return path; }
        public void setPath(String path) { this.path = path; }
        public long getSize() { return size; }
        public void setSize(long size) { this.size = size; }
        public long getLastModified() { return lastModified; }
        public void setLastModified(long lastModified) { this.lastModified = lastModified; }
        public boolean isCanRead() { return canRead; }
        public void setCanRead(boolean canRead) { this.canRead = canRead; }
        public boolean isCanWrite() { return canWrite; }
        public void setCanWrite(boolean canWrite) { this.canWrite = canWrite; }

        public static class CertFileInfoBuilder {
            private CertFileInfo info = new CertFileInfo();

            public CertFileInfoBuilder name(String name) {
                info.name = name;
                return this;
            }

            public CertFileInfoBuilder path(String path) {
                info.path = path;
                return this;
            }

            public CertFileInfoBuilder size(long size) {
                info.size = size;
                return this;
            }

            public CertFileInfoBuilder lastModified(long lastModified) {
                info.lastModified = lastModified;
                return this;
            }

            public CertFileInfoBuilder canRead(boolean canRead) {
                info.canRead = canRead;
                return this;
            }

            public CertFileInfoBuilder canWrite(boolean canWrite) {
                info.canWrite = canWrite;
                return this;
            }

            public CertFileInfo build() {
                return info;
            }
        }
    }
}