# BSIN WebSocket Scaffold 开发指南

## 📋 项目概述

BSIN WebSocket Scaffold 是一个基于 Spring Boot 的 WebSocket 微服务脚手架，提供实时双向通信的开发模板和最佳实践。

## 🏗️ 架构特性

- 🚀 **实时通信**：基于 WebSocket 的双向通信
- 🏗️ **低延迟**：支持高并发实时数据传输
- 🔧 **开箱即用**：预配置的 WebSocket 支持
- 📦 **容器化支持**：提供 Docker 和 Kubernetes 配置
- 🛡️ **企业级特性**：连接管理、消息路由、监控等

## 📁 项目结构

```
bsin-server-scaffold-websocket/
├── src/                    # 源代码目录
├── doc/                    # 文档和示例
│   └── ws-demo.html        # WebSocket 测试页面
├── k8s/                    # Kubernetes 配置
├── Dockerfile              # 容器化配置
└── pom.xml                 # Maven 配置
```

## 🚀 快速开始

### 环境要求
- JDK 11+
- Maven 3.6+
- IDE (推荐 IntelliJ IDEA)
- 现代浏览器（支持 WebSocket）

### 使用步骤

1. **编译项目**
   ```bash
   mvn clean install
   ```

2. **启动服务**
   ```bash
   mvn spring-boot:run
   ```

3. **测试连接**
   - 打开浏览器访问：`http://localhost:8080/doc/ws-demo.html`
   - 或者使用 WebSocket 客户端工具连接：`ws://localhost:8080/ws`

## 🔧 技术栈

### 核心框架
- **Spring Boot** - 应用框架
- **Spring WebSocket** - WebSocket 支持
- **STOMP** - 消息传递协议

### 基础设施
- **Nacos** - 服务注册与配置中心
- **Redis** - 会话存储（可选）

### 开发工具
- **Maven** - 构建工具
- **Docker** - 容器化
- **Kubernetes** - 容器编排

## 📚 开发指南

### 1. WebSocket 配置

```java
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
    
    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/topic", "/queue");
        config.setApplicationDestinationPrefixes("/app");
    }
    
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws")
                .setAllowedOrigins("*")
                .withSockJS();
    }
}
```

### 2. 消息处理器

```java
@Controller
public class WebSocketController {
    
    @MessageMapping("/hello")
    @SendTo("/topic/greetings")
    public Greeting greeting(HelloMessage message) {
        return new Greeting("Hello, " + message.getName() + "!");
    }
    
    @MessageMapping("/private")
    @SendToUser("/queue/reply")
    public PrivateMessage privateMessage(PrivateMessage message) {
        return new PrivateMessage("Private reply to: " + message.getContent());
    }
}
```

### 3. 消息实体类

```java
@Data
public class HelloMessage {
    private String name;
}

@Data
public class Greeting {
    private String content;
    
    public Greeting(String content) {
        this.content = content;
    }
}
```

### 4. 客户端连接示例

```javascript
// 使用 SockJS 和 STOMP
var socket = new SockJS('/ws');
var stompClient = Stomp.over(socket);

stompClient.connect({}, function (frame) {
    console.log('Connected: ' + frame);
    
    // 订阅广播消息
    stompClient.subscribe('/topic/greetings', function (greeting) {
        console.log(JSON.parse(greeting.body).content);
    });
    
    // 订阅私有消息
    stompClient.subscribe('/user/queue/reply', function (message) {
        console.log(JSON.parse(message.body).content);
    });
});

// 发送消息
stompClient.send("/app/hello", {}, JSON.stringify({'name': 'World'}));
```

## 🐳 容器化部署

### Docker 部署

```bash
# 构建镜像
docker build -t bsin-websocket-scaffold .

# 运行容器
docker run -d -p 8080:8080 --name bsin-websocket bsin-websocket-scaffold
```

### Kubernetes 部署

```bash
# 部署到 Kubernetes
kubectl apply -f k8s/
```

## 📖 测试工具

### 1. 浏览器测试
项目提供了 `doc/ws-demo.html` 测试页面，可以直接在浏览器中测试 WebSocket 功能。

### 2. 命令行测试
```bash
# 使用 wscat 工具测试
npm install -g wscat
wscat -c ws://localhost:8080/ws
```

### 3. Postman 测试
Postman 支持 WebSocket 测试，可以创建 WebSocket 请求进行测试。

## 🔍 监控和调试

### 1. 连接监控
```java
@Component
public class WebSocketEventListener {
    
    @EventListener
    public void handleWebSocketConnectListener(SessionConnectedEvent event) {
        log.info("Received a new web socket connection");
    }
    
    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        log.info("User disconnected: " + event.getSessionId());
    }
}
```

### 2. 消息日志
```java
@MessageMapping("/log")
public void logMessage(String message) {
    log.info("Received message: {}", message);
}
```

## 📄 许可证

本项目采用 Apache License 2.0 许可证。

---

**最后更新**: 2024年12月  
**版本**: 3.0.0-SNAPSHOT 