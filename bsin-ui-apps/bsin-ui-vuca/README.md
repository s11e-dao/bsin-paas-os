# vuca

## 切换node版本

nvm list

nvm use <version>

~~~bash
# 报错
error An unexpected error occurred: "https://registry.npm.taobao.org/@ant-design%2fpro-chat: certificate has expired".

yarn config list # 首先通过查看yarn的配置清单里的strict-ssl：
yarn config set strict-ssl false   # 使用命令关闭HTTPS证书检测
~~~

