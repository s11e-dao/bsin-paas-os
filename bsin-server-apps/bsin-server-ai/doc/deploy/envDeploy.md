# bisn-server-env

#### 介绍
bsin-paas后台服务环境，seata、zookeepe、maven软件包



#### 启动说明
- 1.  idea中的maven配置
      ![](doc/maven_config.png)

- 2. 依赖库安装
~~~bash
cd scripts
bash installIpfsLibs.sh
bash installTronLibs.sh
~~~

- 3.  先启动zookeeper
>* windows
~~~bash
# 打开命令行或者powershell，进入 apache-zookeeper-3.6.1-bin/bin 目录，输入启动命令
./zkServer.sh start   
~~~

>* linux
~~~bash
# 打开终端，进入 apache-zookeeper-3.6.1-bin/bin 目录，输入启动命令
./zkServer.sh start   
~~~


- 4.  再启动seata
>* windows
~~~bash
# 打开终端，进入 seata-server-1.4.2/bin 目录，输入启动命令
./seata-server.bat -p 8091 -h 127.0.0.1
~~~

>* linux
~~~bash
# 打开终端，进入 seata-server-1.4.2/bin 目录，输入启动命令
./seata-server.sh -p 8091 -h 127.0.0.1

~~~



- 5.  或者运行一键启动seata和zookeeper脚本
~~~bash
mkdir -p log
nohup sh env_start.sh 2>log/env.log &
~~~

#### 服务器部署说明

- pem登录
~~~bash
chmod 600 ~/.ssh/leonard-sina-gateWay-1.pem
ssh-add -k ~/.ssh/leonard-sina-gateWay-1.pem
# 免密登录
ssh-copy-id -i ~/.ssh/id_rsa.pub bitnami@3.38.63.165
~~~




· 上传至服务器
~~~bash
sh uploadServer.sh bitnami@3.38.63.165
~~~
- 解压缩运行
~~~bash
tar -zxvf apache-zookeeper-3.6.1-bin.tar
tar -zxvf seata-server-1.4.2.tar
~~~




