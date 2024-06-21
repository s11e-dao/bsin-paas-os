#! /bin/bash

# eg. sh 1_sync2remot.sh ubuntu@69.235.172.107

REMOTE=$1

rsync -arv --progress --exclude=log/* ./* $REMOTE:~/leonard/wechatBot/

ssh $REMOTE

# cd ~/leonard/wechatBot/

