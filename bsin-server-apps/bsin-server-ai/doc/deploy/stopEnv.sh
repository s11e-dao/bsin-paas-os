#!/bin/sh

#kill all running nodes
ps | grep appache-zookeeper-3.6.1 | grep -v grep | awk '{print $1}' | xargs kill -9 2>/dev/null
ps | grep seata-server | grep -v grep | awk '{print $1}' | xargs kill -9 2>/dev/null

echo "stop env sucessful"
