#!/bin/bash
###
 # @Author: leonard
 # @Date: 2024-10-03 15:18:47
 # @LastEditors: leonard 1287279970@qq.com
 # @LastEditTime: 2024-11-18 20:42:26
 # @FilePath: /bsin-paas-os/docker/script/package.sh
 # @Description: 
 # 
 # Copyright (c) 2024 by CBD Technology CO., Ltd, All Rights Reserved. 
### 
# 使用说明，用来提示输入参数
usage(){
	echo "Usage: sh package.sh [server_apps|ui_apps|all]"
	exit 1
}

all(){
  ui_apps
  server_apps
}

ui_apps(){
echo "1. build frontend start..."
echo "1.1. build bsin-apps-container"
cd ../bsin-apps-container
echo ${PWD}
yarn
yarn build

echo "1.2. build bsin-ui-ai-agent"
cd ../bsin-ui-apps/bsin-ui-ai-agent
echo ${PWD}
yarn
yarn build

echo "1.3. build bsin-ui-decision-admin"
echo ${PWD}
cd ../bsin-ui-decision-admin
yarn
yarn build

echo "1.4. build bsin-ui-upms"
echo ${PWD}
cd ../bsin-ui-upms
yarn
yarn build

echo "1.5. build bsin-ui-waas"
echo ${PWD}
cd ../bsin-ui-waas
yarn
yarn build
echo "2. build frontend finish!!!"


echo "1.6. build bsin-ui-bigan"
echo ${PWD}
cd ../../jiujiu-paas-yue17/jiujiu-paas-ui/bsin-ui-bigan/
yarn
yarn build

echo "2. build frontend finish!!!"
}

server_apps(){
echo "3. build server start..."
#cd ../../
cd ../
mvn package -Dcheckstyle.skip=true -Dmaven.test.skip=true -Drat.skip=true -Denforcer.skip=true -Dmaven.javadoc.skip=true -Pdist,embedded-hbase-solr
echo "4. build server finish!!!"
}

# 根据输入参数，选择执行对应方法，不输入则执行使用说明
case "$1" in
"all")
	all
;;
"server_apps")
	server_apps
;;
"ui_apps")
	ui_apps
;;
*)
	usage
;;
esac