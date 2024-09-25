# elasticsearch向量数据库服务搭建
[toc]

### 一、es安装步骤

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
mkdir -p /home/data/elasticsearch/plugins
```

> 4、赋予权限
```json
chmod -R 777 /home/data/elasticsearch
```

> 5、创建配置（可选项）
```json

vi /home/data/elasticsearch/config/elasticsearch.yml

# 可访问IP
cluster.name: "my-es"
network.host: 127.0.0.1

# 集群的节点列表（ElasticSearch8新配置）
# discovery.seed_hosts: ["es-ip:9300"]

# 跨域开启密码
http.cors.enabled: true
http.cors.allow-origin: "*"
# http.cors.allow-headers: Authorization
xpack.security.enabled: false

```

> 6、创建脚本
```json
vim startEs.sh

sudo docker run -d \
--restart=always \
--name elasticsearch \
--network es-net \
-p 9200:9200 \
-p 9300:9300 \
--privileged \
-v /home/data/elasticsearch/data:/usr/share/elasticsearch/data \
-v /home/data/elasticsearch/plugins:/usr/share/elasticsearch/plugins \
-e "discovery.type=single-node" \
-e "ES_JAVA_OPTS=-Xms512m -Xmx512m" \
elasticsearch:8.15.0


```

```json

docker exec -it elasticsearch /bin/bash

# 关闭 密码安全验证

echo 'xpack.security.enabled: false' >> elasticsearch.yml

# 重启容器
sudo docker restart elasticsearch

```

> 7、测试Elasticsearch是否安装成功
```json
http://192.168.198.197:9200

```

> 8、容器操作
```json
# 进入容器
docker exec -it elasticsearch /bin/bash

sudo docker exec -it elasticsearch /bin/bash

# 重启容器
docker restart elasticsearch

```

### 二、es可视化kibana安装步骤
> 1、拉取镜像

```json

docker pull kibana:8.10.2

```

> 2、创建配置（可选项）
```json
vi /home/data/kibana/config/kibana.yml

# 主机地址，可以是ip,主机名
server.host: 0.0.0.0
# 提供服务的端口，监听端口
server.port: 5601
# 该 kibana 服务的名称，默认 your-hostname
server.name: "bolei-kibana"
server.shutdownTimeout: "5s"
 
#####----------elasticsearch相关----------#####
# kibana访问es服务器的URL,就可以有多个，以逗号","隔开
elasticsearch.hosts: [ "http://192.168.198.197:9200" ]
monitoring.ui.container.elasticsearch.enabled: true


```

> 3、启动镜像
```json

sudo docker run -d \
--name kibana2 \
-e ELASTICSEARCH_HOSTS=http://elasticsearch:9200 \
--network=es-net \
-p 5601:5601  \
kibana:8.6.0

```

> 4、容器炒作
```json
# 进入容器
docker exec -it kibana2 /bin/bash

sudo docker exec -it kibana2 /bin/bash

# 重启容器
docker restart kibana1

```

> 5、访问地址

```json
http://192.168.198.197:5601

```

### 三、安装elasticsearch-head
直接使用浏览器插件Multi ElasticSearch Head

使用教程：https://blog.csdn.net/qq_50854662/article/details/135967448

## 四、数据测试，kibana脚本，操作es
dev tool控制台处理

```json
# 创建索引
PUT goods_index
        
# 查询索引
GET goods_index

# 添加映射
PUT goods_index/_mapping
{
  "properties":{
    "age":{
      "type":"integer"
    },
    "name":{
      "type":"keyword"
    },
    "desc":{
      "type":"text"
    }
  }
}
        
# 创建索引并且添加映射
PUT goods_index1
{
  "mappings": {
    "properties": {
       "age":{
      "type":"integer"
      },
      "name":{
        "type":"keyword"
      },
      "desc":{
        "type":"text"
      }
    }
  }
}

# 添加文档，指定id
PUT goods_index/_doc/1
{
  "name":"张三",
  "age":34,
  "desc":"北京人深圳来"
}

# 添加文档，不指定id
POST goods_index/_doc
{
  "name":"李四",
  "age":25,
  "desc":"四川妹子真辣"
}
        
# 修改文档
PUT goods_index/_doc/1
{
  "name":"张三1111",
  "age":34,
  "desc":"北京人深圳来"
}

# 删除文档
DELETE goods_index/_doc/1

# 查询文档 指定id
GET goods_index/_doc/56YoJ5IBpXr2Jk-kqx2q
# 查询所有文档
GET goods_index/_search

# 删除索引
DELETE goods_index

```

## 五、向量数据测试，kibana脚本，操作es

```json
1. 创建索引

PUT /vector_index
{
  "mappings": {
    "properties": {
      "my_vector_field": {
        "type": "dense_vector",
        "dims": 128
      },
      "my_text": {
        "type": "text"
      }
    }
  }
}

2. 写入数据
PUT /vector_index/_doc/1
{
  "my_text": "示例文本",
  "my_vector_field": [0.1, 0.2, ..., 0.128]
}

3. 搜索查询
POST /vector_index/_search
{
  "query": {
    "script_score": { // 使用脚本评分来计算相似度得分
      "query": {
        "match_all": {}
      },
      "script": {
        "source": "cosineSimilarity(params.queryVector, 'my_vector_field') + 1.0", // 计算查询向量与存储向量的余弦相似度，并加 1.0 以确保得分非负
        "params": {
          "queryVector": [0.1, 0.2, ..., 0.128]
        }
      }
    }
  }
}

```

## 参考资料
* https://blog.csdn.net/cmh1008611/article/details/141562198

* https://blog.csdn.net/qq_50854662/article/details/135967448