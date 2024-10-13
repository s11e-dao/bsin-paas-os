#!/bin/bash
# 使用说明，用来提示输入参数
usage(){
	echo "Usage: sh package.sh [server_apps|ui_apps|start|stop|rm]"
	exit 1
}

echo "1. build frontend start..."

echo "1.1. build bsin-apps-container"
cd ../bsin-apps-container
yarn build

echo "1.2. build bsin-ui-ai-agent"
cd ../bsin-ui-apps/bsin-ui-ai-agent
yarn build

echo "1.3. build bsin-ui-decision-admin"
cd ../bsin-ui-decision-admin
yarn build

echo "1.4. build bsin-ui-upms"
cd ../bsin-ui-upms
yarn build

echo "1.5. build bsin-ui-waas"
cd ../bsin-ui-waas
yarn build

echo "2. build frontend finish!!!"

echo "3. build server start..."

cd ../../
mvn package -Dcheckstyle.skip=true -Dmaven.test.skip=true -Drat.skip=true -Denforcer.skip=true -Dmaven.javadoc.skip=true -Pdist,embedded-hbase-solr


echo "4. build server finish!!!"