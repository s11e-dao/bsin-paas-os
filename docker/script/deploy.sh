#!/bin/sh

# 使用说明，用来提示输入参数
usage(){
	echo "Usage: sh 执行脚本.sh [middleware|gateway|server_apps|ui_apps|all|stop|rm]"
	exit 1
}

# 启动基础环境（必须）
middleware(){
	docker-compose up -d bsin-mysql bsin-redis bsin-nacos #bsin-rabbitmq bsin-milvus
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
#	docker-compose up -d
  echo "not support!"
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
