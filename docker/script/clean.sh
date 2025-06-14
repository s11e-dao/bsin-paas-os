#!/bin/bash

# 删除jar文件
echo "1.start remove *.jar"
# apps
rm -f ./app-service/server-apps/app-agent/jar/app-agent-server-3.0.0-SNAPSHOT.jar
rm -f ./app-service/server-apps/brms/jar/brms-server-3.0.0-SNAPSHOT.jar
rm -f ./app-service/server-apps/crm/jar/crm-server-3.0.0-SNAPSHOT.jar
rm -f ./app-service/server-apps/http/jar/http-3.0.0-SNAPSHOT.jar
rm -f ./app-service/server-apps/upms/jar/upms-serve-3.0.0-SNAPSHOT.jar
rm -f ./app-service/server-apps/wass/jar/wass-server-3.0.0-SNAPSHOT.jar
rm -f ./app-service/server-apps/oms/jar/wass-server-3.0.0-SNAPSHOT.jar
rm -f ./app-service/server-apps/workflow/jar/workflow-serve-3.0.0-SNAPSHOT.jar
rm -f ./app-service/server-apps/workflow-admin/jar/workflow-admin-serve-3.0.0-SNAPSHOT.jar

# gateway
rm -f ./app-service/targe-gateway/jar/targe-gateway-admin-3.0.0-SNAPSHOT.jar

# gateway-admin
rm -f ./app-service/targe-gateway/jar/targe-gateway-admin-3.0.0-SNAPSHOT.jar


echo "2.remove *.jar finished!!!"

# 删除前端打包文件
echo "3.start remove frontend package"
rm -rf ./app-service/apps-container/dist/
rm -rf ./app-service/ui-apps/ai-agent/dist/
rm -rf ./app-service/ui-apps/bigan/dist/
rm -rf ./app-service/ui-apps/data-warehouse/dist/
rm -rf ./app-service/ui-apps/doc/dist/
rm -rf ./app-service/ui-apps/sea-condition/dist/
rm -rf ./app-service/ui-apps/upms/dist/
rm -rf ./app-service/ui-apps/waas/dist/

echo "2.remove frontend package finished!!!"