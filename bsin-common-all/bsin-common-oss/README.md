# bsin-common-oss

bsin-pass 对象存储服务

  **文档版本**

| 版本号 | 修改日期       | 编写   | 修改内容                     | 备注 |
| ------ |------------| ------ | ---------------------------- | ---- |
| V1.0.0 | 2023/06/20 | leonard | 新建                         |      |



## 目录结构



## 启动注意事项  
- server.port: 


- jar包本地安装
>* 进入script目录，执行
~~~bash
sh installIpfsLibs.sh #安装ipfs lib
~~~

- 安装部署


## [IPFS](https://ipfs.docs.apiary.io/#reference/dht/put/cp)  
### http api
- 用于与IPFS节点交互的API，
- HTTPAPI目前接受所有方法，因此GET对任何组都可以像POST一样工作。
- 下面所示的方法是应该遵守的规范，尽管任何方法都可以奏效。有关更多信息，请[参阅此讨论](https://github.com/ipfs/go-ipfs/issues/2165)

#### add
添加文件或目录到IPFS

#### files
Manipulate unixfs files.Files is an API for manipulating ipfs objects as if they were a unix filesystem.Note: Most of the subcommands of 'ipfs files' accept the 'flush' option. It defaults to 'true'. Use caution when setting this to 'false'. It will improve performance for large numbers of file operations, but it does so at the cost of consistency guarantees.This command can't be called directly.
- mv
- mkdir
- cp
- ls



#### key
- list
显示本地所有密钥对

- gen
创建秘钥对


#### name
- publish
Publish an object to IPNS.
>* 使用ipfs name publish /ipfs/CID 发布以后，会覆盖当前节点CID
>* 使用 /ipns/节点CID访问

- resolve
Gets the value currently published at an IPNS name.


## IPFS应用
- 1、通过files/mkdir创建文件夹(demo)
- 2、通过key/gen为所创建的文件夹(demo)创建名称为demo的秘钥对，用于ipns发布
- 3、通过add添加文件1.txt至/ipfs，默认上传的文件都是在/ipfs/目录下
- 4、通过files/cp拷贝将步骤3上传的文件拷贝到步骤1创建的目录中(ipfs/cid --> /demo/1.txt)
- 5、通过name/publish将上传文件后的文件夹CID发布到步骤2创建key上

## 上传文件大小限制修改：
* 在BsinGatewayApplication类中配置

## 参考文档

https://x-file-storage.xuyanwu.cn/#/

# 阿里云OSS STS配置使用指南

## 概述

本模块提供了阿里云OSS STS（Security Token Service）的配置和使用功能，支持临时访问凭证的获取和管理。

## 功能特性

- 基于 `FileStorageProperties` 的统一配置管理
- 支持STS临时访问凭证获取
- 自动从OSS endpoint解析区域信息
- 完整的异常处理和日志记录
- 支持自定义策略文档

## 配置说明

### 1. 基础配置

在 `application.yml` 中添加以下配置：

```yaml
dromara:
  x-file-storage:
    default-platform: aliyun-oss-1
    aliyun-oss:
      - platform: aliyun-oss-1
        enable-storage: true
        access-key: ${ALIYUN_ACCESS_KEY}
        secret-key: ${ALIYUN_SECRET_KEY}
        end-point: oss-cn-hangzhou.aliyuncs.com
        bucket-name: ${ALIYUN_BUCKET_NAME}
        domain: https://${ALIYUN_BUCKET_NAME}.oss-cn-hangzhou.aliyuncs.com/
        base-path: upload/
        # STS配置
        sts:
          enabled: true  # 启用STS
          arn: acs:ram::${ALIYUN_ACCOUNT_ID}:role/${ALIYUN_ROLE_NAME}  # 角色ARN
          role-session-name: bsin-oss-session  # 角色会话名称
          policy: |  # 策略文档（可选）
            {
              "Version": "1",
              "Statement": [
                {
                  "Effect": "Allow",
                  "Action": [
                    "oss:PutObject",
                    "oss:GetObject",
                    "oss:DeleteObject"
                  ],
                  "Resource": [
                    "acs:oss:*:*:${ALIYUN_BUCKET_NAME}/*"
                  ]
                }
              ]
            }
          duration-seconds: 3600  # 临时凭证有效期（秒）
```

### 2. 环境变量配置

```bash
# 阿里云访问密钥
export ALIYUN_ACCESS_KEY=your_access_key_id
export ALIYUN_SECRET_KEY=your_access_key_secret

# 阿里云账户信息
export ALIYUN_ACCOUNT_ID=your_account_id
export ALIYUN_ROLE_NAME=your_role_name

# OSS存储桶
export ALIYUN_BUCKET_NAME=your_bucket_name
```

## 使用示例

### 1. 获取STS Token

```java
@Autowired
private AliOssStsConfig stsConfig;

public StsResponse getStsToken() {
    // 检查STS是否启用
    if (!stsConfig.isStsEnabled()) {
        throw new BusinessException("STS_NOT_ENABLED", "STS功能未启用");
    }
    
    // 获取STS配置参数
    String regionId = stsConfig.getRegionId();
    String accessKey = stsConfig.getAccessKey();
    String accessKeySecret = stsConfig.getAccessKeySecret();
    String arn = stsConfig.getArn();
    
    // 构建STS客户端并获取临时凭证
    // ... 具体实现见 UploadController.getStsToken()
}
```

### 2. 获取OSS上传Token

```java
public TokenResponse getOssToken() {
    // 获取STS临时凭证
    StsResponse stsResponse = getStsToken();
    
    // 构建OSS客户端
    String ossDomain = stsConfig.getOssDomain();
    OSS client = new OSSClientBuilder().build(
        ossDomain, 
        stsResponse.getAccessKeyId(), 
        stsResponse.getAccessKeySecret()
    );
    
    // 生成上传策略和签名
    // ... 具体实现见 UploadController.getOssToken()
}
```

## API接口

### 1. 获取STS Token

**接口地址：** `POST /upload/getStsToken`

**响应示例：**
```json
{
  "code": 0,
  "message": "success",
  "data": {
    "accessKeyId": "STS.xxx",
    "accessKeySecret": "xxx",
    "securityToken": "xxx",
    "expiration": "2024-01-01 12:00:00"
  }
}
```

### 2. 获取OSS上传Token

**接口地址：** `POST /upload/getOssToken`

**响应示例：**
```json
{
  "code": 0,
  "message": "success",
  "data": {
    "encodedPolicy": "xxx",
    "aliyunAccessKeyId": "STS.xxx",
    "signature": "xxx",
    "bucketPath": "https://bucket.oss-cn-hangzhou.aliyuncs.com/",
    "securityToken": "xxx"
  }
}
```

## 错误处理

### 常见错误码

| 错误码 | 说明 | 解决方案 |
|--------|------|----------|
| STS_NOT_ENABLED | STS功能未启用 | 检查配置中的 `sts.enabled` 是否为 `true` |
| STS_CONFIG_ERROR | STS配置错误 | 检查访问密钥、角色ARN等配置是否正确 |
| STS_TOKEN_ERROR | 获取STS token失败 | 检查网络连接和阿里云服务状态 |
| OSS_CONFIG_ERROR | OSS配置错误 | 检查域名、存储桶等配置是否正确 |

### 异常处理示例

```java
try {
    StsResponse response = getStsToken();
    // 处理成功响应
} catch (BusinessException e) {
    if ("STS_NOT_ENABLED".equals(e.getCode())) {
        // 处理STS未启用错误
        log.warn("STS功能未启用，请检查配置");
    } else if ("STS_CONFIG_ERROR".equals(e.getCode())) {
        // 处理配置错误
        log.error("STS配置错误：{}", e.getMessage());
    } else {
        // 处理其他错误
        log.error("获取STS token失败：{}", e.getMessage());
    }
}
```

## 最佳实践

### 1. 安全性建议

- 使用环境变量存储敏感信息（访问密钥、角色ARN等）
- 定期轮换访问密钥
- 为不同环境使用不同的角色和策略
- 设置最小权限原则的策略文档

### 2. 性能优化

- 缓存STS token，避免频繁请求
- 设置合理的token过期时间
- 监控STS服务调用频率和错误率

### 3. 监控和日志

- 记录STS token获取的日志
- 监控token过期时间
- 设置告警机制

## 相关文件

- `AliOssStsConfig.java` - STS配置类
- `FileStorageProperties.java` - 文件存储配置类
- `UploadController.java` - 上传控制器
- `StsResponse.java` - STS响应实体
- `TokenResponse.java` - OSS Token响应实体