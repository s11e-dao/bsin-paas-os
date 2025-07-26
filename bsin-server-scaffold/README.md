# BSIN Server Scaffold 微服务脚手架

## 📋 项目概述

BSIN Server Scaffold 是一个企业级微服务开发脚手架，提供多种通信协议的支持，包括 Dubbo、HTTP、WebSocket 和 MQTT，帮助开发者快速构建高质量的微服务应用。

## 🏗️ 架构特性

- 🚀 **多协议支持**：Dubbo、HTTP、WebSocket、MQTT
- 🏗️ **分层架构**：清晰的分层设计，职责分离
- 🔧 **开箱即用**：预配置的依赖和最佳实践
- 📦 **模块化设计**：支持独立开发和部署
- 🛡️ **企业级特性**：配置中心、服务注册、监控等

## 📁 项目结构

```
bsin-server-scaffold/
├── bsin-server-scaffold-dubbo/      # Dubbo 微服务脚手架
│   ├── scaffold-domain-dubbo/       # 领域模型层
│   ├── scaffold-facade-dubbo/       # 服务接口层
│   ├── scaffold-infrastructure-dubbo/ # 基础设施层
│   ├── scaffold-server-dubbo/       # 服务实现层
│   └── doc/                         # 开发文档
├── bsin-server-scaffold-http/       # HTTP 微服务脚手架
│   ├── src/                         # 源代码
│   ├── k8s/                         # Kubernetes配置
│   └── Dockerfile                   # 容器化配置
├── bsin-server-scaffold-websocket/  # WebSocket 微服务脚手架
│   ├── src/                         # 源代码
│   ├── doc/                         # 文档和示例
│   ├── k8s/                         # Kubernetes配置
│   └── Dockerfile                   # 容器化配置
├── bsin-server-scaffold-mqtt/       # MQTT 微服务脚手架
│   ├── src/                         # 源代码
│   ├── doc/                         # 文档和示例
│   └── README.md                    # MQTT使用说明
├── pom.xml                          # 父模块配置
└── README.md                        # 项目说明文档
```

## 🚀 快速开始

### 环境要求
- JDK 11+
- Maven 3.6+
- IDE (推荐 IntelliJ IDEA)

### 选择脚手架类型

| 脚手架类型 | 适用场景 | 特点 | 文档链接 |
|-----------|----------|------|----------|
| **Dubbo** | 微服务间通信 | 高性能RPC，服务治理 | [开发指南](./bsin-server-scaffold-dubbo/doc/README.md) |
| **HTTP** | RESTful API | 标准HTTP协议，易于集成 | 待完善 |
| **WebSocket** | 实时通信 | 双向通信，低延迟 | [示例文档](./bsin-server-scaffold-websocket/doc/) |
| **MQTT** | IoT设备通信 | 轻量级，支持QoS | [使用说明](./bsin-server-scaffold-mqtt/README.md) |

### 使用步骤

1. **选择脚手架**
   ```bash
   # 进入对应的脚手架目录
   cd bsin-server-scaffold-dubbo
   ```

2. **编译项目**
   ```bash
   mvn clean install
   ```

3. **启动服务**
   ```bash
   mvn spring-boot:run
   ```

## 🔧 技术栈

### 核心框架
- **Spring Boot** - 应用框架
- **Spring Cloud** - 微服务框架
- **Apache Dubbo** - RPC框架
- **Apache Shenyu** - API网关

### 基础设施
- **Nacos** - 服务注册与配置中心
- **RocketMQ** - 消息队列
- **Seata** - 分布式事务

### 开发工具
- **Maven** - 构建工具
- **Docker** - 容器化
- **Kubernetes** - 容器编排

## 🐛 已知问题

### Shenyu Sofa 调用问题
在 `shenyu-plugin-sofa` 模块中组装泛化请求的参数存在问题，已在网关调用中实现自定义解析：`CustomerSofaParamResolveServiceImpl`

## 📚 详细文档

- [Dubbo 脚手架开发指南](./bsin-server-scaffold-dubbo/doc/README.md)
- [WebSocket 示例文档](./bsin-server-scaffold-websocket/doc/)
- [MQTT 使用说明](./bsin-server-scaffold-mqtt/README.md)

## 🤝 贡献指南

欢迎提交 Issue 和 Pull Request 来改进这个项目！

### 提交规范
- 使用清晰的提交信息
- 遵循代码规范
- 添加必要的测试用例

## 📄 许可证

本项目采用 Apache License 2.0 许可证。

---

**最后更新**: 2024年12月  
**版本**: 3.0.0-SNAPSHOT
