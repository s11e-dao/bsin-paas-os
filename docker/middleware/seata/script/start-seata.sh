#!/bin/bash

# Seata 服务启动脚本
echo "=========================================="
echo "启动 Seata 分布式事务服务"
echo "=========================================="

# 检查 Docker 是否运行
if ! docker info > /dev/null 2>&1; then
    echo "❌ Docker 服务未运行，请先启动 Docker"
    exit 1
fi

# 切换到 docker 目录
cd "$(dirname "$0")/.."

echo "📋 检查必要的依赖服务..."

# 检查 MySQL 服务是否正在运行
if ! docker ps | grep -q "bsin-mysql-3.0"; then
    echo "⚠️  MySQL 服务未运行，正在启动..."
    docker-compose up -d bsin-mysql-3.0
    echo "⏳ 等待 MySQL 服务启动..."
    sleep 30
fi

# 检查 Nacos 服务是否正在运行
if ! docker ps | grep -q "bsin-nacos-standalone-3.0"; then
    echo "⚠️  Nacos 服务未运行，正在启动..."
    docker-compose up -d bsin-nacos-standalone-3.0
    echo "⏳ 等待 Nacos 服务启动..."
    sleep 30
fi

echo "🚀 启动 Seata 服务..."
docker-compose up -d bsin-seata-3.0

echo "⏳ 等待 Seata 服务启动..."
sleep 20

# 检查服务状态
if docker ps | grep -q "bsin-seata-3.0"; then
    echo "✅ Seata 服务启动成功！"
    echo ""
    echo "服务信息："
    echo "  - 容器名称: bsin-seata-3.0"
    echo "  - 端口: 8091"
    echo "  - 健康检查: http://localhost:8091/health"
    echo "  - 注册中心: Nacos (bsin-nacos-standalone-3.0:8848)"
    echo "  - 数据存储: MySQL (bsin-mysql-3.0:3306/bsin-seata)"
    echo ""
    echo "查看日志命令:"
    echo "  docker logs -f bsin-seata-3.0"
    echo ""
    echo "停止服务命令:"
    echo "  docker-compose stop bsin-seata-3.0"
else
    echo "❌ Seata 服务启动失败！"
    echo ""
    echo "查看错误日志:"
    echo "  docker logs bsin-seata-3.0"
    exit 1
fi
