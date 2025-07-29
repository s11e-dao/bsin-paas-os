/*
 * @Author: leonard
 * @Date: 2025-07-26 11:09:03
 * @LastEditTime: 2025-07-29 17:26:02
 * @FilePath: /bsin-paas-3.0/bsin-paas-os-3.0/bsin-common-all/bsin-common-payment/src/main/java/me/flyray/bsin/payment/channel/wxpay/model/WxPayNormalMchParams.java
 * @Description:
 *
 * Copyright (c) 2025 by CBD Technology CO., Ltd, All Rights Reserved.
 */
package me.flyray.bsin.payment.channel.wxpay.model;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSON;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import me.flyray.bsin.redis.provider.BsinRedisProvider;
import me.flyray.bsin.utils.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.UUID;

/*
 * 微信官方支付 配置参数
 *
 */
@Data
@Slf4j
@Component
public class WxPayNormalMchParams extends NormalMchParams {

  /** 应用App ID */
  private String appId;

  /** 应用AppSecret */
  private String appSecret;

  /** 微信支付商户号 */
  private String mchId;

  /** oauth2地址 */
  private String oauth2Url;

  /** API密钥 */
  private String key;

  /** 微信支付API版本 */
  private String apiVersion;

  /** API V3秘钥 */
  private String apiV3Key;

  /** 序列号 */
  private String serialNo;

  /**
   * API证书(.p12格式)
   *
   * <p>存储在本地文件系统中的路径，例如：/data/cert/apiclient_cert.p12
   */
  private String cert;

  /**
   * 证书文件(.pem格式)
   *
   * <p>存储在本地文件系统中的路径，例如：/data/cert/apiclient_cert.pem
   */
  private String apiClientCert;

  /**
   * 私钥文件(.pem格式)
   *
   * <p>存储在本地文件系统中的路径，例如：/data/cert/apiclient_key.pem
   */
  private String apiClientKey;

  /** 临时文件目录 */
  @Value("${bsin.payment.cert.temp-dir:/tmp/bsin/cert}")
  private String tempCertDir;

  /** Redis缓存前缀 */
  public static final String REDIS_CERT_PREFIX = "bsin:payment:cert:";

  /** 证书文件缓存过期时间（小时） */
  private static final int CERT_CACHE_EXPIRE_HOURS = 24;

  /** 构造函数 - 自动处理证书文件 */
  public WxPayNormalMchParams() {
    // 默认构造函数
  }

  /**
   * 带参数的构造函数 - 自动处理证书文件
   *
   * @param paramsStr JSON参数字符串
   */
  public WxPayNormalMchParams(String paramsStr) {
    if (StrUtil.isNotBlank(paramsStr)) {
      try {
        log.info("开始解析微信支付参数，参数长度: {}", paramsStr.length());
        log.debug("参数内容前200字符: {}", paramsStr.length() > 200 ? paramsStr.substring(0, 200) + "..." : paramsStr);
        
        // 解析JSON参数
        WxPayNormalMchParams params = JSON.parseObject(paramsStr, WxPayNormalMchParams.class);
        
        // 记录解析后的字段信息
        log.info("解析后的字段信息:");
        log.info("  cert: {}", params.getCert() != null ? "有值，长度: " + params.getCert().length() : "无值");
        log.info("  apiClientCert: {}", params.getApiClientCert() != null ? "有值，长度: " + params.getApiClientCert().length() : "无值");
        log.info("  apiClientKey: {}", params.getApiClientKey() != null ? "有值，长度: " + params.getApiClientKey().length() : "无值");
        
        // 复制属性
        copyProperties(params, this);
        
        // 自动处理证书文件
        this.processBase64CertFiles();
      } catch (Exception e) {
        log.error("解析微信支付参数时发生错误: {}", paramsStr, e);
      }
    } else {
      log.warn("参数字符串为空");
    }
  }

  /** 复制属性 */
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

  /** 处理Base64编码的证书文件 将Base64内容转换为文件并保存到临时目录，路径存储在Redis中 */
  public void processBase64CertFiles() {
    try {
      // 确保临时目录存在
      createTempDirectoryIfNotExists();

      // 处理API证书文件
      if (StrUtil.isNotBlank(cert)) {
        log.debug("检查cert字段内容，长度: {}", cert.length());
        // 添加调试测试
        testBase64Detection(cert, "cert");
        if (isBase64Content(cert)) {
          log.info("cert字段为Base64内容，开始保存文件");
          String certPath = saveBase64ToFile(cert, "apiclient_cert.p12", "cert");
          if (certPath != null) {
            this.cert = certPath;
            log.info("cert文件保存成功: {}", certPath);
          }
        } else {
          log.error("API证书文件：apiclient_cert.p12为非Base64内容, 请检查配置。内容长度: {}", cert.length());
        }
      }

      // 处理证书文件
      if (StrUtil.isNotBlank(apiClientCert)) {
        log.debug("检查apiClientCert字段内容，长度: {}", apiClientCert.length());
        if (isBase64Content(apiClientCert)) {
          log.info("apiClientCert字段为Base64内容，开始保存文件");
          String certPath = saveBase64ToFile(apiClientCert, "apiclient_cert.pem", "apiClientCert");
          if (certPath != null) {
            this.apiClientCert = certPath;
            log.info("apiClientCert文件保存成功: {}", certPath);
          }
        } else {
          log.error("API证书文件：apiclient_cert.pem为非Base64内容, 请检查配置。内容长度: {}", apiClientCert.length());
        }
      }

      // 处理私钥文件
      if (StrUtil.isNotBlank(apiClientKey)) {
        log.debug("检查apiClientKey字段内容，长度: {}", apiClientKey.length());
        if (isBase64Content(apiClientKey)) {
          log.info("apiClientKey字段为Base64内容，开始保存文件");
          String keyPath = saveBase64ToFile(apiClientKey, "apiclient_key.pem", "apiClientKey");
          if (keyPath != null) {
            this.apiClientKey = keyPath;
            log.info("apiClientKey文件保存成功: {}", keyPath);
          }
        } else {
          log.error("API证书文件：apiclient_key.pem为非Base64内容, 请检查配置。内容长度: {}", apiClientKey.length());
        }
      }
      log.info("Base64证书文件处理完成");
    } catch (Exception e) {
      log.error("处理Base64证书文件时发生错误", e);
    }
  }

  /** 检查内容是否为Base64编码 */
  private boolean isBase64Content(String content) {
    if (StrUtil.isBlank(content)) {
      log.debug("内容为空，不是Base64");
      return false;
    }

    log.debug("检查内容是否为Base64，原始内容长度: {}", content.length());
    log.debug("内容前100个字符: {}", content.length() > 100 ? content.substring(0, 100) + "..." : content);

    try {
      // 清理内容，移除可能的换行符、空格等
      String cleanedContent = content.replaceAll("[\\r\\n\\s]", "");
      log.debug("清理后内容长度: {}", cleanedContent.length());
      
      // 检查长度是否为4的倍数（Base64编码的要求）
      if (cleanedContent.length() % 4 != 0) {
        log.debug("清理后内容长度不是4的倍数: {}", cleanedContent.length());
        return false;
      }
      
      // 检查是否为文件路径（更严格的检查）
      // 只有当内容看起来像文件路径时才排除
      if (isFilePath(cleanedContent)) {
        log.debug("内容被识别为文件路径，尝试从文件读取Base64内容");
        // 如果是文件路径，尝试读取文件内容并转换为Base64
        try {
          String fileContent = readFileAsBase64(cleanedContent);
          if (fileContent != null) {
            log.info("成功从文件读取Base64内容，长度: {}", fileContent.length());
            // 更新当前字段的值为Base64内容
            if (content.equals(this.cert)) {
              this.cert = fileContent;
            } else if (content.equals(this.apiClientCert)) {
              this.apiClientCert = fileContent;
            } else if (content.equals(this.apiClientKey)) {
              this.apiClientKey = fileContent;
            }
            return true;
          }
        } catch (Exception e) {
          log.error("从文件读取Base64内容失败: {}", e.getMessage());
        }
        return false;
      }
      
      // 检查是否只包含Base64字符
      if (!cleanedContent.matches("^[A-Za-z0-9+/]*={0,2}$")) {
        log.debug("内容包含非Base64字符");
        return false;
      }
      
      // 尝试Base64解码
      Base64.getDecoder().decode(cleanedContent);
      log.debug("Base64解码成功，确认为Base64内容");
      return true;
    } catch (IllegalArgumentException e) {
      log.debug("内容不是有效的Base64编码: {}", e.getMessage());
      return false;
    }
  }

  /** 检查是否为文件路径 */
  private boolean isFilePath(String content) {
    if (StrUtil.isBlank(content)) {
      return false;
    }
    
    log.debug("检查是否为文件路径，内容: {}", content);
    
    // 检查是否以常见的文件路径开头
    if (content.startsWith("/") || content.startsWith("\\") || 
        content.startsWith("C:") || content.startsWith("D:") || 
        content.startsWith("E:") || content.startsWith("F:") ||
        content.startsWith("G:") || content.startsWith("H:") ||
        content.startsWith("I:") || content.startsWith("J:") ||
        content.startsWith("K:") || content.startsWith("L:") ||
        content.startsWith("M:") || content.startsWith("N:") ||
        content.startsWith("O:") || content.startsWith("P:") ||
        content.startsWith("Q:") || content.startsWith("R:") ||
        content.startsWith("S:") || content.startsWith("T:") ||
        content.startsWith("U:") || content.startsWith("V:") ||
        content.startsWith("W:") || content.startsWith("X:") ||
        content.startsWith("Y:") || content.startsWith("Z:")) {
      log.debug("内容以常见路径开头，识别为文件路径");
      return true;
    }
    
    // 检查是否包含典型的文件路径模式
    // 例如：包含多个连续的路径分隔符，或者包含文件扩展名
    if (content.contains("\\") && content.contains(".")) {
      log.debug("内容包含反斜杠和点，识别为文件路径");
      return true;
    }
    
    // 检查是否包含典型的文件扩展名（在路径中）
    String[] commonExtensions = {".pem", ".p12", ".crt", ".key", ".cer", ".pfx", ".txt", ".log"};
    for (String ext : commonExtensions) {
      if (content.contains(ext) && (content.contains("\\") || content.contains("/"))) {
        log.debug("内容包含文件扩展名{}且包含路径分隔符，识别为文件路径", ext);
        return true;
      }
    }
    
    log.debug("内容不是文件路径");
    return false;
  }

  /** 将Base64内容保存为文件 */
  private String saveBase64ToFile(String base64Content, String fileName, String fieldName) {
    try {
      // 生成唯一的文件名
      String uniqueFileName = mchId + "_" + appId + "_" + fileName;
      String filePath = tempCertDir + File.separator + uniqueFileName;
      log.info("保存文件: {}", filePath);

      //      // 检查Redis缓存
      //      //      String redisKey = REDIS_CERT_PREFIX + fieldName + ":" +
      //      // generateContentHash(base64Content);
      //      String redisKey = REDIS_CERT_PREFIX + fieldName;
      //      String cachedPath = BsinRedisProvider.getCacheObject(redisKey);
      //
      //      if (StrUtil.isNotBlank(cachedPath)) {
      //        // 检查缓存的文件是否存在
      //        if (Files.exists(Paths.get(cachedPath))) {
      //          log.info("使用缓存的证书文件: {}", cachedPath);
      //          return cachedPath;
      //        } else {
      //          // 缓存的文件不存在，删除缓存
      //          log.warn("缓存的证书文件不存在，删除缓存: {}", cachedPath);
      //          BsinRedisProvider.deleteObject(redisKey);
      //        }
      //      }

      // 清理Base64内容，移除可能的换行符、空格等
      String cleanedBase64Content = base64Content.replaceAll("[\\r\\n\\s]", "");
      
      // 解码Base64内容
      byte[] fileContent = Base64.getDecoder().decode(cleanedBase64Content);

      // 保存文件
      try (FileOutputStream fos = new FileOutputStream(filePath)) {
        fos.write(fileContent);
        fos.flush();
      }

      // 设置文件权限（仅所有者可读写）
      File file = new File(filePath);
      file.setReadable(true, true);
      file.setWritable(true, true);
      file.setExecutable(false);

      //      // 将文件路径存储到Redis（修复方法调用）
      //      BsinRedisProvider.setCacheObject(redisKey, filePath);
      log.info("证书文件已保存: {}", filePath);
      return filePath;

    } catch (Exception e) {
      log.error("保存Base64文件失败: {}", fileName, e);
      return null;
    }
  }

  /** 生成唯一的文件名 */
  private String generateUniqueFileName(String originalFileName) {
    String uuid = UUID.randomUUID().toString().replace("-", "");
    String extension = "";

    if (originalFileName.contains(".")) {
      extension = originalFileName.substring(originalFileName.lastIndexOf("."));
    }

    return uuid + extension;
  }

  /** 生成内容哈希值 */
  private String generateContentHash(String content) {
    return String.valueOf(content.hashCode());
  }

  /** 创建临时目录（如果不存在） */
  private void createTempDirectoryIfNotExists() {
    try {
      if (tempCertDir == null || tempCertDir.isEmpty()) {
        tempCertDir = System.getProperty("java.io.tmpdir") + File.separator + "bsin/cert";
        log.info("临时证书目录: {}", tempCertDir);
      }
      Path tempPath = Paths.get(tempCertDir);
      if (!Files.exists(tempPath)) {
        Files.createDirectories(tempPath);
        log.info("创建临时证书目录: {}", tempCertDir);
      }
    } catch (IOException e) {
      log.error("创建临时目录失败: {}", tempCertDir, e);
    }
  }

  /** 清理过期的临时文件 */
  public void cleanupExpiredFiles() {
    try {
      File tempDir = new File(tempCertDir);
      if (!tempDir.exists()) {
        return;
      }

      File[] files = tempDir.listFiles();
      if (files == null) {
        return;
      }

      long currentTime = System.currentTimeMillis();
      long expireTime = CERT_CACHE_EXPIRE_HOURS * 3600 * 1000L; // 转换为毫秒

      for (File file : files) {
        if (file.isFile()) {
          long lastModified = file.lastModified();
          if (currentTime - lastModified > expireTime) {
            if (file.delete()) {
              log.info("删除过期文件: {}", file.getAbsolutePath());
            }
          }
        }
      }
    } catch (Exception e) {
      log.error("清理过期文件时发生错误", e);
    }
  }

  /** 获取证书文件路径（如果为Base64则先转换） */
  public String getCertPath() {
    if (StrUtil.isNotBlank(cert) && isBase64Content(cert)) {
      processBase64CertFiles();
    }
    return cert;
  }

  /** 获取证书文件路径（如果为Base64则先转换） */
  public String getApiClientCertPath() {
    if (StrUtil.isNotBlank(apiClientCert) && isBase64Content(apiClientCert)) {
      processBase64CertFiles();
    }
    return apiClientCert;
  }

  /** 获取私钥文件路径（如果为Base64则先转换） */
  public String getApiClientKeyPath() {
    if (StrUtil.isNotBlank(apiClientKey) && isBase64Content(apiClientKey)) {
      processBase64CertFiles();
    }
    return apiClientKey;
  }

  /** 从文件读取Base64内容 */
  private String readFileAsBase64(String filePath) {
    try {
      log.info("尝试从文件读取Base64内容: {}", filePath);
      Path path = Paths.get(filePath);
      if (!Files.exists(path)) {
        log.error("文件不存在: {}", filePath);
        return null;
      }
      
      byte[] fileBytes = Files.readAllBytes(path);
      String base64Content = Base64.getEncoder().encodeToString(fileBytes);
      log.info("成功从文件读取Base64内容，长度: {}", base64Content.length());
      return base64Content;
    } catch (Exception e) {
      log.error("从文件读取Base64内容失败: {}", e.getMessage());
      return null;
    }
  }

  /** 测试Base64内容检测（用于调试） */
  public void testBase64Detection(String content, String fieldName) {
    log.info("测试{}字段的Base64检测", fieldName);
    log.info("内容长度: {}", content != null ? content.length() : 0);
    log.info("是否为空: {}", StrUtil.isBlank(content));
    
    if (content != null) {
      log.info("内容前200个字符: {}", content.length() > 200 ? content.substring(0, 200) + "..." : content);
      String cleanedContent = content.replaceAll("[\\r\\n\\s]", "");
      log.info("清理后长度: {}", cleanedContent.length());
      log.info("长度是否为4的倍数: {}", cleanedContent.length() % 4 == 0);
      log.info("是否为文件路径: {}", isFilePath(cleanedContent));
      log.info("是否只包含Base64字符: {}", cleanedContent.matches("^[A-Za-z0-9+/]*={0,2}$"));
      
      try {
        Base64.getDecoder().decode(cleanedContent);
        log.info("Base64解码成功");
      } catch (IllegalArgumentException e) {
        log.error("Base64解码失败: {}", e.getMessage());
      }
    }
    
    boolean isBase64 = isBase64Content(content);
    log.info("最终检测结果: {}", isBase64);
  }

  @Override
  public String deSenData() {
    WxPayNormalMchParams mchParams = this;
    if (StringUtils.isNotBlank(this.appSecret)) {
      mchParams.setAppSecret(StringUtils.str2Star(this.appSecret, 4, 4, 6));
    }
    if (StringUtils.isNotBlank(this.key)) {
      mchParams.setKey(StringUtils.str2Star(this.key, 4, 4, 6));
    }
    if (StringUtils.isNotBlank(this.apiV3Key)) {
      mchParams.setApiV3Key(StringUtils.str2Star(this.apiV3Key, 4, 4, 6));
    }
    if (StringUtils.isNotBlank(this.serialNo)) {
      mchParams.setSerialNo(StringUtils.str2Star(this.serialNo, 4, 4, 6));
    }
    return JSON.toJSONString(mchParams);
  }
}
