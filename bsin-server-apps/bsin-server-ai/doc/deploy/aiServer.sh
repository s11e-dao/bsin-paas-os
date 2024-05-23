#!/bin/sh

APP_NAME='ai-server-2.0.0-SNAPSHOT.jar'
ARGS='-Xms1024m -Xmx2048m  -XX:PermSize=512M -XX:MaxPermSize=1024m'
cmd=$1

pid=`ps -ef |grep $APP_NAME | grep -v grep| awk '{print $2}'`

startup(){
  if [ -e ./$APP_NAME ]
  then
       echo "startup..."
       nohup java -jar $ARGS $APP_NAME > ./logs/$APP_NAME.log &
       sleep 2
       if [ ! $pid ]; then
          echo "$APP_NAME pid:$pid start successful!"
       else
          echo "$APP_NAME start field!"
       fi
  else
       echo "$APP_NAME doesn't exist"
  fi
}


if [ ! "$cmd" ]; then
  echo "Please specify args 'start|restart|stop'"
  exit
fi

if [ "$cmd" = 'start' ]; then
  if [ ! $pid ]; then
    startup
  else
    echo "$APP_NAME is running! pid=$pid"
  fi
fi

if [ "$cmd" = 'restart' ]; then
  if [ $pid ]
    then
      echo "$APP_NAME pid:$pid will be killed after 2 seconds!"
      sleep 10
      kill -9 $pid
  fi
  startup
fi

if [ "$cmd" = 'stop' ]; then
  if [ $pid ]; then
    echo "$APP_NAME pid:$pid will be killed after 2 seconds!"
    sleep 2
    kill -9 $pid
  fi
  echo "$APP_NAME is stopped"
fi