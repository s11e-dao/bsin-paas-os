# es向量数据库服务
[toc]

## es向量数据库服务搭建

https://blog.csdn.net/cmh1008611/article/details/141562198

步骤：

> 1、创建网络
```json
docker network create es-net

```

> 2、拉取镜像
```json
docker pull elasticsearch:8.15.0

docker pull docker.elastic.co/elasticsearch/elasticsearch:8.15.0

```

> 3、创建挂载目录
```json
mkdir -p /home/data/elasticsearch/config
mkdir -p /home/data/elasticsearch/data
mkdir -p /home/data/elasticsearch/logs
```

> 4、赋予权限
```json
chmod -R 777 /home/data/elasticsearch
```

> 5、创建配置
```json
vim elasticsearch.yml

vi /home/data/elasticsearch/config/elasticsearch.yml

#可访问IP
http.host: 0.0.0.0
        
cluster.name: "my-es"
network.host: 127.0.0.1

#集群的节点列表（ElasticSearch8新配置）
discovery.seed_hosts: ["es-ip:9300"]

# 跨域开启密码
http.cors.enabled: true
http.cors.allow-origin: "*"
http.cors.allow-headers: Authorization
xpack.security.enabled: true

```

> 6、创建脚本
```json
vim startEs.sh

docker run -d \
--restart=always \
--name es \
--network es-net \
-p 9200:9200 \
-p 9300:9300 \
--privileged \
-v /usr/local/es/data:/usr/share/elasticsearch/data \
-v /usr/local/es/plugins:/usr/share/elasticsearch/plugins \
-e "discovery.type=single-node" \
-e "ES_JAVA_OPTS=-Xms512m -Xmx512m" \
elasticsearch:8.6.0

```

> 7、测试Elasticsearch是否安装成功
```json
http://192.168.27.129:9200
```

## 数据测试


