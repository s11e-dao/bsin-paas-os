#!/bin/bash

# 删除jar文件
echo "1.start remove *.jar"
# apps
rm -f ../bsin-paas-os/server-apps/aiAgent/jar/aiAgent-server-2.0.0-SNAPSHOT.jar
rm -f ../bsin-paas-os/server-apps/brms/jar/brms-server-2.0.0-SNAPSHOT.jar
rm -f ../bsin-paas-os/server-apps/crm/jar/crm-server-2.0.0-SNAPSHOT.jar
rm -f ../bsin-paas-os/server-apps/http/jar/http-2.0.0-SNAPSHOT.jar
rm -f ../bsin-paas-os/server-apps/search/jar/search-serve-2.0.0-SNAPSHOT.jar
rm -f ../bsin-paas-os/server-apps/upms/jar/upms-serve-2.0.0-SNAPSHOT.jar
rm -f ../bsin-paas-os/server-apps/wass/jar/wass-server-2.0.0-SNAPSHOT.jar
rm -f ../bsin-paas-os/server-apps/wass/jar/wass-server-2.0.0-SNAPSHOT.jar.original
rm -f ../bsin-paas-os/server-apps/workflow/jar/workflow-serve-2.0.0-SNAPSHOT.jar
rm -f ../bsin-paas-os/server-apps/workflow-admin/jar/workflow-admin-serve-2.0.0-SNAPSHOT.jar

# gateway
rm -f ../bsin-paas-os/targe-gateway/jar/shenyu-bootstrap.jar
rm -f ../bsin-paas-os/targe-gateway/jar/shenyu-bootstrap-sources.jar

# gateway-admin
rm -f ../bsin-paas-os/targe-gateway-admin/jar/shenyu-admin.jar
rm -f ../bsin-paas-os/targe-gateway-admin/jar/shenyu-admin-javadoc.jar
rm -f ../bsin-paas-os/targe-gateway-admin/jar/shenyu-admin-sources.jar

echo "2.remove *.jar finished!!!"

# 删除前端打包文件
echo "3.start remove frontend package"


echo "2.remove frontend package finished!!!"