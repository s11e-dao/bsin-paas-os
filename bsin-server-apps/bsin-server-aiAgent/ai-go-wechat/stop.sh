#!/bin/sh



#kill all running nodes
sudo ps -ef | grep ./wechatbot-amd64-linux | grep -v grep | awk '{print $2}' | xargs kill -9 #2> /dev/null


echo "stop program sucessful"
