#!/bin/sh

# 使用说明，用来提示输入参数
usage(){
	echo "Usage: sh deploy.sh [build|middleware|gateway|server_apps|ui_apps|ali_ai_studio|start|stop|rm|clean|copy|app_agent|upms|waas|crm|brms|search|workflow|workflow_admin|iot|elasticsearch|rocketmq]"
	exit 1
}

# 创建 .env 文件并修复权限
setup_environment(){
	# 创建 .env 文件
	cat > .env << EOF
# User ID and Group ID for containers
UID=$(id -u)
GID=$(id -g)

# Timezone
TZ=Asia/Shanghai

# Default values
MIDDLEWARE_HOME=.
EOF

	# 修复权限问题
	echo "Fixing permissions for data directories..."
	chmod -R 777 middleware/elasticsearch/data 2>/dev/null || true
	chmod -R 777 middleware/kibana/data 2>/dev/null || true
	chmod -R 777 middleware/rocketmq/store 2>/dev/null || true
	chmod -R 777 middleware/mysql/data 2>/dev/null || true
	chmod -R 777 middleware/redis/data 2>/dev/null || true
	
	echo "Created .env file with UID=$(id -u) and GID=$(id -g)"
}

# 重新构建
build(){
	setup_environment
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
	setup_environment
	sudo docker-compose up -d bsin-mysql-3.0 bsin-redis-3.0 
	sleep 20
	sudo docker-compose up -d bsin-nacos-standalone-3.0 bsin-emqx-3.0 bsin-elasticsearch-3.0 bsin-elasticsearch-3.0-head #bsin-elasticsearch-3.0-kibana
	sleep 2
	rocketmq
	sleep 20
	sudo docker-compose up -d bsin-seata-3.0 #bsin-nginx-3.0 #bsin-rabbitmq-3.0 bsin-milvus-3.0
}

# 启动es环境
elasticsearch(){
	setup_environment
	sudo docker-compose up -d bsin-elasticsearch-3.0  bsin-elasticsearch-3.0-head #bsin-elasticsearch-3.0-kibana
}

# 启动rocketmq环境
rocketmq(){
	setup_environment
	sudo docker-compose up -d bsin-rocketmq-namesrv-3.0 bsin-rocketmq-broker-3.0 bsin-rocketmq-init-topic-3.0 bsin-rocketmq-dashboard-3.0 bsin-rocketmq-proxy-3.0 
}

# 启动网关模块（必须）
gateway(){
	setup_environment
	docker-compose up -d bsin-targe-gateway-admin-3.0 bsin-targe-gateway-3.0
}

# 启动server-apps模块
server_apps(){
	setup_environment
	docker-compose up -d bsin-server-upms-3.0 bsin-server-waas-3.0 bsin-server-crm-3.0 bsin-server-app-agent-3.0 bsin-server-oms-3.0 bsin-server-community-3.0 bsin-server-brms-3.0 bsin-server-golang-3.0 bsin-server-mpc-client1-3.0 bsin-server-mpc-client2-3.0 bsin-server-mpc-client3-3.0 bsin-server-mpc-client4-3.0 bsin-server-ali-ai-studio-3.0 #bsin-server-iot-3.0 bsin-server-workflow-3.0 bsin-server-workflow-admin-3.0
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
# 启动golang模块
golang(){
	docker-compose up -d bsin-server-golang-3.0
}
# 启动mpc-client模块
mpc_client(){
	docker-compose up -d bsin-server-mpc-client1-3.0 bsin-server-mpc-client2-3.0 bsin-server-mpc-client3-3.0 bsin-server-mpc-client4-3.0
}

ali_ai_studio(){
	middleware
	docker-compose up -d bsin-server-ali-ai-studio-3.0 bsin-ui-ali-ai-studio-3.0
}

# 启动ui-apps模块
ui_apps(){
	setup_environment
	docker-compose up -d bsin-apps-container-3.0 bsin-ui-upms-3.0 bsin-ui-ai-agent-3.0 bsin-ui-doc-3.0 bsin-ui-waas-3.0 bsin-ui-bigan-3.0 bsin-ui-data-warehouse-3.0 bsin-ui-decision-admin-3.0 bsin-ui-sea-condition-3.0 bsin-ui-ali-ai-studio-3.0
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
"elasticsearch")
	elasticsearch
;;
"rocketmq")
	rocketmq
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
"golang")
	golang
;;
"mpc_client")
	golang
;;
"ali_ai_studio")
	ali_ai_studio
;;
"mpc_client")
	golang
	mpc_client
;;
"ali_ai_studio")
	ali_ai_studio
;;
"ui_apps")
	ui_apps
;;
"start")
	setup_environment
	middleware
	# sleep 20
	gateway
	sleep 20
	server_apps
	sleep 20
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
