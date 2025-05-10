#!/bin/sh

# 使用说明，用来提示输入参数
usage(){
	echo "Usage: sh deploy.sh [build|middleware|gateway|server_apps|ui_apps|start|stop|rm|clean|copy|ai_agent|upms|waas|crm|brms|search|workflow|workflow_admin|iot]"
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

# copy程序
copy(){
  ./script/copy.sh
}
# 启动基础环境（必须）
middleware(){
	docker-compose up -d bsin-mysql bsin-redis bsin-nacos-standalone bsin-emqx #bsin-nginx #bsin-rabbitmq bsin-milvus
}

# 启动网关模块（必须）
gateway(){
	docker-compose up -d bsin-targe-gateway-admin bsin-targe-gateway
}
# 启动server-apps模块
server_apps(){
	docker-compose up -d bsin-server-upms bsin-server-waas bsin-server-crm bsin-server-ai-agent bsin-server-oms bsin-server-community bsin-server-brms bsin-server-iot #bsin-server-workflow bsin-server-workflow-admin bsin-server-search
}

# 启动upms模块
upms(){
	docker-compose up -d bsin-server-upms
}
# 启动crm模块
crm(){
	docker-compose up -d bsin-server-crm
}
# 启动waas模块
waas(){
	docker-compose up -d bsin-server-waas
}
# 启动waas模块
oms(){
  docker-compose up -d bsin-server-oms
}
# 启动community模块
community(){
  docker-compose up -d bsin-server-community
}
# 启动ai_agent模块
ai_agent(){
	docker-compose up -d bsin-server-ai-agent
}
# 启动brms模块
brms(){
	docker-compose up -d bsin-server-brms
}
# 启动search模块
search(){
	docker-compose up -d bsin-server-search
}
# 启动workflow模块
workflow(){
	docker-compose up -d bsin-server-workflow
}
# 启动workflow-admin模块
workflow_admin(){
	docker-compose up -d bsin-server-workflow-admin
}
# 启动iot模块
iot(){
	docker-compose up -d bsin-server-iot
}
# 启动ui-apps模块
ui_apps(){
	docker-compose up -d bsin-apps-container bsin-ui-upms bsin-ui-ai-agent bsin-ui-waas bsin-ui-bigan bsin-ui-decision-admin bsin-ui-sea-condition
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
"search")
	search
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
"copy")
	copy
;;
*)
	usage
;;
esac
