# 部署
## 准备
### 硬件配置
- aws账号(FireFox浏览器记住了密码)：
leijiwu001@sina.com
BL&xd7370628
- visa虚拟卡： 4367970131207949 771 2026年12月

- 2个lightsail服务
* 2GB RAM 2个vCPU 60GB SSD
* 用户名：bitnami
* 资源分配如下
*
| 服务器名称     | 公有 IPv4       | 私有 IPv4       | 开放端口                                                                                     | 部署服务                                            |
|-----------|---------------|---------------|------------------------------------------------------------------------------------------|-------------------------------------------------|
| gateWay-1 | 3.38.63.165   | 172.26.11.100 | 22、80、443、8097、8092、8000、8003、8013、2181(zookeeper) 9191(zookeeperServerPort) seata(8091) | nginx,gateWay,upms,apps-container,ui-upms,ui-ai |
| gateWay-2 | 43.200.152.71 | 172.26.3.233  | 22、80、443、80、6379、19530、8072、8126、8125、19530(milvus)                                     | redis,milvus,crm,market,ai                      |


*
| 服务器名称   | rpc.rest.port | rpc.bolt.port | server.port |  
|---------|---------------|---------------|-------------| 
| market  | 8344          | 12204         | 8083        | 
| crm     | 8342          | 12202         | 8072        | 
| ai      | 8126          | 12215         | 8349        | 
| upms    | 8418          | 12290         | 8092        | 
| gateway | 8659          | 12279         | 8097        | 
  

- 域名及证书
*
| 域名                      | 解析           | 证书  | 服务                   | 备注                 |
|-------------------------|--------------|-----|----------------------|--------------------|
| copilot.s11edao.com     | 3.38.63.165  | 无   | apps-container(8000) | 基座                 | 
| copilotupms.s11edao.com | 3.38.63.165  | 无   | ui-upms(8003)        | 权限管理，同一个服务器，暂时不用解析 | 
| ai.s11edao.com          | 43.200.152.71  | 无   | ui-ai(8013)          | 微服务，暂时不用解析         | 
| wechat.s11edao.com      | 43.200.152.71  | 无   | ai(8125)             | 同一个服务器，暂时不用解析      | 
| wxportal.s11edao.com    | 43.200.152.71  | 带证书 | ai(8126)             | ai工程，用于微信公众号访问     |  

 
### 系统
- ubuntu
* 交换分区设置
~~~bash
# 首先，使用 dd 命令或 fallocate 命令创建一个大小为 1GB 的交换文件。fallocate 命令比 dd 更快，但并非在所有系统上都可用。以下是使用 fallocate 的示例命令：
sudo fallocate -l 1G /swapfile
# 如果 fallocate 不可用，可以使用 dd 命令：
sudo dd if=/dev/zero of=/swapfile bs=1M count=1024
# 设置正确的权限，出于安全原因，交换文件应该只能被 root 用户读写。设置权限：
sudo chmod 600 /swapfile
# 设置交换空间，使用 mkswap 命令将文件设置为交换空间：
sudo mkswap /swapfile
# 启用交换空间，使用 swapon 命令启用交换空间：
sudo swapon /swapfile
# 使交换永久生效，为了在系统重启后保持交换设置，需要编辑 /etc/fstab 文件。打开这个文件：
sudo vim /etc/fstab
# 然后添加以下行：
/swapfile none swap sw 0 0
# 使用top命令检查
~~~
* ifconfig
~~~bash
sudo apt install net-tools
whereis ifconfig
sudo ln -s /usr/sbin/ifconfig /usr/bin

~~~
* jdk11安装
~~~bash
apt-cache search java11
sudo apt-get install openjdk-11-jdk
vi /etc/profile
# 末尾添加以下
export JAVA_HOME=/usr/lib/jvm/java-11-openjdk-amd64
~~~

* nginx安装
~~~bash
# install
sudo apt install nginx -y
# 查看nginx服务是否启动
sudo systemctl status nginx
# 查看版本
nginx -v
# 检查 配置文件
sudo nginx -t
# 停止服务
sudo nginx -s stop
# 状态
sudo systemctl status nginx

# 重新加载配置文件
sudo nginx -s reload

#
-bash: nginx: command not found
# 查看nginx路径
whereis nginx
nginx: /usr/sbin/nginx /usr/lib/nginx /etc/nginx /usr/share/nginx /usr/share/man/man8/nginx.8.gz
# 方案一
vim /etc/profile
# 添加
PATH=$PATH:/usr/sbin/nginx
source /etc/profile

# 方案二
sudo ln -s /usr/sbin/nginx /usr/bin

nginx -s reload


# 若80端口占用，则
sudo netstat -anp | grep 80
kill 占用端口的进程
# 网页端重启实例

# 413 Request Entity Too Large -- 修改nginx配置文件
client_max_body_size 200m; # 改为你需要的大小!正确配置参考：
~~~

* redis 部署在 gateWay-1上
~~~bash
whereis redis-server
redis-server: /opt/bitnami/redis/bin/redis-server
# lightsail自带redis不知道在哪里配置，直接kill
sudo netstat -anp | grep 6379
kill 占用端口的进程
# 重新安装
sudo apt install redis-server
sudo systemctrl start redis-server
# 配置
sudo vim /etc/redis/redis.conf
# 在打开的配置文件中找到以下行，修改 @ requirepass foobared 为
requirepass 123456

# 修改redis配置文件/etc/redis.conf 修改bind  假设你的redis服务器地址： 3.38.63.165
bind 127.0.0.1 3.38.63.165 # 不行
直接注释掉上一句即可
# 重启
sudo systemctl restart redis-server.service 
~~~

* milvus 部署在 gateWay-2上： 43.200.152.71
~~~bash 
# 安装docke
# 安装 Docker
curl -fsSL https://get.docker.com -o get-docker.sh
sudo sh get-docker.sh

To run Docker as a non-privileged user, consider setting up the
Docker daemon in rootless mode for your user:

    dockerd-rootless-setuptool.sh install

Visit https://docs.docker.com/go/rootless/ to learn about rootless mode.


To run the Docker daemon as a fully privileged service, but granting non-root
users access, refer to https://docs.docker.com/go/daemon-access/

WARNING: Access to the remote API on a privileged Docker daemon is equivalent
         to root access on the host. Refer to the 'Docker daemon attack surface'
         documentation for details: https://docs.docker.com/go/attack-surface/



# Docker 更换配置国内镜像，可不配
vim /etc/docker/daemon.json
{
    "registry-mirrors": [
        "https://hub-mirror.c.163.com",
        "https://mirror.baidubce.com",
        "https://dockerproxy.com",
        "https://docker.nju.edu.cn"
    ]
}
sudo systemctl daemon-reload
sudo systemctl restart docker
# 开机自动启动docker
systemctl enable docker

# Docker Compose 安装
# 1、首先前往 https://github.com/docker/compose/releases/latest 查看最新的 docker-compose 版本号，比如截稿时最新版本为 2.23.0。

# 2、下载最新版本的 docker-compose，你需要将下面的 2.23.0 替换成最新的版本号
sudo curl -L "https://github.com/docker/compose/releases/download/2.24.5/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose
# 下载不了直接使用 wget 链接
# 3、授予可执行权限
sudo chmod +x /usr/local/bin/docker-compose

# 4、测试是否安装成功（可能需要重启系统）
docker-compose -v
# 安装成功会显示 docker-compose 版本


# 下载milvus
cd ~/copilot/env/
wget https://github.com/milvus-io/milvus/releases/download/v2.3.7/milvus-standalone-docker-compose.yml -O docker-compose.yml

docker-compose up 
# permission denied while trying to connect to the Docker daemon socket 
# 添加docker group
sudo groupadd docker
# groupadd: group 'docker' already exists 忽略


# 将用户’username’加到docker group中, -aG：-a在-G存在的情况下，增加次要用户组的支持，而不是修改当前用户组。
sudo usermod -aG docker username
# 重启session，上述操作之后，需要重新开一个session，用户再次登录之后，就可以不用sudo来执行docker相关的命令了。

~~~

### 基础环境 
- zookeeper和seata
上传至服务器: /home/bitnami/copilot/env
~~~bash
# 找到bisn-server-env
cd bisn-server-env
# 同步上传zookeeper和seata
sh sync_seata_zookeeper.sh bitnami@3.88.104.155
# zookeeper启动
bash ./apache-zookeeper-3.6.1-bin/bin/zkServer.sh start

# seata启动 
# windows
./seata-server.bat -p 8091 -h 127.0.0.1 -m file
# linux
./seata-server.sh -p 8091 -h 127.0.0.1 -m file &
~~~

### 应用程序
- 1、后台应用程序全部上传至服务器: ~/bsinCopilot/server
~~~bash
targe-gateway-server-1.0.0-SNAPSHOT.jar
upms-server-1.0.0-SNAPSHOT.jar
market-server-2.0.0-SNAPSHOT.jar
crm-server-2.0.0-SNAPSHOT.jar
~~~
- 2、前端应用程序全部上传至服务器: ~/bsinCopilot/front
~~~bash
# 端口8000，绑定域名 copilot.s11edao.com
bsin-apps-container

# 端口8003
bsin-ui-upms

# 端口8013
bsin-ui-ai
~~~

- 2、启动
* 2.1、bsin-server-targe-gateway 网关启动
~~~bash
# 端口：8097，绑定域名 copilotGateway.s11edao.com
nohup java -jar -Xms1024m -Xmx2048m  -XX:PermSize=512M -XX:MaxPermSize=1024m targe-gateway-server-1.0.0-SNAPSHOT.jar >/logs/targe-gateway-server.log &
~~~

* 2.2、bsin-server-upms 权限管理启动
~~~bash
# 端口：8092
nohup java -jar -Xms1024m -Xmx2048m  -XX:PermSize=512M -XX:MaxPermSize=1024m upms-server-1.0.0-SNAPSHOT.jar >/logs/upms-server.log &
~~~

* 2.3、bsin-server-market jiujiu的market启动
~~~bash
# 端口：8083
nohup java -jar -Xms1024m -Xmx2048m  -XX:PermSize=512M -XX:MaxPermSize=1024m market-server-2.0.0-SNAPSHOT.jar >/logs/market-server.log &
~~~


* 2.4、bsin-server-crm jiujiu的CRM服务启动
~~~bash
# 端口：8072
nohup java -jar -Xms1024m -Xmx2048m  -XX:PermSize=512M -XX:MaxPermSize=1024m crm-server-2.0.0-SNAPSHOT.jar >/logs/crm-server.log &
~~~

* 2.4、bsin-server-crm ai服务启动
~~~bash
# 端口：8126  微信公众号使用，需要https的域名  wxportal.s11edao.com 
nohup java -jar -Xms1024m -Xmx2048m  -XX:PermSize=512M -XX:MaxPermSize=1024m crm-server-2.0.0-SNAPSHOT.jar >/logs/crm-server.log &
~~~

* 2.3、ai-go-wechat 启动
~~~bash
# 端口： 8025
nohup ./ai-go-wechat >./logs/ai-go-wechat.log &
~~~



## 异常解决：
- RPC调用异常
~~~
com.alipay.sofa.rpc.core.exception.SofaRouteException: RPC-020020009: The service addresses [bolt://:12204?version=1.0&accepts=100000&appName=bsin-server-market&weight=100&language=java&pid=10045&interface=me.flyray.bsin.facade.service.MerchantService&timeout=0&serialization=hessian2&protocol=bolt&delay=-1&dynamic=true&startTime=1707187948473&id=rpc-cfg-22&uniqueId=1.0&rpcVer=50803,] of service [me.flyray.bsin.facade.service.MerchantService:1.0:1.0] is not available,or specify url not exist in providers 
# 修改网卡配置，查看服务器网卡名称，改为: ens5
ifconfig 
ens5: flags=4163<UP,BROADCAST,RUNNING,MULTICAST>  mtu 9001
        inet 172.26.11.100  netmask 255.255.240.0  broadcast 172.26.15.255
        inet6 fe80::82:4cff:fe5d:d078  prefixlen 64  scopeid 0x20<link>
        inet6 2406:da12:f65:d500:f75b:d1df:6419:7e0c  prefixlen 128  scopeid 0x0<global>
        ether 02:82:4c:5d:d0:78  txqueuelen 1000  (Ethernet)
        RX packets 823163  bytes 1097117223 (1.0 GiB)
        RX errors 0  dropped 0  overruns 0  frame 0
        TX packets 203182  bytes 18376581 (17.5 MiB)
        TX errors 0  dropped 0 overruns 0  carrier 0  collisions 0

lo: flags=73<UP,LOOPBACK,RUNNING>  mtu 65536
        inet 127.0.0.1  netmask 255.0.0.0
        inet6 ::1  prefixlen 128  scopeid 0x10<host>
        loop  txqueuelen 1000  (Local Loopback)
        RX packets 37644  bytes 2685554 (2.5 MiB)
        RX errors 0  dropped 0  overruns 0  frame 0
        TX packets 37644  bytes 2685554 (2.5 MiB)
        TX errors 0  dropped 0 overruns 0  carrier 0  collisions 0

~~~

- 跨域請求
~~~
# nginx 跨域配置
set $cors_origin $http_origin;
set $cors_cred   true;
add_header Access-Control-Allow-Origin      $cors_origin;
add_header Access-Control-Allow-Credentials $cors_cred;
add_header Access-Control-Allow-Headers    'Origin, X-Requested-With, Content-Type, Accept';#服务端可以接收的header
add_header Access-Control-Allow-Methods     'GET,POST,OPTIONS';
add_header Access-Control-Allow-Credentials, true;#服务端接收认证信息，如cookie
~~~