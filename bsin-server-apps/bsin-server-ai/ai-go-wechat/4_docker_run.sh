#! /bin/bash

# eg. sudo sh 4_docker_run.sh leonard_bot

REMOTE=$1

docker run -i -t --name $REMOTE leonard/wechatbot:latest /bin/bash

sudo docker run -i -t --name caoyangbot caoyang/wechatbot:latest /bin/bash

# 然后Ctrl+P+Q即可退出控制台，但是容器没有退出，可使用命令查看： docker ps -a

# sudo docker attach 433f401d4fcb  进入容器

# docker run -itd --name $REMOTE leonard/wechatbot:latest bash
# -i 不是必需，加了，容器起来后终端返回容器ID

# docker attach $REMOTE


