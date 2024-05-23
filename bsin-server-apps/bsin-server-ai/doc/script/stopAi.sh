#!/bin/sh

APP_NAME=ai-server-2.0.0-SNAPSHOT.jar

is_exist(){
  pid=$(ps -ef|grep $APP_NAME|grep -v grep|awk '{print $2}' )
  if [ -z "${pid}" ]; then
    return 1
  else
    return 0
  fi
}

shutdown(){
  is_exist
  if [ $? -eq "1" ]; then
    echo "${APP_NAME} is not running."
  else
    kill -9 ${pid} 2>&1 &
    echo "${APP_NAME} shutdown success"
  fi
}
shutdown