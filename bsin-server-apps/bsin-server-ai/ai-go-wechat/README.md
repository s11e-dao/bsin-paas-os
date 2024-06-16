# wechatbot
> 最近chatGPT异常火爆，本项目可以将个人微信化身GPT机器人，
> 项目基于[openwechat](https://github.com/eatmoreapple/openwechat) 开发。


### 目前实现了以下功能
 * 提问增加上下文，更接近官网效果 
 * 机器人群聊@回复
 * 机器人私聊回复
 * 好友添加自动通过
 
# 使用前提 
> * 微信必须实名认证。

# 注意事项
> * 项目仅供娱乐，滥用可能有微信封禁的风险，请勿用于商业用途。
> * 请注意收发敏感信息，本项目不做信息过滤。

# 快速开始
> 非技术人员请直接下载release中的[压缩包](https://github.com/869413421/wechatbot/releases/tag/v1.1.1) ，解压运行。
````
# 获取项目
git clone https://github.com/869413421/wechatbot.git

# 进入项目目录
cd wechatbot

# 复制配置文件
copy config.dev.json config.json

# 启动项目
go run main.go

````


## 引入swagger
- 安装swagger
~~~shell
# Linux
go get -u github.com/go-swagger/go-swagger/cmd/swagger
~~~

- 引入
~~~shell
swag init -g main.go
# 输出
├── docs
│   ├── docs.go
│   ├── swagger.json
│   └── swagger.yaml
~~~

- 使用
~~~go
// 千万不要忘了导入把你上一步生成的docs
import (
_ "bsinpass/go/docs"
"github.com/gin-gonic/gin"
ginSwagger "github.com/swaggo/gin-swagger"
"github.com/swaggo/gin-swagger/swaggerFiles"
)

// 注册swagger api相关路由
router.GET("/swagger/*any", ginSwagger.DisablingWrapHandler(swaggerFiles.Handler, "NAME_OF_ENV_VARIABLE"))
~~~

# 引入 [go-redis](https://redis.uptrace.dev/zh/guide/go-redis.html#%E5%AE%89%E8%A3%85)
~~~bash
go get github.com/redis/go-redis/v9
~~~



# Linux 运行
- 编译
~~~bash
go build -o 
~~~

~~~bash
# 打开终端，登录服务器
ssh root@81.71.39.231
cd /opt/app/wechatbot/
nohup ./wechatbot-amd64-linux &
# 生成二维码，扫描登录

~~~


# docker 部署
- Dockerfile语法知识
~~~bash
# 指定基础镜像，必须为第一个命令
FROM <image name>
# 指定作者
格式：
    MAINTAINER <name>
示例：
    MAINTAINER Kingram
# 构建镜像时执行的shell或者exec命令,UN指令创建的中间镜像会被缓存，并会在下次构建中使用。如果不想使用这些缓存镜像，可以在构建时指定--no-cache参数，如：docker build --no-cache
格式：
    RUN <command>
exec执行
格式：
    RUN ["executable", "param1", "param2"]
示例：
    RUN ["executable", "param1", "param2"]
    RUN apk update
    RUN ["/etc/execfile", "arg1", "arg1"]
# 将本地文件添加到容器中
格式:
    ADD <src>... <dest>
    ADD ["<src>",... "<dest>"] # 用于支持包含空格的路径
示例：
    ADD hom* /mydir/          # 添加所有以"hom"开头的文件
    ADD hom?.txt /mydir/      # ? 替代一个单字符,例如："home.txt"
    ADD test relativeDir/     # 添加 "test" 到 `WORKDIR`/relativeDir/
    ADD test /absoluteDir/    # 添加 "test" 到 /absoluteDir/
# 指定容器启动时执行的命令,Dockerfile只允许使用一次CMD指令。 使用多个CMD会抵消之前所有的指令，只有最后一个指令生效。 CMD有三种形式：
格式：
    CMD ["executable","param1","param2"] (执行可执行文件，优先)
    CMD ["param1","param2"] (设置了ENTRYPOINT，则直接调用ENTRYPOINT添加参数)
    CMD command param1 param2 (执行shell内部命令)
示例：
    CMD echo "This is a test." | wc -
    CMD ["/usr/bin/wc","--help"]
注：
 　　CMD不同于RUN，CMD用于指定在容器启动时所要执行的命令，而RUN用于指定镜像构建时所要执行的命令。
 　　
# 配置给容器一个可执行的命令，这意味着在每次使用镜像创建容器时一个特定的应用程序可以被设置为默认程序。同时也意味着该镜像每次被调用时仅能运行指定的应用。
格式：
    ENTRYPOINT ["executable", "param1", "param2"] (可执行文件, 优先)
    ENTRYPOINT command param1 param2 (shell内部命令)
示例：
    FROM ubuntu
    ENTRYPOINT ["top", "-b"]
    CMD ["-c"]
# 指定RUN、CMD与ENTRYPOINT命令的工作目录。在使用docker run运行容器时，可以通过-w参数覆盖构建时所设置的工作目录。
格式：
    WORKDIR /path/to/workdir
示例：
    WORKDIR /a  (这时工作目录为/a)
    WORKDIR b  (这时工作目录为/a/b)
    WORKDIR c  (这时工作目录为/a/b/c)
# 指定交互端口,EXPOSE并不会让容器的端口访问到主机。要使其可访问，需要在docker run运行容器时通过-p来发布这些端口，或通过-P参数来发布EXPOSE导出的所有端口
格式：
    EXPOSE <port> [<port>...]
示例：
    EXPOSE 80 443
    EXPOSE 8080
    EXPOSE 11211/tcp 11211/udp
~~~

- 举个栗子:一个简单的GO程序创建为docker镜像
>* 代码如下:
~~~go
//hello.go
package main
import (
    "fmt"
)
func main() {
    fmt.Println("Hello, World!")
}
~~~
>* 第一步编译:
~~~bash
go build -o hello
~~~

>* 第二步编写Dockerfile
~~~bash
#源镜像
FROM golang:latest

# 为我们的镜像设置必要的环境变量
ENV GO111MODULE=on \
    CGO_ENABLED=0 \
    GOOS=linux \
    GOARCH=amd64 \
    GOPROXY="https://goproxy.cn,direct"


# 在docker的根目录下创立相应的应用目录
RUN mkdir -p /opt/app/wechatbot

## 设置工作目录
WORKDIR /opt/app/wechatbot
## 把以后（宿主机上）目录下的文件都复制到docker上刚创立的目录下
COPY . .
#将服务器的go工程代码退出到docker容器中
#ADD . $GOPATH/src/github.com/mygohttp
#go构建可执行文件
RUN go build main.go
#裸露端口
EXPOSE 8080

RUN chmod +x main
ENTRYPOINT ["./main"]

## 启动docker须要执行的文件
#CMD go run main.go
#最终运行docker的命令
#ENTRYPOINT  ["./mygohttp"]
~~~

>* 第三步创建镜像
~~~bash
docker build -t leonard/wechatbot .
# 查看镜像
docker images
~~

>* 第四步创建并启动容器
~~~
docker run -it leonard/wechatbot:latest
~~~

# 使用示例
### 向机器人发送`我要问下一个问题`，清空会话信息。
### 私聊
<img width="300px" src="https://raw.githubusercontent.com/869413421/study/master/static/%E5%BE%AE%E4%BF%A1%E5%9B%BE%E7%89%87_20221208153022.jpg"/>

### 群聊@回复
<img width="300px" src="https://raw.githubusercontent.com/869413421/study/master/static/%E5%BE%AE%E4%BF%A1%E5%9B%BE%E7%89%87_20221208153015.jpg"/>

### 添加微信（备注: wechabot）进群交流

**如果二维码图片没显示出来，请添加微信号 huangyanming681925**

<img width="210px"  src="https://raw.githubusercontent.com/869413421/study/master/static/qr.png" align="left">



## 元客DAO
- 服务器账号：
~~~
正式环境——腾讯云账号：
账号：magicstoneyb@163.com
密码：yuankedao@2022
服务器IP：http://81.71.39.231
~~~
- openAI账号：
~~~
magicstoneyb@163.com    411@magicM
~~~

## GPT商业化
~~~
kobe666@icloud.com
nuoxiangwei7056


xyw13692319084@163.com
xuyiwen980508

lynn-secret-key:
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

bolei-secret-key:
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


bolei-曹老师
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
~~~
