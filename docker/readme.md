# bsin-paas-os Docker 启动说明

## 一、准备环境

### 1. 安装命令行工具
```bash
# 推荐使用 zsh + Oh My Zsh
sudo apt-get install -y zsh git curl
sh -c "$(curl -fsSL https://raw.githubusercontent.com/ohmyzsh/ohmyzsh/master/tools/install.sh)"
```

### 2. 安装 Docker

#### Ubuntu
```bash
# 先移除旧版（若有）
sudo apt-get remove -y docker docker-engine docker.io containerd runc

# 更新 APT 源为阿里云镜像（可选）
sudo sed -i 's|http://.*.ubuntu.com|https://mirrors.aliyun.com|g' /etc/apt/sources.list
sudo apt-get update

# 安装依赖并添加 Docker 官方／阿里云 GPG Key
sudo apt-get install -y ca-certificates curl gnupg lsb-release
sudo mkdir -p /etc/apt/keyrings
curl -fsSL https://mirrors.aliyun.com/docker-ce/linux/ubuntu/gpg \
  | sudo gpg --dearmor -o /etc/apt/keyrings/docker.gpg
# （可选）官方源：
# curl -fsSL https://download.docker.com/linux/ubuntu/gpg \
#   | sudo gpg --dearmor -o /etc/apt/keyrings/docker.gpg

# 添加镜像源
echo \
  "deb [arch=$(dpkg --print-architecture) signed-by=/etc/apt/keyrings/docker.gpg] \
   https://mirrors.aliyun.com/docker-ce/linux/ubuntu \
   $(lsb_release -cs) stable" \
  | sudo tee /etc/apt/sources.list.d/docker.list

# 安装 Docker Engine
sudo apt-get update
sudo apt-get install -y docker-ce docker-ce-cli containerd.io docker-compose-plugin

# 验证
docker -v     # Docker version XX.X.X
```

#### CentOS / Alibaba Cloud Linux
参考官方文档或云厂商社区帖子安装对应版本的 `docker-ce`。

### 3、安装 Docker Compose
```bash
# 下载 v2.25.0
sudo curl -L "https://github.com/docker/compose/releases/download/v2.25.0/docker-compose-$(uname -s)-$(uname -m)" \
  -o /usr/local/bin/docker-compose

sudo chmod +x /usr/local/bin/docker-compose
docker-compose --version   # Docker Compose version v2.25.0

# 如需卸载
sudo rm /usr/local/bin/docker-compose
```
> **Tip**：如果拉取 GitHub Releases 失败，可先在 `/etc/hosts` 中写入 GitHub CDN IP，或使用代理。

## 二、编译打包

在项目根目录下有一键打包脚本 `script/package.sh`，支持以下参数：

| 参数         | 功能             |
|------------|----------------|
| `ui_apps`  | 打包前端程序        |
| `server_apps` | 打包后端程序        |
| `all`      | 同时打包前后端程序     |

```bash
# 打包前端
bash ./script/package.sh ui_apps

# 打包后端（注意先修改配置文件中的 MySQL/Redis/Nacos 地址）
bash ./script/package.sh server_apps

# 一键打包前后端
bash ./script/package.sh all
```

## 三、修改 Docker 镜像中 IP 与环境

1. **Nginx**（域名访问情况配置）
    - 若无需域名，注释或移除 `deploy.sh` 中对 `bsin-nginx` 镜像的域名配置。
    - 需要域名访问配置文件路径：`/docker/middleware/nginx/conf/nginx.conf`

2. **环境变量**
    - 在项目根目录修改 `.env`，设置服务器公网 IP 及数据库、Redis、Nacos 等服务地址。

3**环境变量**
   - 修改 docker-compose.yml 中的 shenyu.httpPath地址

## 四、部署脚本与目录

### 1. 创建镜像构建目录（创建一次）
```bash
bash ./script/deploy.sh mkdir
```

### 2. 拷贝打包文件与 `.env`
```bash
bash ./script/deploy.sh copy
# 或手动将 ./dist、./jar 及 .env 复制到 deploy 目录
```

### 3. 执行upload.sh远程上传（可选）
```bash
sh upload.sh root@47.105.xx.xxx
```

## 五、镜像构建与容器管理

在镜像构建目录下执行：
```bash
# 构建所有镜像
bash ./script/deploy.sh build

# 启动所有容器
bash ./script/deploy.sh start

# 停止所有容器（但不删除）
bash ./script/deploy.sh stop

# 停止并删除所有容器
bash ./script/deploy.sh down   # 如需在脚本中自定义，可添加该命令
```
- 启动成功后访问：<http://localhost:8000>

## 六、Docker UI 服务（可选）
使用 Portainer 管理容器：
```bash
docker-compose -f portainer.yml up -d
```
访问：<http://localhost:9000>

## 七、常见问题 (FAQ)

1. **镜像构建失败**
    - 设置 Docker 代理：[Docker 代理快速配置指南](https://cloud-atlas.readthedocs.io/zh-cn/latest/docker/network/docker_proxy_quickstart.html)

2. **`docker pull` 失败**
    - 编辑 `/etc/docker/daemon.json`，添加国内镜像加速器：
      ```json
      {
        "registry-mirrors": [
          "https://docker.m.daocloud.io",
          "https://hub-mirror.c.163.com",
          "https://dockerhub.chenby.cn"
        ],
        "exec-opts": ["native.cgroupdriver=systemd"]
      }
      ```
    - 重启 Docker：
      ```bash
      sudo systemctl daemon-reload
      sudo systemctl restart docker
      ```

3. **targe-gateway 容器启动失败**
    - 检查 `application.yml` 中 Nacos 地址是否指向正确的容器或主机 IP。
    - 登录 ShenYu 管理后台，确认 **插件列表→Proxy→Dubbo→Register** 插件配置了正确的 Nacos 注册中心。

4. **其他容器启动异常**
    - 请逐一核对各服务的配置文件：Nacos、MySQL、Redis、ShenYu 用户名/密码、数据库用户/密码等。

5. **云服务器访问不到服务**
    - 检查安全组／防火墙，确保放通必要端口。

## 八、端口说明

| 端口   | 服务名称      |
|------|-----------|
| 8000 | 基座访问地址    |
| 9095 | 网关后台管理  |
| 9195 | 网关服务      |
| 554  | RTSP 协议   |
| 1935 | RTMP      |
| 8865 | easymedia |