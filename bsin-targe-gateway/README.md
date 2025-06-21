# BSIN Target Gateway

## 启动说明

### 方式1：使用启动脚本（推荐）
```bash
./start.sh
```

### 方式2：手动设置环境变量
```bash
export BSIN_NACOS_SERVER_ADDR=172.28.0.11:8848
export BSIN_NACOS_USERNAME=nacos
export BSIN_NACOS_PASSWORD=nacos
export BSIN_WEBSOCKET_HOST_PORT=172.28.0.11:9095
java -jar target/bsin-targe-gateway-3.0.0-SNAPSHOT.jar
```

### 方式3：直接启动（已修改配置文件）
```bash
java -jar target/bsin-targe-gateway-3.0.0-SNAPSHOT.jar
```

## 故障排除

### Nacos连接问题
如果遇到以下错误：
```
Connection refused: /192.168.1.6:9848
Server check fail, please check server 192.168.1.6 ,port 9848 is available
```

**解决方案：**
1. 确保Docker容器中的Nacos服务正在运行
2. 使用正确的IP地址：`172.28.0.11:8848`
3. 检查网络连接：`telnet 172.28.0.11 8848`

### 检查服务状态
```bash
# 检查Nacos容器状态
docker ps | grep nacos

# 检查Nacos容器IP
docker inspect bsin-nacos-standalone-3.0 | grep IPAddress

# 测试连接
telnet 172.28.0.11 8848
telnet 172.28.0.11 9848
```

## 配置说明

- **Nacos服务器地址**: 172.28.0.11:8848
- **WebSocket地址**: 172.28.0.11:9095
- **应用端口**: 9195

# 介绍
[toc]

## Bsin-PaaS
引入shenyu网关
### 启动步骤
- 1.启动BsinGatewayApplication
- 2.启动ShenyuAdminBootstrap
- 3.启动UpmsDubboApplication
- 4.启动
