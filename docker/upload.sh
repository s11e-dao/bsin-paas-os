#!/bin/bash

#	Usage: sh upload.sh root@47.105.xx.xxx
REMOTE=$1

rsync -arv --progress --exclude=log/* --exclude=./middleware/redis/data/* --exclude=./middleware/mysql/data/* ./* $REMOTE:/root/bsin-paas-os/