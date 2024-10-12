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


mkdir -p ./bsin-paas-os/server-apps/ai-agent/jar
mkdir -p ./bsin-paas-os/server-apps/brms/jar
mkdir -p ./bsin-paas-os/server-apps/crm/jar
mkdir -p ./bsin-paas-os/server-apps/http/jar
mkdir -p ./bsin-paas-os/server-apps/search/jar
mkdir -p ./bsin-paas-os/server-apps/upms/jar
mkdir -p ./bsin-paas-os/server-apps/waas/jar
mkdir -p ./bsin-paas-os/server-apps/workflow/jar
mkdir -p ./bsin-paas-os/server-apps/workflow-admin/jar

mkdir -p ./bsin-paas-os/targe-gateway/jar
mkdir -p ./bsin-paas-os/targe-gateway-admin/jar


mkdir -p ./bsin-paas-os/ui-apps/ai-agent/dist/
mkdir -p ./bsin-paas-os/ui-apps/decision-admin/dist/
mkdir -p ./bsin-paas-os/ui-apps/upms/dist/
mkdir -p ./bsin-paas-os/ui-apps/waas/dist/

echo "2. create dir finished!!!"



# 复制sql文件
echo "3. begin copy sql "
#cp ../sql/timeless_seckill.sql ./mysql/db
echo "4. end copy sql "

# 复制jar文件
echo "5. begin copy jar "

echo "5.1 begin copy targe-gateway-admin jar "
cp ../bsin-targe-gateway-admin/target/targe-gateway-admin-2.0.0-SNAPSHOT.jar ./bsin-paas-os/targe-gateway-admin/jar/targe-gateway-admin-server-2.0.0-SNAPSHOT.jar
cp ../bsin-targe-gateway-admin/target/targe-gateway-admin-2.0.0-SNAPSHOT-javadoc.jar ./bsin-paas-os/targe-gateway-admin/jar/targe-gateway-admin-javadoc-2.0.0-SNAPSHOT.jar
cp ../bsin-targe-gateway-admin/target/targe-gateway-admin-2.0.0-SNAPSHOT-sources.jar ./bsin-paas-os/targe-gateway-admin/jar/targe-gateway-admin-sources-2.0.0-SNAPSHOT.jar


echo "5.2 begin copy targe-gateway jar "
cp ../bsin-targe-gateway/target/targe-gateway-2.0.0-SNAPSHOT.jar ./bsin-paas-os/targe-gateway/jar/targe-gateway-server-2.0.0-SNAPSHOT.jar
cp ../bsin-targe-gateway/target/targe-gateway-2.0.0-SNAPSHOT-sources.jar ./bsin-paas-os/targe-gateway/jar/targe-gateway-sources-2.0.0-SNAPSHOT.jar

echo "5.3 begin copy server-apps"
cp ../bsin-server-apps/bsin-server-ai-agent/ai-agent-server/target/ai-agent-server-2.0.0-SNAPSHOT.jar ./bsin-paas-os/server-apps/ai-agent/jar/ai-agent-server-2.0.0-SNAPSHOT.jar
cp ../bsin-server-apps/bsin-server-brms/brms-server/target/brms-server-2.0.0-SNAPSHOT.jar ./bsin-paas-os/server-apps/brms/jar/brms-server-2.0.0-SNAPSHOT.jar
cp ../bsin-server-apps/bsin-server-crm/crm-server/target/crm-server-2.0.0-SNAPSHOT.jar ./bsin-paas-os/server-apps/crm/jar/crm-server-2.0.0-SNAPSHOT.jar
cp ../bsin-server-apps/bsin-server-http/target/bsin-server-http.jar ./bsin-paas-os/server-apps/http/jar/http-server-2.0.0-SNAPSHOT.jar
cp ../bsin-server-apps/bsin-server-search/search-server/target/search-server-2.0.0-SNAPSHOT.jar ./bsin-paas-os/server-apps/search/jar/search-server-2.0.0-SNAPSHOT.jar
cp ../bsin-server-apps/bsin-server-upms/upms-server/target/upms-server-2.0.0-SNAPSHOT.jar ./bsin-paas-os/server-apps/upms/jar/upms-server-2.0.0-SNAPSHOT.jar
cp ../bsin-server-apps/bsin-server-waas/waas-server/target/waas-server-2.0.0-SNAPSHOT.jar ./bsin-paas-os/server-apps/waas/jar/waas-server-2.0.0-SNAPSHOT.jar
cp ../bsin-server-apps/bsin-server-workflow/workflow-server/target/workflow-server-2.0.0-SNAPSHOT.jar ./bsin-paas-os/server-apps/workflow/jar/workflow-server-2.0.0-SNAPSHOT.jar
cp ../bsin-server-apps/bsin-server-workflow-admin/workflow-admin-server/target/workflow-admin-server-2.0.0-SNAPSHOT.jar ./bsin-paas-os/server-apps/workflow-admin/jar/workflow-admin-server-2.0.0-SNAPSHOT.jar

echo "6. end copy jar "


echo "7. begin copy frontend dist "
cp -r ../bsin-apps-container/dist/* ./bsin-paas-os/apps-container/dist/

cp -r ../bsin-ui-apps/bsin-ui-ai-agent/dist/* ./bsin-paas-os/ui-apps/ai-agent/dist/
cp -r ../bsin-ui-apps/bsin-ui-decision-admin/dist/* ./bsin-paas-os/ui-apps/decision-admin/dist/
cp -r ../bsin-ui-apps/bsin-ui-upms/dist/* ./bsin-paas-os/ui-apps/upms/dist/
cp -r ../bsin-ui-apps/bsin-ui-waas/dist/* ./bsin-paas-os/ui-apps/waas/dist/

echo "8. end copy frontend dist"

