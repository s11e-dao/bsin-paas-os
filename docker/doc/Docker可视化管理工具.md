# 安装Portainer可视化工具

1、下载Portainer镜像

sudo docker pull portainer/portainer-ce

2、运行Portainer容器

sudo docker run -d -p 9000:9999 \
--name portainer --restart always \
-v /var/run/docker.sock:/var/run/docker.sock \
-v portainer_data:/data portainer/portainer-ce


参数	参数说明
-d	在后台运行容器。
-p 9000:9000	将容器的9000端口映射到宿主机的9000端口。
--name portainer	为容器指定一个名称为portainer。
--restart always	如果容器退出，总是自动重启。
-v /var/run/docker.sock:/var/run/docker.sock	将Docker守护进程的Unix套接字挂载到容器中，这允许Portainer直接与Docker守护进程通信。
-v portainer_data:/data	创建一个持久化的数据卷，以保存Portainer的配置数据。
portainer/portainer-ce	指定要运行的Portainer镜像。