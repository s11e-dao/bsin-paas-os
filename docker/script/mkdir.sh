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

mkdir -p ./bsin-paas-os/server-apps/app-agent/jar
mkdir -p ./bsin-paas-os/server-apps/brms/jar
mkdir -p ./bsin-paas-os/server-apps/crm/jar
#mkdir -p ./bsin-paas-os/server-apps/file/jar
mkdir -p ./bsin-paas-os/server-apps/upms/jar
mkdir -p ./bsin-paas-os/server-apps/waas/jar
mkdir -p ./bsin-paas-os/server-apps/workflow/jar
mkdir -p ./bsin-paas-os/server-apps/workflow-admin/jar
mkdir -p ./bsin-paas-os/server-apps/oms/jar
mkdir -p ./bsin-paas-os/server-apps/community/jar
mkdir -p ./bsin-paas-os/server-apps/iot/jar

mkdir -p ./bsin-paas-os/targe-gateway/jar
mkdir -p ./bsin-paas-os/targe-gateway-admin/jar

mkdir -p ./bsin-paas-os/apps-container/dist/
mkdir -p ./bsin-paas-os/ui-apps/ai-agent/dist/
mkdir -p ./bsin-paas-os/ui-apps/decision-admin/dist/
mkdir -p ./bsin-paas-os/ui-apps/upms/dist/
mkdir -p ./bsin-paas-os/ui-apps/waas/dist/
mkdir -p ./bsin-paas-os/ui-apps/bigan/dist/
mkdir -p ./bsin-paas-os/ui-apps/sea-condition/dist/
mkdir -p ./bsin-paas-os/ui-apps/data-warehouse/dist/

echo "2. create dir finished!!!"


