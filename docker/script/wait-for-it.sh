#!/bin/bash

# 等待服务可用
# 参数1：要检查的服务的主机名和端口
HOST=$1
PORT=$2

until nc -z $HOST $PORT; do
  echo "Waiting for $HOST:$PORT..."
  sleep 2
done

echo "$HOST:$PORT is up!"


#services:
#  app:
#    build:
#      context: .
#      dockerfile: Dockerfile.app
#    depends_on:
#      - db
#    command: ["./wait-for-it.sh", "db", "3306", "--", "npm", "start"]