#!/bin/bash

#	Usage: sh upload.sh root@47.105.xx.xxx
REMOTE=$1

rsync -arv --progress --exclude=log/* ./* $REMOTE:/root/bsin-paas-os/