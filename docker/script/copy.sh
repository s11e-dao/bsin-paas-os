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
mkdir -p ./app-service/server-apps/http/jar
mkdir -p ./app-service/server-apps/file/jar
mkdir -p ./app-service/server-apps/search/jar
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
mkdir -p ./app-service/ui-apps/doc/dist/
mkdir -p ./app-service/ui-apps/sea-condition/dist/
mkdir -p ./app-service/ui-apps/data-warehouse/dist/

echo "2. create dir finished!!!"

# 复制sql文件
echo "3. begin copy sql "
#cp ../sql/test.sql ../middleware/mysql/db
echo "4. end copy sql "

# 复制jar文件
echo "5. begin copy jar "

echo "5.1 begin copy targe-gateway-admin jar "
cp -rvf ../bsin-targe-gateway-admin/target/bsin-targe-gateway-admin-3.0.0-SNAPSHOT.jar ./app-service/targe-gateway-admin/jar/bsin-targe-gateway-admin-3.0.0-SNAPSHOT.jar

echo "5.2 begin copy targe-gateway jar "
cp -rvf ../bsin-targe-gateway/target/bsin-targe-gateway-3.0.0-SNAPSHOT.jar ./app-service/targe-gateway/jar/bsin-targe-gateway-3.0.0-SNAPSHOT.jar

echo "5.3 begin copy server-apps"
cp -rvf ../bsin-server-apps/bsin-server-app-agent/app-agent-server/target/app-agent-server-3.0.0-SNAPSHOT.jar ./app-service/server-apps/app-agent/jar/app-agent-server-3.0.0-SNAPSHOT.jar
cp -rvf ../bsin-server-apps/bsin-server-brms/brms-server/target/brms-server-3.0.0-SNAPSHOT.jar ./app-service/server-apps/brms/jar/brms-server-3.0.0-SNAPSHOT.jar
cp -rvf ../bsin-server-apps/bsin-server-crm/crm-server/target/crm-server-3.0.0-SNAPSHOT.jar ./app-service/server-apps/crm/jar/crm-server-3.0.0-SNAPSHOT.jar
cp -rvf ../bsin-server-apps/bsin-server-file/target/file-server-3.0.0-SNAPSHOT.jar ./app-service/server-apps/file/jar/file-server-3.0.0-SNAPSHOT.jar
cp -rvf ../bsin-server-apps/bsin-server-upms/upms-server/target/upms-server-3.0.0-SNAPSHOT.jar ./app-service/server-apps/upms/jar/upms-server-3.0.0-SNAPSHOT.jar
cp -rvf ../bsin-server-apps/bsin-server-waas/waas-server/target/waas-server-3.0.0-SNAPSHOT.jar ./app-service/server-apps/waas/jar/waas-server-3.0.0-SNAPSHOT.jar
cp -rvf ../bsin-server-apps/bsin-server-workflow/workflow-server/target/workflow-server-3.0.0-SNAPSHOT.jar ./app-service/server-apps/workflow/jar/workflow-server-3.0.0-SNAPSHOT.jar
cp -rvf ../bsin-server-apps/bsin-server-workflow-admin/workflow-admin-server/target/workflow-admin-server-3.0.0-SNAPSHOT.jar ./app-service/server-apps/workflow-admin/jar/workflow-admin-server-3.0.0-SNAPSHOT.jar
cp -rvf ../bsin-server-apps/bsin-server-iot/iot-server/target/iot-server-3.0.0-SNAPSHOT.jar ./app-service/server-apps/iot/jar/iot-server-3.0.0-SNAPSHOT.jar


echo "5.3 begin copy .env files"
cp ../.env ./app-service/server-apps/app-agent -rvf
cp ../.env ./app-service/server-apps/brms -rvf
cp ../.env ./app-service/server-apps/crm -rvf
cp ../.env ./app-service/server-apps/upms -rvf
cp ../.env ./app-service/server-apps/waas -rvf
cp ../.env ./app-service/server-apps/workflow -rvf
cp ../.env ./app-service/server-apps/workflow-admin -rvf
cp ../.env ./app-service/server-apps/oms -rvf
cp ../.env ./app-service/server-apps/community -rvf
cp ../.env ./app-service/server-apps/file -rvf
cp ../.env ./app-service/server-apps/http -rvf
cp ../.env ./app-service/server-apps/iot -rvf
cp ../.env ./app-service/targe-gateway-admin -rvf
cp ../.env ./app-service/targe-gateway -rvf


echo "5.3 begin copy .sql files"

cp ../bsin-targe-gateway-admin/script/bsin-shenyu-gateway.sql ./middleware/mysql/db/ -rvf
cp ../bsin-server-apps/bsin-server-app-agent/script/bsin-ai.sql ./middleware/mysql/db/ -rvf
cp ../bsin-server-apps/bsin-server-brms/script/bsin-brms.sql ./middleware/mysql/db/ -rvf
cp ../bsin-server-apps/bsin-server-crm/script/bsin-crm.sql ./middleware/mysql/db/ -rvf
cp ../bsin-server-apps/bsin-server-file/script/bsin-file.sql ./middleware/mysql/db/ -rvf
cp ../bsin-server-apps/bsin-server-iot/script/bsin-iot.sql ./middleware/mysql/db/ -rvf
cp ../bsin-server-apps/bsin-server-upms/script/bsin-upms.sql ./middleware/mysql/db/ -rvf
cp ../bsin-server-apps/bsin-server-waas/script/bsin-waas.sql ./middleware/mysql/db/ -rvf
cp ../bsin-server-apps/bsin-server-workflow/script/bsin-workflow.sql ./middleware/mysql/db/ -rvf
cp ../bsin-server-apps/bsin-server-workflow-admin/script/bsin-workflow-admin.sql ./middleware/mysql/db/ -rvf



##
cp -rvf /home/leonard/ssd12/bsin-paas/prj/sea/bsin-ui-sea-condition/dist/* ./app-service/ui-apps/sea-condition/dist/

## jiujiu-paas，业务闭源
cp -rvf ../../jiujiu-paas-3.0/jiujiu-paas-server/bsin-server-oms/oms-server/target/oms-server-3.0.0-SNAPSHOT.jar ./app-service/server-apps/oms/jar/oms-server-3.0.0-SNAPSHOT.jar
cp -rvf ../../jiujiu-paas-3.0/jiujiu-paas-server/bsin-server-community/community-server/target/community-server-3.0.0-SNAPSHOT.jar ./app-service/server-apps/community/jar/community-server-3.0.0-SNAPSHOT.jar

cp ../../.env ./app-service/server-apps/community -rvf
cp ../../.env ./app-service/server-apps/oms -rvf

echo "6. end copy jar "

echo "7. begin copy frontend dist "
cp -rvf  ../bsin-apps-container/dist/* ./app-service/apps-container/dist/
cp -rvf  ../bsin-ui-apps/bsin-ui-ai-agent/dist/* ./app-service/ui-apps/ai-agent/dist/
cp -rvf  ../bsin-ui-apps/bsin-ui-decision-admin/dist/* ./app-service/ui-apps/decision-admin/dist/
cp -rvf  ../bsin-ui-apps/bsin-ui-upms/dist/* ./app-service/ui-apps/upms/dist/
cp -rvf  ../bsin-ui-apps/bsin-ui-waas/dist/* ./app-service/ui-apps/waas/dist/
cp -rvf  ../bsin-ui-apps/bsin-ui-data-warehouse/dist/* ./app-service/ui-apps/data-warehouse/dist/
cp -rvf  ../bsin-paas-doc/dist/* ./app-service/ui-apps/doc/dist/

## jiujiu-paas，业务闭源
 cp -rvf  ../../jiujiu-paas-3.0/jiujiu-paas-ui/bsin-ui-bigan/dist/* ./app-service/ui-apps/bigan/dist/

## 拷贝自定义项目脚本
## cp -r /home/leonard/ssd12/bsin-paas/prj/sea/bsin-ui-sea-condition/dist/* ./app-service/ui-apps/sea-condition/dist/

echo "8. end copy frontend dist"

