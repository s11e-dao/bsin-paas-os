#!/bin/bash

# 创建目标目录

echo "0. clean target dir start..."
bash clean.sh

echo "1. create dir start..."
mkdir -p ./middleware/mysql/db
mkdir -p ./middleware/mysql/logs
mkdir -p ./middleware/mysql/data
mkdir -p ./middleware/milvus/logs
mkdir -p ./middleware/nacos/logs
mkdir -p ./middleware/rabbitmq/logs
mkdir -p ./middleware/redis/logs

mkdir -p ./app-service/server-apps/app-agent/jar
mkdir -p ./app-service/server-apps/brms/jar
mkdir -p ./app-service/server-apps/crm/jar
#mkdir -p ./app-service/server-apps/file/jar
mkdir -p ./app-service/server-apps/upms/jar
mkdir -p ./app-service/server-apps/waas/jar
mkdir -p ./app-service/server-apps/workflow/jar
mkdir -p ./app-service/server-apps/workflow-admin/jar
mkdir -p ./app-service/server-apps/oms/jar
mkdir -p ./app-service/server-apps/community/jar
mkdir -p ./app-service/server-apps/iot/jar

mkdir -p ./app-service/targe-gateway/jar
mkdir -p ./app-service/targe-gateway-admin/jar

mkdir -p ./app-service/apps-container/dist/
mkdir -p ./app-service/ui-apps/ai-agent/dist/
mkdir -p ./app-service/ui-apps/decision-admin/dist/
mkdir -p ./app-service/ui-apps/upms/dist/
mkdir -p ./app-service/ui-apps/waas/dist/
mkdir -p ./app-service/ui-apps/bigan/dist/
mkdir -p ./app-service/ui-apps/sea-condition/dist/
mkdir -p ./app-service/ui-apps/data-warehouse/dist/

echo "2. create dir finished!!!"


