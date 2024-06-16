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