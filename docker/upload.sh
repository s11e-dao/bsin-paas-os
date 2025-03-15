#!/bin/bash

# 使用说明，用来提示输入参数
usage(){
	echo "Usage: sh upload.sh root@47.105.xx.xxx"
	exit 1
}
REMOTE=$1
if [[ -z $REMOTE ]]; then
	usage
fi

rsync -arv --progress \
	--exclude=log/* \
	--exclude=./middleware/redis/data/* \
	--exclude=./middleware/mysql/data/* \
	--exclude=*.log \
	./* $REMOTE:/root/bsin-paas-os/
