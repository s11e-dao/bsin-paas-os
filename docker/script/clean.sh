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
rm -f ./app-service/server-apps/wass/jar/wass-server-3.0.0-SNAPSHOT.jar.original
rm -f ./app-service/server-apps/workflow/jar/workflow-serve-3.0.0-SNAPSHOT.jar
rm -f ./app-service/server-apps/workflow-admin/jar/workflow-admin-serve-3.0.0-SNAPSHOT.jar

# gateway
rm -f ./app-service/targe-gateway/jar/targe-gateway-admin-3.0.0-SNAPSHOT.jar

# gateway-admin
rm -f ./app-service/targe-gateway/jar/targe-gateway-admin-3.0.0-SNAPSHOT.jar


echo "2.remove *.jar finished!!!"

# 删除前端打包文件
echo "3.start remove frontend package"


echo "2.remove frontend package finished!!!"