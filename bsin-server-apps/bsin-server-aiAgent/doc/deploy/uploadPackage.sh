#!/usr/bin/env bash
# eg. sh uploadPackage.sh bitnami@43.200.152.71


REMOTE=$1

scp -r ../../ai-server/target/ai-server-2.0.0-SNAPSHOT.jar $REMOTE:~/copilot/server/

#scp ./stopAi.sh $REMOTE:~/copilot/server/
