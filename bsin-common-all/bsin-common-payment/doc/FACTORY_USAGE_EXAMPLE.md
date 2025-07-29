# NormalMchParams.factory() 方法使用示例

## 功能概述

`NormalMchParams.factory()` 方法现在支持自动处理微信支付证书文件。当返回 `WxPayNormalMchParams` 实例时，会自动检测并处理Base64编码的证书文件，将其转换为实际文件并保存到服务器临时目录。

## 核心改进

### 1. 自动证书处理
- ✅ 在 `factory()` 方法中自动检测 `WxPayNormalMchParams` 实例
- ✅ 自动调用 `processBase64CertFiles()` 方法处理证书文件
- ✅ 支持Base64编码的证书文件自动转换
- ✅ 支持Redis缓存机制避免重复转换

### 2. 智能内容检测
- ✅ 自动区分Base64内容和文件路径
- ✅ 只处理Base64编码的证书文件
- ✅ 保持文件路径格式不变

## 使用示例

### 1. 基本使用（自动处理Base64证书）

```java
// 准备包含Base64证书的JSON参数
String base64Cert = Base64.getEncoder().encodeToString("certificate content".getBytes());
String paramsStr = String.format(
    "{\"appId\":\"wx123456789\",\"mchId\":\"1234567890\",\"key\":\"your_api_key\"," +
    "\"cert\":\"%s\",\"apiClientCert\":\"%s\",\"apiClientKey\":\"%s\"}",
    base64Cert, base64Cert, base64Cert
);

// 使用factory方法创建实例（自动处理证书文件）
NormalMchParams params = NormalMchParams.factory("wxpay", paramsStr);

// 验证结果
if (params instanceof WxPayNormalMchParams) {
    WxPayNormalMchParams wxPayParams = (WxPayNormalMchParams) params;
    
    // 证书文件已被自动处理，现在是文件路径而不是Base64内容
    System.out.println("证书路径: " + wxPayParams.getCert());
    System.out.println("证书文件路径: " + wxPayParams.getApiClientCert());
    System.out.println("私钥文件路径: " + wxPayParams.getApiClientKey());
    
    // 输出示例：
    // 证书路径: /tmp/bsin/cert/a1b2c3d4e5f6.p12
    // 证书文件路径: /tmp/bsin/cert/f7g8h9i0j1k2.pem
    // 私钥文件路径: /tmp/bsin/cert/l3m4n5o6p7q8.pem
}
```

### 2. 混合内容处理（Base64 + 文件路径）

```java
// 准备混合内容的JSON参数
String base64Cert = Base64.getEncoder().encodeToString("certificate content".getBytes());
String paramsStr = String.format(
    "{\"appId\":\"wx123456789\",\"mchId\":\"1234567890\",\"key\":\"your_api_key\"," +
    "\"cert\":\"%s\",\"apiClientCert\":\"/data/cert/apiclient_cert.pem\",\"apiClientKey\":\"%s\"}",
    base64Cert, base64Cert
);

// 使用factory方法创建实例
NormalMchParams params = NormalMchParams.factory("wxpay", paramsStr);

if (params instanceof WxPayNormalMchParams) {
    WxPayNormalMchParams wxPayParams = (WxPayNormalMchParams) params;
    
    // Base64内容被转换为文件路径
    assertNotEquals(base64Cert, wxPayParams.getCert());
    assertNotEquals(base64Cert, wxPayParams.getApiClientKey());
    
    // 文件路径保持不变
    assertEquals("/data/cert/apiclient_cert.pem", wxPayParams.getApiClientCert());
}
```

### 3. 纯文件路径处理

```java
// 准备只包含文件路径的JSON参数
String paramsStr = "{\"appId\":\"wx123456789\",\"mchId\":\"1234567890\",\"key\":\"your_api_key\"," +
                  "\"cert\":\"/data/cert/apiclient_cert.p12\",\"apiClientCert\":\"/data/cert/apiclient_cert.pem\"," +
                  "\"apiClientKey\":\"/data/cert/apiclient_key.pem\"}";

// 使用factory方法创建实例
NormalMchParams params = NormalMchParams.factory("wxpay", paramsStr);

if (params instanceof WxPayNormalMchParams) {
    WxPayNormalMchParams wxPayParams = (WxPayNormalMchParams) params;
    
    // 文件路径保持不变
    assertEquals("/data/cert/apiclient_cert.p12", wxPayParams.getCert());
    assertEquals("/data/cert/apiclient_cert.pem", wxPayParams.getApiClientCert());
    assertEquals("/data/cert/apiclient_key.pem", wxPayParams.getApiClientKey());
}
```

## 构造函数使用

### 1. 带参数的构造函数

```java
// 准备JSON参数字符串
String base64Cert = Base64.getEncoder().encodeToString("certificate content".getBytes());
String paramsStr = String.format(
    "{\"appId\":\"wx123456789\",\"mchId\":\"1234567890\",\"key\":\"your_api_key\"," +
    "\"cert\":\"%s\",\"apiClientCert\":\"%s\",\"apiClientKey\":\"%s\"}",
    base64Cert, base64Cert, base64Cert
);

// 使用带参数的构造函数（自动处理证书文件）
WxPayNormalMchParams params = new WxPayNormalMchParams(paramsStr);

// 证书文件已被自动处理
System.out.println("证书路径: " + params.getCert());
System.out.println("应用ID: " + params.getAppId());
System.out.println("商户号: " + params.getMchId());
```

### 2. 默认构造函数 + 手动处理

```java
// 使用默认构造函数
WxPayNormalMchParams params = new WxPayNormalMchParams();

// 设置参数
params.setAppId("wx123456789");
params.setMchId("1234567890");
params.setKey("your_api_key");

// 设置Base64证书
String base64Cert = Base64.getEncoder().encodeToString("certificate content".getBytes());
params.setCert(base64Cert);
params.setApiClientCert(base64Cert);
params.setApiClientKey(base64Cert);

// 手动处理证书文件
params.processBase64CertFiles();

// 获取处理后的文件路径
System.out.println("证书路径: " + params.getCertPath());
System.out.println("证书文件路径: " + params.getApiClientCertPath());
System.out.println("私钥文件路径: " + params.getApiClientKeyPath());
```

## 错误处理

### 1. 无效的ifCode

```java
// 使用无效的ifCode
NormalMchParams params = NormalMchParams.factory("invalid", paramsStr);
// 返回null
assertNull(params);
```

### 2. 无效的JSON参数

```java
// 使用无效的JSON
NormalMchParams params = NormalMchParams.factory("wxpay", "invalid json");
// 返回null
assertNull(params);
```

### 3. 空参数

```java
// 使用空参数
NormalMchParams params = NormalMchParams.factory("wxpay", null);
// 返回null
assertNull(params);

NormalMchParams params2 = NormalMchParams.factory("wxpay", "");
// 返回null
assertNull(params2);
```

## 处理流程

### 1. factory方法流程

```java
public static NormalMchParams factory(String ifCode, String paramsStr) {
    try {
        // 1. 生成类名
        String className = capitalizeFirstAndThirdFromEnd(ifCode) + "NormalMchParams";
        
        // 2. 加载类
        Class<?> clazz = Class.forName(
            NormalMchParams.class.getPackage().getName() + "." + className);
        
        // 3. 创建实例
        NormalMchParams instance = (NormalMchParams) JSONObject.parseObject(paramsStr, clazz);
        
        // 4. 如果是微信支付参数，自动处理证书文件
        if (instance instanceof WxPayNormalMchParams) {
            WxPayNormalMchParams wxPayParams = (WxPayNormalMchParams) instance;
            wxPayParams.processBase64CertFiles();
            log.info("微信支付参数证书文件处理完成");
        }
        
        return instance;
    } catch (Exception e) {
        log.error("创建支付参数实例时发生错误", e);
        return null;
    }
}
```

### 2. 证书处理流程

```java
public void processBase64CertFiles() {
    // 1. 确保临时目录存在
    createTempDirectoryIfNotExists();
    
    // 2. 处理API证书文件
    if (StrUtil.isNotBlank(cert) && isBase64Content(cert)) {
        String certPath = saveBase64ToFile(cert, "apiclient_cert.p12", "cert");
        if (certPath != null) {
            this.cert = certPath;
        }
    }
    
    // 3. 处理证书文件
    if (StrUtil.isNotBlank(apiClientCert) && isBase64Content(apiClientCert)) {
        String certPath = saveBase64ToFile(apiClientCert, "apiclient_cert.pem", "apiClientCert");
        if (certPath != null) {
            this.apiClientCert = certPath;
        }
    }
    
    // 4. 处理私钥文件
    if (StrUtil.isNotBlank(apiClientKey) && isBase64Content(apiClientKey)) {
        String keyPath = saveBase64ToFile(apiClientKey, "apiclient_key.pem", "apiClientKey");
        if (keyPath != null) {
            this.apiClientKey = keyPath;
        }
    }
}
```

## 配置要求

### 1. 应用配置

确保在 `application.yml` 中配置了证书处理相关参数：

```yaml
bsin:
  payment:
    cert:
      temp-dir: /tmp/bsin/cert
      cache-expire-hours: 24
      enabled: true
```

### 2. Redis配置

确保Redis服务正常运行：

```yaml
spring:
  redis:
    host: localhost
    port: 6379
    database: 0
```

### 3. 目录权限

确保临时目录有正确的读写权限：

```bash
mkdir -p /tmp/bsin/cert
chmod 755 /tmp/bsin/cert
```

## 性能优化

### 1. 缓存机制

- 使用文件内容哈希值作为缓存键
- 避免重复转换相同内容
- 支持缓存过期时间配置

### 2. 文件管理

- 使用唯一文件名避免冲突
- 定期清理过期文件
- 设置合适的文件权限

### 3. 错误处理

- 完整的异常处理机制
- 详细的日志记录
- 优雅的降级处理

## 测试用例

### 1. 单元测试

```java
@Test
void testFactory_WithWxPayAndBase64Cert() {
    String base64Cert = Base64.getEncoder().encodeToString("test content".getBytes());
    String paramsStr = String.format(
        "{\"appId\":\"test_app_id\",\"cert\":\"%s\"}", base64Cert);
    
    NormalMchParams result = NormalMchParams.factory("wxpay", paramsStr);
    
    assertNotNull(result);
    assertTrue(result instanceof WxPayNormalMchParams);
    
    WxPayNormalMchParams wxPayParams = (WxPayNormalMchParams) result;
    assertNotEquals(base64Cert, wxPayParams.getCert());
    assertTrue(wxPayParams.getCert().contains("/tmp/bsin/cert/"));
}
```

### 2. 集成测试

```java
@Test
void testFactory_WithRealCertificate() {
    // 使用真实的证书文件进行测试
    String realCertBase64 = loadRealCertificateBase64();
    String paramsStr = String.format(
        "{\"appId\":\"test_app_id\",\"cert\":\"%s\"}", realCertBase64);
    
    NormalMchParams result = NormalMchParams.factory("wxpay", paramsStr);
    
    // 验证证书文件被正确保存
    WxPayNormalMchParams wxPayParams = (WxPayNormalMchParams) result;
    String certPath = wxPayParams.getCert();
    
    assertTrue(new File(certPath).exists());
    assertTrue(new File(certPath).canRead());
}
```

## 注意事项

### 1. 安全性

- 证书文件存储在临时目录，定期清理
- 设置严格的文件权限
- 避免在日志中输出敏感信息

### 2. 性能

- 大文件处理时注意内存使用
- 合理设置缓存过期时间
- 监控磁盘空间使用情况

### 3. 兼容性

- 支持Base64和文件路径两种格式
- 向后兼容现有代码
- 支持不同版本的证书文件

## 更新日志

- **2025-01-XX**: 初始版本，支持factory方法自动处理证书文件
- **2025-01-XX**: 添加带参数的构造函数支持
- **2025-01-XX**: 优化错误处理和日志记录
- **2025-01-XX**: 完善测试用例和文档