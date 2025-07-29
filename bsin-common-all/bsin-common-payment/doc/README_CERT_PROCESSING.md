# 微信支付证书文件处理功能说明

## 功能概述

本功能实现了微信支付证书文件的自动处理，支持Base64编码的证书文件转换为实际文件并保存到服务器临时目录，同时使用Redis缓存文件路径以提高性能。

## 核心功能

### 1. Base64文件转换
- 自动检测证书文件是否为Base64编码
- 将Base64内容解码为原始文件
- 保存到服务器临时目录
- 生成唯一的文件名避免冲突

### 2. Redis缓存管理
- 使用文件内容哈希值作为缓存键
- 缓存文件路径，避免重复转换
- 支持缓存过期时间配置
- 自动清理无效缓存

### 3. 文件生命周期管理
- 定时清理过期文件
- 文件权限管理
- 文件有效性验证

## 使用示例

### 1. 基本使用

```java
@Autowired
private CertFileProcessor certFileProcessor;

// 处理微信支付参数中的证书文件
WxPayNormalMchParams params = new WxPayNormalMchParams();
params.setAppId("your_app_id");
params.setMchId("your_mch_id");

// 设置Base64编码的证书文件
String base64Cert = "MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQC...";
params.setCert(base64Cert);
params.setApiClientCert(base64Cert);
params.setApiClientKey(base64Cert);

// 处理证书文件
WxPayNormalMchParams processedParams = certFileProcessor.processWxPayCertFiles(params);

// 获取处理后的文件路径
String certPath = processedParams.getCertPath();
String apiClientCertPath = processedParams.getApiClientCertPath();
String apiClientKeyPath = processedParams.getApiClientKeyPath();
```

### 2. 直接使用WxPayNormalMchParams

```java
WxPayNormalMchParams params = new WxPayNormalMchParams();

// 设置Base64证书
params.setCert("base64_encoded_cert_content");

// 自动处理Base64文件
params.processBase64CertFiles();

// 获取文件路径
String certPath = params.getCertPath();
```

### 3. 文件验证

```java
// 检查文件是否存在
boolean exists = certFileProcessor.isCertFileExists("/path/to/cert.pem");

// 验证文件有效性
boolean valid = certFileProcessor.validateCertFile("/path/to/cert.pem");

// 获取文件信息
CertFileProcessor.CertFileInfo info = certFileProcessor.getCertFileInfo("/path/to/cert.pem");
if (info != null) {
    System.out.println("文件名: " + info.getName());
    System.out.println("文件大小: " + info.getSize() + " bytes");
    System.out.println("可读: " + info.isCanRead());
}
```

## 配置说明

### 1. 应用配置

在 `application.yml` 中添加以下配置：

```yaml
bsin:
  payment:
    cert:
      # 临时文件目录
      temp-dir: /tmp/bsin/cert
      # 缓存过期时间（小时）
      cache-expire-hours: 24
      # 是否启用证书文件处理
      enabled: true
      # 文件大小限制（字节）
      max-file-size: 1048576  # 1MB
```

### 2. Redis配置

确保Redis配置正确：

```yaml
spring:
  redis:
    host: localhost
    port: 6379
    database: 0
    timeout: 3000ms
```

## 文件处理流程

### 1. Base64检测
```java
// 检查内容是否为Base64编码
private boolean isBase64Content(String content) {
    if (StrUtil.isBlank(content)) {
        return false;
    }
    
    // 检查是否为文件路径
    if (content.contains("/") || content.contains("\\")) {
        return false;
    }
    
    try {
        Base64.getDecoder().decode(content);
        return true;
    } catch (IllegalArgumentException e) {
        return false;
    }
}
```

### 2. 文件保存
```java
// 将Base64内容保存为文件
private String saveBase64ToFile(String base64Content, String fileName, String fieldName) {
    // 生成唯一文件名
    String uniqueFileName = generateUniqueFileName(fileName);
    String filePath = tempCertDir + File.separator + uniqueFileName;
    
    // 检查Redis缓存
    String redisKey = REDIS_CERT_PREFIX + fieldName + ":" + generateContentHash(base64Content);
    String cachedPath = BsinRedisProvider.getCacheObject(redisKey);
    
    if (StrUtil.isNotBlank(cachedPath) && Files.exists(Paths.get(cachedPath))) {
        return cachedPath; // 使用缓存
    }
    
    // 解码并保存文件
    byte[] fileContent = Base64.getDecoder().decode(base64Content);
    try (FileOutputStream fos = new FileOutputStream(filePath)) {
        fos.write(fileContent);
    }
    
    // 设置文件权限
    File file = new File(filePath);
    file.setReadable(true, true);
    file.setWritable(true, true);
    
    // 缓存文件路径
    BsinRedisProvider.setCacheObject(redisKey, filePath, CERT_CACHE_EXPIRE_HOURS * 3600);
    
    return filePath;
}
```

### 3. 缓存管理
```java
// Redis缓存键格式
// bsin:payment:cert:{fieldName}:{contentHash}

// 示例缓存键
// bsin:payment:cert:cert:123456789
// bsin:payment:cert:apiClientCert:987654321
// bsin:payment:cert:apiClientKey:456789123
```

## 定时任务

### 1. 文件清理任务

```java
@Component
public class CertFileCleanupScheduler {
    
    @Scheduled(cron = "0 0 2 * * ?") // 每天凌晨2点
    public void cleanupExpiredCertFiles() {
        wxPayNormalMchParams.cleanupExpiredFiles();
    }
}
```

### 2. 清理逻辑

```java
public void cleanupExpiredFiles() {
    File tempDir = new File(tempCertDir);
    if (!tempDir.exists()) {
        return;
    }
    
    File[] files = tempDir.listFiles();
    if (files == null) {
        return;
    }
    
    long currentTime = System.currentTimeMillis();
    long expireTime = CERT_CACHE_EXPIRE_HOURS * 3600 * 1000L;
    
    for (File file : files) {
        if (file.isFile()) {
            long lastModified = file.lastModified();
            if (currentTime - lastModified > expireTime) {
                file.delete();
            }
        }
    }
}
```

## 错误处理

### 1. 常见错误

- **文件大小超限**: 证书文件超过1MB限制
- **Base64解码失败**: 内容不是有效的Base64编码
- **文件保存失败**: 磁盘空间不足或权限问题
- **Redis连接失败**: 缓存服务不可用

### 2. 错误处理策略

```java
try {
    // 处理证书文件
    params.processBase64CertFiles();
} catch (Exception e) {
    log.error("处理证书文件时发生错误", e);
    // 使用原始参数，不进行转换
    return originalParams;
}
```

## 性能优化

### 1. 缓存策略
- 使用文件内容哈希值作为缓存键
- 避免重复转换相同内容
- 设置合理的缓存过期时间

### 2. 文件管理
- 使用唯一文件名避免冲突
- 定期清理过期文件
- 设置合适的文件权限

### 3. 内存优化
- 流式处理大文件
- 及时释放资源
- 避免内存泄漏

## 安全考虑

### 1. 文件安全
- 设置严格的文件权限
- 使用临时目录存储
- 定期清理敏感文件

### 2. 缓存安全
- 使用加密的Redis连接
- 设置缓存过期时间
- 避免缓存敏感信息

### 3. 输入验证
- 验证Base64内容有效性
- 检查文件大小限制
- 验证文件类型

## 监控和日志

### 1. 关键日志
```java
log.info("证书文件已保存: {}", filePath);
log.info("使用缓存的证书文件: {}", cachedPath);
log.info("Base64证书文件处理完成");
log.error("保存Base64文件失败: {}", fileName, e);
```

### 2. 监控指标
- 文件转换成功率
- 缓存命中率
- 文件清理数量
- 错误率统计

## 测试用例

### 1. 单元测试
```java
@Test
void testProcessWxPayCertFiles_WithBase64Cert() {
    String base64Cert = Base64.getEncoder().encodeToString("test content".getBytes());
    testParams.setCert(base64Cert);
    
    WxPayNormalMchParams result = certFileProcessor.processWxPayCertFiles(testParams);
    
    assertNotNull(result);
    assertNotEquals(base64Cert, result.getCert());
    assertTrue(result.getCert().contains("/tmp/test/cert/"));
}
```

### 2. 集成测试
```java
@Test
void testCertFileLifecycle() {
    // 1. 创建Base64证书
    // 2. 处理文件
    // 3. 验证文件存在
    // 4. 清理文件
    // 5. 验证文件已删除
}
```

## 部署注意事项

### 1. 目录权限
确保临时目录有正确的读写权限：
```bash
mkdir -p /tmp/bsin/cert
chmod 755 /tmp/bsin/cert
```

### 2. Redis配置
确保Redis服务正常运行且配置正确。

### 3. 磁盘空间
确保临时目录有足够的磁盘空间。

### 4. 定时任务
确保定时任务配置正确并正常运行。

## 更新日志

- **2025-01-XX**: 初始版本，支持Base64证书文件处理
- **2025-01-XX**: 添加Redis缓存功能
- **2025-01-XX**: 添加定时清理任务
- **2025-01-XX**: 优化错误处理和日志记录