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
  private static final String REDIS_CERT_PREFIX = "bsin:payment:cert:";

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
        // 解析JSON参数
        WxPayNormalMchParams params = JSON.parseObject(paramsStr, WxPayNormalMchParams.class);
        // 复制属性
        copyProperties(params, this);
        // 自动处理证书文件
        this.processBase64CertFiles();
      } catch (Exception e) {
        log.error("解析微信支付参数时发生错误: {}", paramsStr, e);
      }
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
      if (StrUtil.isNotBlank(cert) && isBase64Content(cert)) {
        String certPath = saveBase64ToFile(cert, "apiclient_cert.p12", "cert");
        if (certPath != null) {
          this.cert = certPath;
        }
      }

      // 处理证书文件
      if (StrUtil.isNotBlank(apiClientCert) && isBase64Content(apiClientCert)) {
        String certPath = saveBase64ToFile(apiClientCert, "apiclient_cert.pem", "apiClientCert");
        if (certPath != null) {
          this.apiClientCert = certPath;
        }
      }

      // 处理私钥文件
      if (StrUtil.isNotBlank(apiClientKey) && isBase64Content(apiClientKey)) {
        String keyPath = saveBase64ToFile(apiClientKey, "apiclient_key.pem", "apiClientKey");
        if (keyPath != null) {
          this.apiClientKey = keyPath;
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
      return false;
    }

    // 检查是否为文件路径（包含路径分隔符）
    if (content.contains("/") || content.contains("\\")) {
      return false;
    }

    try {
      // 尝试Base64解码
      Base64.getDecoder().decode(content);
      return true;
    } catch (IllegalArgumentException e) {
      return false;
    }
  }

  /** 将Base64内容保存为文件 */
  private String saveBase64ToFile(String base64Content, String fileName, String fieldName) {
    try {
      // 生成唯一的文件名
      //      String uniqueFileName = generateUniqueFileName(fileName);
      String uniqueFileName = mchId + "_" + appId + "_" + fileName;
      String extension = "";
      if (fileName.contains(".")) {
        extension = fileName.substring(fileName.lastIndexOf("."));
      }
      uniqueFileName = uniqueFileName + extension;
      String filePath = tempCertDir + File.separator + uniqueFileName;
      log.info("保存文件: {}", filePath);

      // 检查Redis缓存
//      String redisKey = REDIS_CERT_PREFIX + fieldName + ":" + generateContentHash(base64Content);
      String redisKey = REDIS_CERT_PREFIX + fieldName;
      String cachedPath = BsinRedisProvider.getCacheObject(redisKey);

      if (StrUtil.isNotBlank(cachedPath)) {
        // 检查缓存的文件是否存在
        if (Files.exists(Paths.get(cachedPath))) {
          log.info("使用缓存的证书文件: {}", cachedPath);
          return cachedPath;
        } else {
          // 缓存的文件不存在，删除缓存
          log.warn("缓存的证书文件不存在，删除缓存: {}", cachedPath);
          BsinRedisProvider.deleteObject(redisKey);
        }
      }

      // 解码Base64内容
      byte[] fileContent = Base64.getDecoder().decode(base64Content);

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

      // 将文件路径存储到Redis（修复方法调用）
      BsinRedisProvider.setCacheObject(redisKey, filePath);

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
