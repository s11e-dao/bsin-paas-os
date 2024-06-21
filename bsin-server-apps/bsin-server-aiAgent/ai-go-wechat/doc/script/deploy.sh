#! /bin/bash

# eg. sh deploy.sh ubuntu@69.235.172.107

REMOTE=$1

rsync -arv --progress --exclude=log/* ./config.json $REMOTE:~/leonard/
rsync -arv --progress --exclude=log/* ./bin/wechatbot-amd64-linux $REMOTE:~/leonard/



# sshpass -p $password ssh $REMOTE
# cd /opt/app//opt/app/bsinpaas/golang/wechatbot/
# chmod 777 wechatbot-amd64-linux			
# sudo nohup ./wechatbot-amd64-linux &		
# jobs