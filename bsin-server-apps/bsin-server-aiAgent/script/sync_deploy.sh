#! /bin/bash

# eg. sh sync_deploy.sh ubuntu@69.235.172.107
# eg. sh sync_deploy.sh bitnami@3.88.104.155
# eg. 恋爱脑 sh sync_deploy.sh bitnami@52.36.185.163

REMOTE=$1
# rsync -arv --progress --exclude=log/* ./ai-server/target/ai-server-1.0.0-SNAPSHOT.jar $REMOTE:~/BsinPaas/server/
scp -r ./ai-server/target/ai-server-1.0.0-SNAPSHOT.jar $REMOTE:~/BsinPaas/server/
scp -r ./ai-server/src/main/resources/SensitiveWordList.txt $REMOTE:~/BsinPaas/server/

ssh $REMOTE
