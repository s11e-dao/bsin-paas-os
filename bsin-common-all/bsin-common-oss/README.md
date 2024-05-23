# bsin-common-oss

bsin-pass 对象存储服务

  **文档版本**

| 版本号 | 修改日期       | 编写   | 修改内容                     | 备注 |
| ------ |------------| ------ | ---------------------------- | ---- |
| V1.0.0 | 2023/06/20 | leonard | 新建                         |      |



## 目录结构



## 启动注意事项  
- server.port: 


- jar包本地安装
>* 进入script目录，执行
~~~bash
sh installIpfsLibs.sh #安装ipfs lib
~~~

- 安装部署


## [IPFS](https://ipfs.docs.apiary.io/#reference/dht/put/cp)  
### http api
- 用于与IPFS节点交互的API，
- HTTPAPI目前接受所有方法，因此GET对任何组都可以像POST一样工作。
- 下面所示的方法是应该遵守的规范，尽管任何方法都可以奏效。有关更多信息，请[参阅此讨论](https://github.com/ipfs/go-ipfs/issues/2165)

#### add
添加文件或目录到IPFS

#### files
Manipulate unixfs files.Files is an API for manipulating ipfs objects as if they were a unix filesystem.Note: Most of the subcommands of 'ipfs files' accept the 'flush' option. It defaults to 'true'. Use caution when setting this to 'false'. It will improve performance for large numbers of file operations, but it does so at the cost of consistency guarantees.This command can't be called directly.
- mv
- mkdir
- cp
- ls



#### key
- list
显示本地所有密钥对

- gen
创建秘钥对


#### name
- publish
Publish an object to IPNS.
>* 使用ipfs name publish /ipfs/CID 发布以后，会覆盖当前节点CID
>* 使用 /ipns/节点CID访问

- resolve
Gets the value currently published at an IPNS name.


## IPFS应用
- 1、通过files/mkdir创建文件夹(demo)
- 2、通过key/gen为所创建的文件夹(demo)创建名称为demo的秘钥对，用于ipns发布
- 3、通过add添加文件1.txt至/ipfs，默认上传的文件都是在/ipfs/目录下
- 4、通过files/cp拷贝将步骤3上传的文件拷贝到步骤1创建的目录中(ipfs/cid --> /demo/1.txt)
- 5、通过name/publish将上传文件后的文件夹CID发布到步骤2创建key上

## 上传文件大小限制修改：
* 在BsinGatewayApplication类中配置