#!/bin/bash

# 创建目标目录

echo "0. clean target dir start..."
bash clean.sh

# 复制sql文件
echo "3. begin copy sql "
#cp ../sql/timeless_seckill.sql ./mysql/db
echo "4. end copy sql "

# 复制jar文件
echo "5. begin copy jar "

echo "5.1 begin copy targe-gateway-admin jar "
cp -rvf ../bsin-targe-gateway-admin/target/targe-gateway-admin.jar ./bsin-paas-os/targe-gateway-admin/jar/targe-gateway-admin-3.0.0-SNAPSHOT.jar

echo "5.2 begin copy targe-gateway jar "
cp -rvf ../bsin-targe-gateway/target/targe-gateway.jar ./bsin-paas-os/targe-gateway/jar/targe-gateway-3.0.0-SNAPSHOT.jar

echo "5.3 begin copy server-apps"
cp -rvf ../bsin-server-apps/bsin-server-app-agent/app-agent-server/target/app-agent-server-3.0.0-SNAPSHOT.jar ./bsin-paas-os/server-apps/app-agent/jar/app-agent-server-3.0.0-SNAPSHOT.jar
cp -rvf ../bsin-server-apps/bsin-server-brms/brms-server/target/brms-server-3.0.0-SNAPSHOT.jar ./bsin-paas-os/server-apps/brms/jar/brms-server-3.0.0-SNAPSHOT.jar
cp -rvf ../bsin-server-apps/bsin-server-crm/crm-server/target/crm-server-3.0.0-SNAPSHOT.jar ./bsin-paas-os/server-apps/crm/jar/crm-server-3.0.0-SNAPSHOT.jar
#cp -rvf ../bsin-server-apps/bsin-server-file/target/bsin-server-file.jar ./bsin-paas-os/server-apps/file/jar/bsin-file-server-3.0.0-SNAPSHOT.jar
cp -rvf ../bsin-server-apps/bsin-server-upms/upms-server/target/upms-server-3.0.0-SNAPSHOT.jar ./bsin-paas-os/server-apps/upms/jar/upms-server-3.0.0-SNAPSHOT.jar
cp -rvf ../bsin-server-apps/bsin-server-waas/waas-server/target/waas-server-3.0.0-SNAPSHOT.jar ./bsin-paas-os/server-apps/waas/jar/waas-server-3.0.0-SNAPSHOT.jar
#cp -rvf ../bsin-server-apps/bsin-server-workflow/workflow-server/target/workflow-server-3.0.0-SNAPSHOT.jar ./bsin-paas-os/server-apps/workflow/jar/workflow-server-3.0.0-SNAPSHOT.jar
#cp -rvf ../bsin-server-apps/bsin-server-workflow-admin/workflow-admin-server/target/workflow-admin-server-3.0.0-SNAPSHOT.jar ./bsin-paas-os/server-apps/workflow-admin/jar/workflow-admin-server-3.0.0-SNAPSHOT.jar
#cp -rvf ../bsin-server-apps/bsin-server-iot/iot-server/target/iot-server-3.0.0-SNAPSHOT.jar ./bsin-paas-os/server-apps/iot/jar/iot-server-3.0.0-SNAPSHOT.jar


echo "5.3 begin copy .env files"
cp ../.env ./bsin-paas-os/server-apps/app-agent -rvf
cp ../.env ./bsin-paas-os/server-apps/brms -rvf
cp ../.env ./bsin-paas-os/server-apps/crm -rvf
cp ../.env ./bsin-paas-os/server-apps/upms -rvf
cp ../.env ./bsin-paas-os/server-apps/waas -rvf
cp ../.env ./bsin-paas-os/server-apps/workflow -rvf
cp ../.env ./bsin-paas-os/server-apps/workflow-admin -rvf
cp ../.env ./bsin-paas-os/server-apps/oms -rvf
cp ../.env ./bsin-paas-os/server-apps/iot -rvf
cp ../.env ./bsin-paas-os/targe-gateway-admin -rvf
cp ../.env ./bsin-paas-os/targe-gateway -rvf

## jiujiu-paas，业务闭源
#cp -rvf ../../jiujiu-paas-server/bsin-server-oms/oms-server/target/oms-server-3.0.0-SNAPSHOT.jar ./bsin-paas-os/server-apps/oms/jar/oms-server-3.0.0-SNAPSHOT.jar
#cp -rvf ../../jiujiu-paas-server/bsin-server-community/community-server/target/community-server-3.0.0-SNAPSHOT.jar ./bsin-paas-os/server-apps/community/jar/community-server-3.0.0-SNAPSHOT.jar

cp ../../.env ./bsin-paas-os/server-apps/community -rvf
cp ../../.env ./bsin-paas-os/server-apps/oms -rvf

echo "6. end copy jar "

echo "7. begin copy frontend dist "
cp -rvf  ../bsin-apps-container/dist/* ./bsin-paas-os/apps-container/dist/
cp -rvf  ../bsin-ui-apps/bsin-ui-ai-agent/dist/* ./bsin-paas-os/ui-apps/ai-agent/dist/
cp -rvf  ../bsin-ui-apps/bsin-ui-decision-admin/dist/* ./bsin-paas-os/ui-apps/decision-admin/dist/
cp -rvf  ../bsin-ui-apps/bsin-ui-upms/dist/* ./bsin-paas-os/ui-apps/upms/dist/
cp -rvf  ../bsin-ui-apps/bsin-ui-waas/dist/* ./bsin-paas-os/ui-apps/waas/dist/
cp -rvf  ../bsin-ui-apps/bsin-ui-data-warehouse/dist/* ./bsin-paas-os/ui-apps/data-warehouse/dist/

## jiujiu-paas，业务闭源
# cp -rvf  ../../jiujiu-paas/jiujiu-paas-ui/bsin-ui-bigan/dist/* ./bsin-paas-os/ui-apps/bigan/dist/

## 拷贝自定义项目脚本
## cp -r /home/leonard/ssd12/bsin-paas/prj/sea/bsin-ui-sea-condition/dist/* ./bsin-paas-os/ui-apps/sea-condition/dist/

echo "8. end copy frontend dist"

