#!/usr/bin/env bash
# eg. sh uploadPackage.sh bitnami@3.38.63.165

REMOTE=$1 
# 前端 ai
tar -zcvf ui-ai.tar ../../dist
scp -r ui-ai.tar $REMOTE:~/copilot/front/
 
