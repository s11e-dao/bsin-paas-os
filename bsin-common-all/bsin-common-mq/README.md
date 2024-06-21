# bsin-common-mq

bsin-pass 消息队列服务

  **文档版本**

| 版本号 | 修改日期       | 编写   | 修改内容                     | 备注 |
| ------ |------------| ------ | ---------------------------- | ---- |
| V1.0.0 | 2023/06/20 | leonard | 新建                         |      |


## 启动注意事项  
- server.port: 

```shell
docker compose -f docker-compose-rocketmq.yml up
# 后台运行
docker compose -f docker-compose-rocketmq.yml up -d
```

```shell
nohup sh mqnamesrv > name.out 2>&1 &

nohup sh mqbroker -c /home/rednet/soft/rocketmq-all-5.1.4-bin-release/conf/broker.conf > broker.out 2>&1 &

nohup sh mqbroker -c /home/rednet/soft/rocketmq-all-4.9.4-bin-release/conf/broker.conf > broker.out 2>&1 &

# 关闭 
sh mqshutdown namesrv

sh mqshutdown broker

docker run -d --name rocketmq-dashboard -e "JAVA_OPTS=-Drocketmq.namesrv.addr=127.0.0.1:9876" -p 8080:8080 -t apacherocketmq/rocketmq-dashboard:latest
```

##发送和接收消息测试

> export NAMESRV_ADDR=localhost:9876
> sh tools.sh org.apache.rocketmq.example.quickstart.Producer


> sh tools.sh org.apache.rocketmq.example.quickstart.Consumer


## rocketMq安装
https://blog.csdn.net/daringdart/article/details/136880725

## 注意事项
* linux 下需要关闭防火墙
* 9876端口浏览器无法之间访问，通过程序可以连接