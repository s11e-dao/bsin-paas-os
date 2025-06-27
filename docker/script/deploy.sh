#!/bin/sh

# 使用说明，用来提示输入参数
usage(){
	echo "Usage: sh deploy.sh [build|middleware|gateway|server_apps|ui_apps|start|stop|rm|clean|copy|app_agent|upms|waas|crm|brms|search|workflow|workflow_admin|iot]"
	exit 1
}

# 重新构建
build(){
	docker-compose stop
	docker-compose rm
	docker-compose build
}

# clean程序
clean(){
  ./script/package.sh
}

# mkdir程序
mkdir(){
  ./script/mkdir.sh
}

# copy程序
copy(){
  ./script/copy.sh
}
# 启动基础环境（必须）
middleware(){
	docker-compose up -d bsin-mysql-3.0 bsin-redis-3.0 bsin-nacos-standalone-3.0 bsin-emqx-3.0 #bsin-nginx-3.0 #bsin-rabbitmq-3.0 bsin-milvus-3.0
}

# 启动网关模块（必须）
gateway(){
	docker-compose up -d bsin-targe-gateway-admin-3.0 bsin-targe-gateway-3.0
}
# 启动server-apps模块
server_apps(){
	docker-compose up -d bsin-server-upms-3.0 bsin-server-waas-3.0 bsin-server-crm-3.0 bsin-server-app-agent-3.0 bsin-server-oms-3.0 bsin-server-community-3.0 bsin-server-brms-3.0 #bsin-server-iot-3.0 bsin-server-workflow-3.0 bsin-server-workflow-admin-3.0
}

# 启动upms模块
upms(){
	docker-compose up -d bsin-server-upms-3.0
}
# 启动crm模块
crm(){
	docker-compose up -d bsin-server-crm-3.0
}
# 启动waas模块
waas(){
	docker-compose up -d bsin-server-waas-3.0
}
# 启动waas模块
oms(){
  docker-compose up -d bsin-server-oms-3.0
}
# 启动community模块
community(){
  docker-compose up -d bsin-server-community-3.0
}
# 启动ai_agent模块
app_agent(){
	docker-compose up -d bsin-server-app-agent-3.0
}
# 启动brms模块
brms(){
	docker-compose up -d bsin-server-brms-3.0
}

# 启动workflow模块
workflow(){
	docker-compose up -d bsin-server-workflow-3.0
}
# 启动workflow-admin模块
workflow_admin(){
	docker-compose up -d bsin-server-workflow-admin-3.0
}
# 启动iot模块
iot(){
	docker-compose up -d bsin-server-iot-3.0
}
# 启动ui-apps模块
ui_apps(){
	docker-compose up -d bsin-apps-container-3.0 bsin-ui-upms-3.0 bsin-ui-ai-agent-3.0 bsin-ui-doc-3.0 bsin-ui-waas-3.0 bsin-ui-bigan-3.0 bsin-ui-data-warehouse-3.0 bsin-ui-decision-admin-3.0 bsin-ui-sea-condition-3.0
}
# 关闭所有环境/模块
stop(){
	docker-compose stop
}

# 删除所有环境/模块
rm(){
	docker-compose rm
}

# 根据输入参数，选择执行对应方法，不输入则执行使用说明
case "$1" in
"build")
	build
;;
"middleware")
	middleware
;;
"gateway")
	gateway
;;
"server_apps")
	server_apps
;;
"ai_agent")
	ai_agent
;;
"upms")
	upms
;;
"waas")
	waas
;;
"oms")
	oms
;;
"community")
	community
;;
"crm")
	crm
;;
"brms")
	brms
;;
"workflow")
	workflow
;;
"workflow_admin")
	workflow_admin
;;
"iot")
	iot
;;
"ui_apps")
	ui_apps
;;
"start")
	middleware
	gateway
	server_apps
	ui_apps
;;
"stop")
	stop
;;
"rm")
	rm
;;
"clean")
	clean
;;
"mkdir")
	mkdir
;;
"copy")
	copy
;;
*)
	usage
;;
esac
