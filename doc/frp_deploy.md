
## FRP


## FRPS 部署
- 登录云服务器，下载并解压
~~~bash
wget https://github.com/fatedier/frp/releases/download/v0.51.3/frp_0.51.3_linux_amd64.tar.gz
tar -zxvf frp_0.51.3_linux_amd64.tar.gz
mv frp_0.51.3_linux_amd64 /usr/local/frp
# 修改配置文件
vim /usr/local/frp/frps.ini
# 写入以下内容：
# 配置项的分类，通常使用 [common] 表示
[common]
# 指定frps服务端绑定的网口IP地址。如有网口1和网口2，一般使用 0.0.0.0 表示绑定所有可用的网络接口
bind_addr = 0.0.0.0
# 指定frps服务端监听的端口号(该端口需要防火墙或安全组放行)，用于接收来自外部客户端的连接，可修改
bind_port = 7000

# 用于验证客户端连接的令牌。客户端需要使用相同的令牌来与服务端建立连接，可修改
token = 12356789

# 当您需要进行HTTP转发时，可以指定用于虚拟主机的HTTP端口
vhost_http_port = 80
# 当您需要进行HTTPS转发时，可以指定用于虚拟主机的HTTPS端口
vhost_https_port = 443

# 指定frps的仪表盘（dashboard）的访问端口(该端口需要防火墙或安全组放行)，用于通过Web界面进行服务端的管理和监控
dashboard_port = 7500
# 仪表盘的用户名，可以自定义
dashboard_user = admin
# 仪表盘的密码，可以自定义
dashboard_pwd = your password


# 调试时，可先注释日志，直接打印到控制台
# 指定日志文件的路径和名称。默认情况下，日志会输出到控制台，但您可以将日志保存到文件中方便查看和分析
log_file = ./frps.log
# 日志级别，可选值为 trace, debug, info, warn, error
log_level = info
# 保留日志文件的最大天数
log_max_days = 1

~~~

- 开机自启 service
~~~bash
vim /etc/systemd/system/frps.service
# 写入以下内容
[Unit]
Description=Frp Server Service
After=network.target

[Service]
Type=simple
User=root
Group=root
ExecStart=/usr/local/frp/frps -c /usr/local/frp/frps.ini
Restart=on-failure

[Install]
WantedBy=multi-user.target 
# 加载配置文件
systemctl daemon-reload
# 使能开机自启
systemctl enable frps
# 启动服务
systemctl start frps
# 查看服务状态
systemctl status frps
~~~

## FRPC 配置
- 本地配置
~~~bash
wget https://github.com/fatedier/frp/releases/download/v0.51.3/frp_0.51.3_linux_amd64.tar.gz
tar -zxvf frp_0.51.3_linux_amd64.tar.gz
sudo mv frp_0.51.3_linux_amd64 /usr/local/frp
# 修改配置文件
sudo vim /usr/local/frp/frpc.ini
# 写入以下内容：
[common]
#frps公网ip地址
server_addr = 47.100.16.156
#frps端口   
server_port = 7000    
#服务端认证密码
token = 12356789    
tls_enable = true

[leonard-honeywell-ssh]  
type = tcp
local_ip = 127.0.0.1
# linux远程连接服务端口
local_port = 22
#远程的时候链接的端
remote_port = 6022    
 
[bsin-server-targe-gateway]
type = tcp
local_ip = 127.0.0.1
# linux远程连接服务端口
local_port = 9195
#远程的时候链接的端
remote_port = 9195    


[bsin-server-targe-gateway-admin]  
type = tcp
local_ip = 127.0.0.1
# linux远程连接服务端口
local_port = 9095
#远程的时候链接的端
remote_port = 9095


[bsin-server-upms]
type = tcp
local_ip = 127.0.0.1
# linux远程连接服务端口
local_port = 9101
#远程的时候链接的端
remote_port = 9101


[bsin-server-crm]  
type = tcp
local_ip = 127.0.0.1
# linux远程连接服务端口
local_port = 9102
#远程的时候链接的端
remote_port = 9102

[bsin-server-ai-brms]  
type = tcp
local_ip = 127.0.0.1
# linux远程连接服务端口
local_port = 9103
#远程的时候链接的端
remote_port = 9103


[bsin-server-ai-agent]  
type = tcp
local_ip = 127.0.0.1
# linux远程连接服务端口
local_port = 9104
#远程的时候链接的端
remote_port = 9104


[bsin-server-ai-workflow]  
type = tcp
local_ip = 127.0.0.1
# linux远程连接服务端口
local_port = 9105
#远程的时候链接的端
remote_port = 9105


[bsin-server-ai-workflow-admin]  
type = tcp
local_ip = 127.0.0.1
# linux远程连接服务端口
local_port = 9106
#远程的时候链接的端
remote_port = 9106


[bsin-server-waas]  
type = tcp
local_ip = 127.0.0.1
# linux远程连接服务端口
local_port = 9107
#远程的时候链接的端
remote_port = 9107


[bsin-server-http]  
type = tcp
local_ip = 127.0.0.1
# linux远程连接服务端口
local_port = 9108
#远程的时候链接的端
remote_port = 9108


[bsin-server-search]  
type = tcp
local_ip = 127.0.0.1
# linux远程连接服务端口
local_port = 9109
#远程的时候链接的端
remote_port = 9109



[bsin-server-community]  
type = tcp
local_ip = 127.0.0.1
# linux远程连接服务端口
local_port = 9110
#远程的时候链接的端
remote_port = 9110



[bsin-server-oms]  
type = tcp
local_ip = 127.0.0.1
# linux远程连接服务端口
local_port = 9111
#远程的时候链接的端
remote_port = 9111


[bsin-server-iot]  
type = tcp
local_ip = 127.0.0.1
# linux远程连接服务端口
local_port = 9120
#远程的时候链接的端
remote_port = 9120

[bsin-apps-container]  
type = tcp
local_ip = 127.0.0.1
# linux远程连接服务端口
local_port = 8000
#远程的时候链接的端
remote_port = 8000

[bsin-ui-upms]  
type = tcp
local_ip = 127.0.0.1
# linux远程连接服务端口
local_port = 8001
#远程的时候链接的端
remote_port = 8001

[bsin-ui-waas]  
type = tcp
local_ip = 127.0.0.1
# linux远程连接服务端口
local_port = 8002
#远程的时候链接的端
remote_port = 8002

[bsin-ui-decision-admin]  
type = tcp
local_ip = 127.0.0.1
# linux远程连接服务端口
local_port = 8003
#远程的时候链接的端
remote_port = 8003


[bsin-ui-ai-agent]  
type = tcp
local_ip = 127.0.0.1
# linux远程连接服务端口
local_port = 8004
#远程的时候链接的端
remote_port = 8004

[bsin-ui-decision-erp]  
type = tcp
local_ip = 127.0.0.1
# linux远程连接服务端口
local_port = 8005
#远程的时候链接的端
remote_port = 8005

[bsin-ui-merchant-bigan]  
type = tcp
local_ip = 127.0.0.1
# linux远程连接服务端口
local_port = 8006
#远程的时候链接的端
remote_port = 8006

[bsin-ui-dapp]  
type = tcp
local_ip = 127.0.0.1
# linux远程连接服务端口
local_port = 8007
#远程的时候链接的端
remote_port = 8007

[bsin-ui-sea-condition]  
type = tcp
local_ip = 127.0.0.1
# linux远程连接服务端口
local_port = 8009
#远程的时候链接的端
remote_port = 8009

[bsin-ui-bigan]  
type = tcp
local_ip = 127.0.0.1
# linux远程连接服务端口
local_port = 8010
#远程的时候链接的端
remote_port = 8010


[bsin-app-vuca]  
type = tcp
local_ip = 127.0.0.1
# linux远程连接服务端口
local_port = 8888
#远程的时候链接的端
remote_port = 8888

~~~

- 开机自启 service
~~~bash
sudo vim /etc/systemd/system/frpc.service
# 写入以下内容
[Unit]
Description=Frp Client Service
After=network.target

[Service]
Type=simple
User=root
Group=root
ExecStart=/usr/local/frp/frpc -c /usr/local/frp/frpc.ini
Restart=on-failure

[Install]
WantedBy=multi-user.target



# 加载配置文件
sudo systemctl daemon-reload
# 使能开机自启
sudo systemctl enable frpc
# 启动服务
sudo systemctl start frpc
# 查看服务状态
sudo systemctl status frpc
~~~

## [参考](https://blog.csdn.net/m0_52973297/article/details/130475107?utm_medium=distribute.pc_relevant.none-task-blog-2~default~baidujs_baidulandingword~default-5-130475107-blog-121210349.235^v43^pc_blog_bottom_relevance_base9&spm=1001.2101.3001.4242.4&utm_relevant_index=8)