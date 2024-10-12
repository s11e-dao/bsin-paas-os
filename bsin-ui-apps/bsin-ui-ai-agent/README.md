#  bsin-ui-aiAgent

## 介绍

## 软件架构


## 安装教程 
- node版本 >= 18.0.0
~~~bash
# 切换node版本
nvm list
nvm use <version>

# 报错
error An unexpected error occurred: "https://registry.npm.taobao.org/@ant-design%2fpro-chat: certificate has expired".
yarn config list # 首先通过查看yarn的配置清单里的strict-ssl：
yarn config set strict-ssl false   # 使用命令关闭HTTPS证书检测

# 安装依赖
yarn

# 运行
yarn start

# 打包
yarn build

~~~


## 使用说明








## 本地知识库
### mysql数据库
- 

### milvus向量数据库
- 方案一
>* 一个Tenant创建一个库-database
>* 一个merchant创建一个collection,用merchantNo作为collection的name,若名字不支持数字则
>* retrival
>* 用户的chat memory context存储
>* 字段设计

|  id |  customers_no |  knowledge_base_no | text |vector  |
|  ----  | ----  | ----  |----  |----  |
| 主键  | 客户ID |绑定的知识库 |chunk文本 | 向量数据 |


- 方案二
一个Tenant创建一个库-database  
>* 两个collection：merchantCollection和customerCollection
>* merchantCollection字段设计

|  id |  customers_no |  knowledge_base_no |  knowledge_base_file_no | text |vector  |
|  ----  | ----  | ----  | ----  | ----  | ----  |
| 主键  | 商戶ID | 绑定的知识库ID | 绑定的知识库文件ID | chunk后的文本 | 向量数据 |


>* customerCollection 字段设计

|  id |  customers_no |  knowledge_base_no |  knowledge_base_file_no | text |vector  |
|  ----  | ----  | ----  | ----  | ----  | ----  |
| 主键  | 客戶ID | 绑定的知识库ID | 绑定的知识库文件ID | chunk后的文本 | 向量数据 |




## TODO:
### 1.后台菜单梳理
- 索引模型(顶级租户-平台菜单权限)
>* Embedding-2
>* M3E

- 文件处理模型(顶级租户-平台菜单权限)
>* openAI-16k
>* 

- LLM(顶级租户-平台菜单权限)
>* openAI-16k
>* 


- copilot
创建一个copilot，品牌官，数字分身，可以绑定知识库，绑定编排模型，设置提示词，存储chat contex，绑定tool chain 
- 

- 知识库

- AI编排

- tool

- agent

- chat


### 2.本地知识库UI
- 新建知识库
>* name
>* embeddingModel
>* spiltModel

- 编辑知识库



### 3.



### 
#### 前端(方脸： react,vue,uniapp  发哥： Vue，数据分析，挖掘，ERP)
>* UI 
>* 小程序？？
>* H5??


#### 后台(雨辉：数据分析，挖掘，java整套技术栈,中间件,c++/c#，ERP   东方：主要python，数据分析，数据挖掘(爬虫)  leonard: 全栈(工作：c/c++ 机器人运动控制算法) solidity、java、python、)


- 本地知识库模块-Indexes（索引）(Local knowledge base Embeddings and VectorStores)
>* 知识库管理
>* 知识库创建和fine-tune
>>* 文档加载器(document loaders):  
指定源进行加载数据的。将特定格式的数据，转换为文本。如 CSV、File Directory、HTML、JSON、Markdown、PDF。另外使用相关接口处理本地知识，或者在线知识。如 AirbyteJSONAirtable、Alibaba Cloud MaxCompute、wikipedia、BiliBili、GitHub、GitBook 等等
>>* 文本拆分器(text splitters):  
由于模型对输入的字符长度有限制，我们在碰到很长的文本时，需要把文本分割成多个小的文本片段。
>>* 向量存储器(vectorstores):  
存储提取的文本向量，包括 Faiss、Milvus、Pinecone、Chroma 等。如下是 LangChain 集成的向量数据库
>>* 检索器(retrievers):
>* chat contex memory


>* 模型(models)
>>* memory

>* prompts template
>>* 


>* 服务编排|智能体(Agent)
>>* 


- 后台管理(柠檬:rust一整套，Vu3,uniapp, 智能合约安全审计，链上攻击防护...摄影|散文|诗歌)
>* 
>* 

- 产品： 尘伯(体育->艺术->哲学  )



- 

- IT(柠檬)
>* CICD
>* 



## 上线准备
- 1.登录首页弹出： token失效问题 (ok)
- 2.服务应用图标，子菜单图标 (ai应用图标手动修改)
- 3.登录首页显示AI的首页 (忽略，修改基座登录首页，子应用首页不能跳转)
- 4.个人中心跳到账户， bsin-ui-ai 添加 userCenter 界面
- 5.聊天对应默认知识库(feature) 
- 6.刷新问题 (待解决)
- 7.注册失败导致 报用户名已经存在 事务同步
- 7.账户： 
* 创建copilot租户，在此租户下创建的商户和客户默认拥有： bsin-ai 服务， 
* 菜单权限： 
>* copilot租户管理员账户： 
>* admin 拥有： 权限+监控 菜单，
>* 从节点登录
>* 商户号为空处理，后台代码处理

* 普通商户|客户 只拥有 LLms|向量化|提示词|知识库|智能体|敏感词|AI编排|工具集|账户 菜单
>* 从bigan注册登录

权限管理：
产品管理-产品应用-缺少默认应用字段


## 创建节点(租户)
- 1.使用委员会登录，admin 123456
- 2.进入jiujiu子应用-节点-添加： 创建了节点名称和密码的节点登录账号
节点产品：
节点名称:
节点编号：
登录密码：

- 3.注册用户需要认证，目前线上版本没有jiujiu服务，无法认证
：前端临时添加 registerNeedAudit 字段，后台注册时添加 审核扔正逻辑

- 4、菜单权限： (功能管理)
* 权限管理子应用-应用管理-给[通用人工智能]应用添加应用功能
* [通用人工智能]子应用-权限-菜单管理-编辑对应菜单-选择所属功能
* 



- 图片上传：权限校验




    valueEnum: {
      1: { text: '启用', status: 'Processing' },
      0: { text: '未启用', status: 'Default' },
    },