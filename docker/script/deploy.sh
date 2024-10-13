#!/bin/sh

# 使用说明，用来提示输入参数
usage(){
	echo "Usage: sh deploy.sh [build|middleware|gateway|server_apps|ui_apps|start|stop|rm]"
	exit 1
}

# 重新构建
build(){
#  ./script/package.sh
#  ./script/copy.sh
	docker-compose stop
	docker-compose rm
	docker-compose build
}


# 启动基础环境（必须）
middleware(){
	docker-compose up -d bsin-mysql bsin-redis bsin-nacos-standalone #bsin-rabbitmq bsin-milvus
}

# 启动网关模块（必须）
gateway(){
	docker-compose up -d bsin-targe-gateway-admin bsin-targe-gateway
}
# 启动server-apps模块
server_apps(){
	docker-compose up -d bsin-server-upms bsin-server-waas bsin-server-crm bsin-server-ai-agent
}

# 启动ui-apps模块
ui_apps(){
	docker-compose up -d bsin-apps-container bsin-ui-upms bsin-ui-ai-agent bsin-ui-waas #bsin-ui-decision-admin
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
*)
	usage
;;
esac
