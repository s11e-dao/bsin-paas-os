# Seata 分布式事务服务

## 概述

Seata 是一款开源的分布式事务解决方案，致力于在微服务架构下提供高性能和简单易用的分布式事务服务。

本配置使用官方的 `seataio/seata-server:2.5.0` 镜像，具有以下优势：
- ✅ 官方维护，稳定可靠
- ✅ 镜像体积更小，启动更快
- ✅ 配置更简洁，维护成本低
- ✅ 及时获得安全更新和 bug 修复

## 配置说明

### 服务信息
- **容器名称**: `bsin-seata-3.0`
- **基础镜像**: `seataio/seata-server:2.5.0` (官方镜像)
- **构建镜像**: `bsin-seata:3.0.0`
- **端口**: `8091`
- **IP地址**: `172.28.0.5`

### 依赖服务
- MySQL: `bsin-mysql-3.0:3306`
- Nacos: `bsin-nacos-standalone-3.0:8848`

### 存储模式
- **模式**: 数据库存储 (db)
- **数据库**: MySQL
- **数据库名**: `bsin-seata`

## 目录结构

```
middleware/seata/
├── dockerfile          # Seata 镜像构建文件
├── conf/               # 配置文件目录
│   ├── registry.conf   # 注册中心和配置中心配置
│   └── file.conf       # Seata 服务配置
├── scripts/            # 脚本目录
│   └── init-seata.sh   # 数据库初始化脚本
├── logs/               # 日志目录
└── README.md           # 说明文档
```

## 快速启动

### 1. 使用脚本启动
```bash
cd /path/to/docker
./scripts/start-seata.sh
```

### 2. 使用 Docker Compose
```bash
# 启动依赖服务
docker-compose up -d bsin-mysql-3.0 bsin-nacos-standalone-3.0

# 启动 Seata 服务
docker-compose up -d bsin-seata-3.0
```

## 健康检查

```bash
# 检查服务状态
curl http://localhost:8091/health

# 查看日志
docker logs -f bsin-seata-3.0

# 检查注册中心
# 访问 Nacos 控制台: http://localhost:8848/nacos
# 默认用户名/密码: nacos/nacos
```

## 数据库表结构

Seata 会自动在 `bsin-seata` 数据库中创建以下表：
- `global_table`: 全局事务表
- `branch_table`: 分支事务表
- `lock_table`: 全局锁表
- `undo_log`: 回滚日志表

各业务数据库也会自动创建 `undo_log` 表用于 AT 模式的事务回滚。

## 配置参数

### 环境变量
- `SEATA_PORT`: 服务端口 (默认: 8091)
- `STORE_MODE`: 存储模式 (db)
- `STORE_DB_URL`: 数据库连接地址
- `SEATA_REGISTRY_TYPE`: 注册中心类型 (nacos)
- `SEATA_REGISTRY_NACOS_SERVER_ADDR`: Nacos 地址

### JVM 参数
- `-Xmx2048m -Xms2048m`: 堆内存设置
- `-XX:+UseG1GC`: 使用 G1 垃圾收集器
- `-Duser.timezone=Asia/Shanghai`: 时区设置

## 在应用中使用

### 1. 添加依赖
```xml
<dependency>
    <groupId>io.seata</groupId>
    <artifactId>seata-spring-boot-starter</artifactId>
    <version>1.4.2</version>
</dependency>
```

### 2. 配置文件
```yaml
seata:
  enabled: true
  application-id: your-application-name
  tx-service-group: your-tx-group
  registry:
    type: nacos
    nacos:
      application: seata-server
      server-addr: bsin-nacos-standalone-3.0:8848
      group: SEATA_GROUP
  config:
    type: nacos
    nacos:
      server-addr: bsin-nacos-standalone-3.0:8848
      group: SEATA_GROUP
```

### 3. 使用注解
```java
@GlobalTransactional
public void businessMethod() {
    // 分布式事务逻辑
}
```

## 故障排查

### 常见问题
1. **服务启动失败**
   - 检查 MySQL 和 Nacos 是否正常运行
   - 查看容器日志: `docker logs bsin-seata-3.0`

2. **注册中心连接失败**
   - 检查 Nacos 服务状态
   - 验证网络连接

3. **数据库连接失败**
   - 确认 MySQL 服务正常
   - 检查数据库用户权限

### 日志位置
- 容器日志: `docker logs bsin-seata-3.0`
- 应用日志: `./logs/seata_gc.log`

## 监控和管理

### Prometheus 指标
- 端口: 9898 (如果启用)
- 路径: `/metrics`

### 管理端点
- 健康检查: `http://localhost:8091/health`

## 版本信息
- Seata 版本: 2.5.0
- 基础镜像: `seataio/seata-server:2.5.0`
- JDK 版本: 17 (内置于官方镜像)

### 可用的官方镜像版本
```bash
# 推荐使用的稳定版本
seataio/seata-server:2.5.0    # 当前使用 (最新稳定版)
seataio/seata-server:1.6.1    # 较旧稳定版
seataio/seata-server:1.5.2    # 较旧稳定版

# 查看所有可用版本
docker search seataio/seata-server
```

### 版本 2.5.0 的新特性
- ✨ 更好的性能优化
- 🛡️ 增强的安全特性
- 🔧 改进的配置管理
- 📈 更完善的监控指标

### 切换版本
如需切换版本，只需修改 `dockerfile` 第 2 行：
```dockerfile
FROM seataio/seata-server:2.5.0  # 当前版本
```
