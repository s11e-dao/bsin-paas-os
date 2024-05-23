#!/usr/bin/env bash
# eg. sh uploadNginxSSL.sh bitnami@3.38.63.165
# sh uploadNginxSSL.sh bitnami@43.200.152.71

REMOTE=$1
scp -r ./nginx/nginx.conf $REMOTE:~/copilot/conf/
scp -r ./ssl/* $REMOTE:~/copilot/conf/
