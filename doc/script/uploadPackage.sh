#!/usr/bin/env bash
# eg. sh uploadPackage.sh bitnami@3.38.63.165

REMOTE=$1
#scp -r ../../bsin-server-targe-gateway/target/bsin-server-targe-gateway-1.0.0-SNAPSHOT.jar $REMOTE:~/copilot/server/
#scp -r ../../bsin-server-upms/upms-server/target/upms-server-1.0.0-SNAPSHOT.jar $REMOTE:~/copilot/server/

# 前端基座
tar -zcvf ./apps-container.tar ../../bsin-apps-container/dist
scp -r apps-container.tar $REMOTE:~/copilot/front/


# 前端upms
tar -zcvf ui-upms.tar ../../bsin-ui-apps/bsin-ui-upms/dist
scp -r ui-upms.tar $REMOTE:~/copilot/front/
 
