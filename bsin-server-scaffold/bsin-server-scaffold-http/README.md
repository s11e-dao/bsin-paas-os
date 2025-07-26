# BSIN HTTP Scaffold 开发指南

## 📋 项目概述

BSIN HTTP Scaffold 是一个基于 Spring Boot 的 HTTP 微服务脚手架，提供 RESTful API 开发的标准模板和最佳实践。

## 🏗️ 架构特性

- 🚀 **RESTful API**：标准 HTTP 协议，易于集成
- 🏗️ **分层架构**：清晰的分层设计，职责分离
- 🔧 **开箱即用**：预配置的依赖和最佳实践
- 📦 **容器化支持**：提供 Docker 和 Kubernetes 配置
- 🛡️ **企业级特性**：配置中心、服务注册、监控等

## 📁 项目结构

```
bsin-server-scaffold-http/
├── src/                    # 源代码目录
├── k8s/                    # Kubernetes 配置
├── Dockerfile              # 容器化配置
└── pom.xml                 # Maven 配置
```

## 🚀 快速开始

### 环境要求
- JDK 11+
- Maven 3.6+
- IDE (推荐 IntelliJ IDEA)

### 使用步骤

1. **编译项目**
   ```bash
   mvn clean install
   ```

2. **启动服务**
   ```bash
   mvn spring-boot:run
   ```

3. **容器化部署**
   ```bash
   docker build -t bsin-http-scaffold .
   docker run -p 8080:8080 bsin-http-scaffold
   ```

## 🔧 技术栈

### 核心框架
- **Spring Boot** - 应用框架
- **Spring Web** - Web 开发
- **Spring Validation** - 参数校验

### 基础设施
- **Nacos** - 服务注册与配置中心
- **RocketMQ** - 消息队列

### 开发工具
- **Maven** - 构建工具
- **Docker** - 容器化
- **Kubernetes** - 容器编排

## 📚 开发指南

### 1. 创建 Controller

```java
@RestController
@RequestMapping("/api/v1")
@Validated
public class ExampleController {
    
    @GetMapping("/hello")
    public ResponseEntity<String> hello() {
        return ResponseEntity.ok("Hello World!");
    }
    
    @PostMapping("/data")
    public ResponseEntity<Result<DataDTO>> createData(@Valid @RequestBody DataDTO data) {
        // 业务逻辑
        return ResponseEntity.ok(Result.success(data));
    }
}
```

### 2. 参数校验

```java
@Data
public class DataDTO {
    @NotBlank(message = "名称不能为空")
    private String name;
    
    @NotNull(message = "年龄不能为空")
    @Min(value = 0, message = "年龄不能小于0")
    private Integer age;
}
```

### 3. 异常处理

```java
@ControllerAdvice
public class GlobalExceptionHandler {
    
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Result<String>> handleValidationException(MethodArgumentNotValidException e) {
        String message = e.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining(", "));
        return ResponseEntity.badRequest().body(Result.error("400", message));
    }
}
```

## 🐳 容器化部署

### Docker 部署

```bash
# 构建镜像
docker build -t bsin-http-scaffold .

# 运行容器
docker run -d -p 8080:8080 --name bsin-http bsin-http-scaffold
```

### Kubernetes 部署

```bash
# 部署到 Kubernetes
kubectl apply -f k8s/
```

## 📄 许可证

本项目采用 Apache License 2.0 许可证。

---

**最后更新**: 2024年12月  
**版本**: 3.0.0-SNAPSHOT 