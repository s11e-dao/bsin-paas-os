#!/usr/bin/env bash
# eg. sh uploadPackage.sh bitnami@43.200.152.71

REMOTE=$1 
# 前端 ai
sh ./build.sh 
scp -r ai-go-wechat.tar $REMOTE:~/copilot/server/
 
