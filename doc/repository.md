## Bsin-PaaS 后端仓库及服务

|            项目名称            | 是否开源 |                                                               仓库地址                                                               | 功能简介                                                                               | port |   
|:--------------------------:|:----:|:--------------------------------------------------------------------------------------------------------------------------------:|:-----------------------------------------------------------------------------------|:-----|  
|   bsin-paas-dependencies   |  是   |               [bsin-paas-dependencies](https://gitee.com/s11e-DAO/bsin-paas-os/tree/master/bsin-paas-dependencies)               | 系统全局maven依赖                                                                        | none |  
|      bsin-common-all       |  是   |           [bsin-common-all](https://gitee.com/s11e-DAO/bsin-paas-os/tree/master/bsin-server-apps/bsin-server-ai-agent)           | Bsin-PaaS 系统公共模块： 区块链工具类、redis工具类、消息队列工具类、对象存储工具类、支付工具类、短信工具类、第三方授权、基础工具类、安全工具类... | none |  
|     bsin-targe-gateway     |  是   |         [bsin-targe-gateway](https://gitee.com/s11e-DAO/bsin-paas-os/tree/master/bsin-server-apps/bsin-server-ai-agent)          | Bsin-PaaS 网关                                                                       | 9195 |  
|  bsin-targe-gateway-admin  |  是   |      [bsin-targe-gateway-admin](https://gitee.com/s11e-DAO/bsin-paas-os/tree/master/bsin-server-apps/bsin-server-ai-agent)       | Bsin-PaaS 网关后台管理                                                                   | 9095 |  
|      bsin-server-upms      |  是   |          [bsin-server-upms](https://gitee.com/s11e-DAO/bsin-paas-os、tree/master/bsin-server-apps/bsin-server-ai-agent)           | Bsin-PaaS 权限管理                                                                     | 9101 |  
|      bsin-server-crm       |  是   |             [bsin-server-crm](https://gitee.com/s11e-DAO/bsin-paas-os/tree/master/bsin-server-apps/bsin-server-crm)              | Bsin-PaaS 客户关系管理系统                                                                 | 9102 |  
|      bsin-server-brms      |  是   |            [bsin-server-brms](https://gitee.com/s11e-DAO/bsin-paas-os/tree/master/bsin-server-apps/bsin-server-brms)             | Bsin-PaaS 业务规则管理系统                                                                 | 9103 |  
|    bsin-server-ai-agent    |  是   |                             [bsin-server-ai-agent](https://gitee.com/s11e-DAO/bsin-server-ai-agent)                              | Bsin-PaaS ai智能体系统                                                                  | 9104 |  
|  bsin-server-ai-go-wechat  |  是   |                       [bsin-server-ai-agent](https://gitee.com/s11e-DAO/bsin-server-ai-agent/ai-go-wechat)                       | Bsin-PaaS ai微信平台                                                                   | 8026 | 
|    bsin-server-workflow    |  是   |        [bsin-server-workflow](https://gitee.com/s11e-DAO/bsin-paas-os/tree/master/bsin-server-apps/bsin-server-workflow)         | Bsin-PaaS 工作流                                                                      | 9105 |    
| bsin-server-workflow-admin |  是   | [bsin-server-workflow-admin](https://gitee.com/s11e-DAO/bsin-paas-os/tree/master/bsin-server-apps/bsin-server-ai-workflow-admin) | Bsin-PaaS 工作流后台管理                                                                  | 9106 |  
|      bsin-server-waas      |  否   |                                 [bsin-server-waas](https://gitee.com/s11e-DAO/bsin-server-waas)                                  | Bsin-PaaS 火源钱包系统                                                                   | 9107 |  
|      bsin-server-http      |  是   |            [bsin-server-http](https://gitee.com/s11e-DAO/bsin-paas-os/tree/master/bsin-server-apps/bsin-server-http)             | Bsin-PaaS http服务                                                                   | 9108 |  
|     bsin-server-search     |  是   |          [bsin-server-search](https://gitee.com/s11e-DAO/bsin-paas-os/tree/master/bsin-server-apps/bsin-server-search)           | Bsin-PaaS 搜索引擎服务                                                                   | 9109 |  

## Bsin-PaaS 后台管理前端，app，小程序...仓库

|           项目名称           | 是否开源 |                                                       仓库地址                                                       | 功能简介                                | port |
|:------------------------:|:----:|:----------------------------------------------------------------------------------------------------------------:|:------------------------------------|:-----|  
|   bsin-apps-container    |  是   |                [bsin-apps-container](https://gitee.com/s11e-DAO/bsin-paas-os/bsin-apps-container)                | Bsin-PaaS 微前端应用基座                   | 8000 |    
|       bsin-ui-upms       |  是   |          [bsin-ui-upms](https://gitee.com/s11e-DAO/bsin-paas-os/tree/master/bsin-ui-apps/bsin-ui-upms)           | Bsin-PaaS 权限管理后台管理子应用前端             | 8001 |   
|       bsin-ui-waas       |  否   |      [bsin-ui-orchestration](https://gitee.com/s11e-DAO/bsin-paas-os/tree/master/bsin-ui-apps/bsin-ui-waas)      | Bsin-PaaS 火源钱包系统后台子应用前端             | 8002 |   
|  bsin-ui-decision-admin  |  否   | [bsin-ui-orchestration](https://gitee.com/s11e-DAO/bsin-paas-os/tree/master/bsin-ui-apps/bsin-ui-decision-admin) | Bsin-PaaS 智能决策引擎后台子应用前端             | 8003 |    
|     bsin-ui-ai-agent     |  否   |      [bsin-ui-ai-agent](https://gitee.com/s11e-DAO/bsin-paas-os/tree/master/bsin-ui-apps/bsin-ui-ai-agent)       | Bsin-PaaS 通用人工智能管理子应用前端             | 8004 |      
|       bsin-ui-erp        |  否   |                         [bsin-ui-orchestration](https://gitee.com/s11e-DAO/bsin-ui-erp)                          | Bsin-PaaS ERP系统后台子应用前端              | 8005 |      
|   bsin-minapp-merchant   |  否   |                     [bsin-minapp-merchant](https://gitee.com/s11e-DAO/bsin-minapp-merchant)                      |                                     |      |    
|    bsin-minapp-baigui    |  否   |                      [bsin-minapp-merchant](https://gitee.com/s11e-DAO/bsin-minapp-baigui)                       | Baigui是dao的nft发行流通工具，属于daobook内置模块。 |      |    
| bsin-minapp-daobook-task |  是   |                 [bsin-minapp-daobook-task](https://gitee.com/s11e-DAO/bsin-minapp-daobook-task)                  | 治理工具：daobook                        |      |      
|    bsin-minapp-wallet    |  否   |                       [bsin-minapp-wallet](https://gitee.com/s11e-DAO/bsin-minapp-wallet)                        |                                     |      |      

## Bsin-PaaS 业务闭源jiujiu仓库

|         项目名称          | 是否开源 |                                                       仓库地址                                                        | 功能简介                          | port |   
|:---------------------:|:----:|:-----------------------------------------------------------------------------------------------------------------:|:------------------------------|:-----|  
|      jiujiu-paas      |  否   |                             [jiujiu-paas](https://gitee.com/arrowspider/jiujiu-paas)                              | 基于bsin-paas构建的一站式web3品牌服务     |      |  
|    bsin-app-baigui    |  否   |      [bsin-app-baigui](https://gitee.com/arrowspider/jiujiu-paas/tree/master/jiujiu-paas-ui/bsin-app-baigui)      | Baigui 是 S11e Network 的智慧收银终端 |      |   
|    bsin-app-bigan     |  否   |      [bsin-app-baigui](https://gitee.com/arrowspider/jiujiu-paas/tree/master/jiujiu-paas-ui/bsin-app-bigan)       | bigan 是s11e network 商户端       |      |   
|    bsin-app-jiuiju    |  否   |      [bsin-app-baigui](https://gitee.com/arrowspider/jiujiu-paas/tree/master/jiujiu-paas-ui/bsin-app-jiuiju)      | jiujiu 小程序、app                | 8110 |   
|    bsin-app-bigan     |  否   |      [bsin-app-baigui](https://gitee.com/arrowspider/jiujiu-paas/tree/master/jiujiu-paas-ui/bsin-app-bigan)       | bigan 是s11e network 商户端       |      |   
|     bsin-app-vuca     |  否   |       [bsin-app-baigui](https://gitee.com/arrowspider/jiujiu-paas/tree/master/jiujiu-paas-ui/bsin-app-vuca)       | AI智能体web3应用                   |      |   
|     bsin-ui-bigan     |  否   |       [bsin-app-baigui](https://gitee.com/arrowspider/jiujiu-paas/tree/master/jiujiu-paas-ui/bsin-ui-bigan)       | jiujiu后台管理子应用前端               | 8010 |     
| bsin-server-community |  否   | [bsin-app-baigui](https://gitee.com/arrowspider/jiujiu-paas/tree/master/jiujiu-paas-server/bsin-server-community) | jiujiu DAO活动任务子系统             | 9110 |   
|    bsin-server-oms    |  否   |    [bsin-app-baigui](https://gitee.com/arrowspider/jiujiu-paas/tree/master/jiujiu-paas-server/bsin-server-oms)    | jiujiu 订单管理子系统                | 9111 |    

## Bsin-PaaS 开发脚手架仓库

|              项目名称              | 是否开源 |                                                              仓库地址                                                               | 功能简介                                                 | port |   
|:------------------------------:|:----:|:-------------------------------------------------------------------------------------------------------------------------------:|:-----------------------------------------------------|:-----|  
|   bsin-server-scaffold-dubbo   |  是   |   [bsin-server-scaffold](https://gitee.com/s11e-DAO/bsin-paas-os/tree/master/bsin-server-scaffold/bsin-server-scaffold-dubbo)   | Bsin-Paas 后端dubbo服务应用开发脚手架                           |      |  
|   bsin-server-scaffold-http    |  是   |   [bsin-server-scaffold](https://gitee.com/s11e-DAO/bsin-paas-os/tree/master/bsin-server-scaffold/bsin-server-scaffold-http)    | Bsin-Paas 后端http服务应用开发脚手架                            |      |  
|   bsin-server-scaffold-mqtt    |  是   |   [bsin-server-scaffold](https://gitee.com/s11e-DAO/bsin-paas-os/tree/master/bsin-server-scaffold/bsin-server-scaffold-mqtt)    | Bsin-Paas 后端mqtt服务应用开发脚手架                            |      |  
|   bsin-server-scaffold-sofa    |  是   |   [bsin-server-scaffold](https://gitee.com/s11e-DAO/bsin-paas-os/tree/master/bsin-server-scaffold/bsin-server-scaffold-sofa)    | Bsin-Paas 后端sofa服务应用开发脚手架                            |      |  
| bsin-server-scaffold-websocket |  是   | [bsin-server-scaffold](https://gitee.com/s11e-DAO/bsin-paas-os/tree/master/bsin-server-scaffold/bsin-server-scaffold-websocket) | Bsin-Paas 后端websocket服务应用开发脚手架                       |      |  
|  bsin-ui-scaffold-react-app1   |  是   | [bsin-ui-scaffold-react-app1](https://gitee.com/s11e-DAO/bsin-paas-os/tree/master/bsin-ui-scaffold/bsin-ui-scaffold-react-app1) | Bsin-PaaS react-app1开发脚手架                            |      |  
|  bsin-ui-scaffold-react-app2   |  是   | [bsin-ui-scaffold-react-app1](https://gitee.com/s11e-DAO/bsin-paas-os/tree/master/bsin-ui-scaffold/bsin-ui-scaffold-react-app1) | Bsin-PaaS react-app2开发脚手架                            |      |  
|  bsin-ui-scaffold-react-umi4   |  是   | [bsin-ui-scaffold-react-app1](https://gitee.com/s11e-DAO/bsin-paas-os/tree/master/bsin-ui-scaffold/bsin-ui-scaffold-react-app1) | Bsin-PaaS react-umi4开发脚手架                            |      |  
|  bsin-ui-scaffold-react-vue3   |  是   | [bsin-ui-scaffold-react-app1](https://gitee.com/s11e-DAO/bsin-paas-os/tree/master/bsin-ui-scaffold/bsin-ui-scaffold-react-app1) | Bsin-PaaS vue3开发脚手架                                  |      |  
|   bsin-minapps-container-ios   |  是   |                       [bsin-minapp-container-ios](https://gitee.com/s11e-DAO/bsin-minapps-container-ios)                        | 原生ios APP壳应用，可容纳各种小程序应用，提供极致的h5用户体验，帮助企业打造超级应用生态     |      |    
| bsin-minapps-container-android |  是   |                   [bsin-minapps-container-android](https://gitee.com/s11e-DAO/bsin-minapps-container-android)                   | 原生android APP壳应用，可容纳各种小程序应用，提供极致的h5用户体验，帮助企业打造超级应用生态 |      |    

## Bsin-PaaS 其他仓库

|      项目名称       | 是否开源 |                             仓库地址                              | 功能简介                   | server port |   
|:---------------:|:----:|:-------------------------------------------------------------:|:-----------------------|:------------|  
| bsin-server-env |  是   | [bisn-server-env](https://gitee.com/s11e-DAO/bisn-server-env) | Bsin-PaaS 后台dubbo工程脚手架 |             |  
|  bsin-paas-doc  |  是   |   [bsin-paas-doc](https://gitee.com/s11e-DAO/bsin-paas-doc)   | Bsin-PaaS 帮助文档中心       |             |  

## 中间件

|      中间件      |   版本    |                         地址                          | 功能简介 | port                 |   
|:-------------:|:-------:|:---------------------------------------------------:|:-----|:---------------------|  
|     mysql     |         | [mysql](https://gitee.com/s11e-DAO/bisn-server-env) |      | 3306                 |  
|     redis     |         |  [redis](https://gitee.com/s11e-DAO/bsin-paas-doc)  |      | 6379                 |  
|     nacos     | 2.4.2.1 |  [redis](https://nacos.io/download/nacos-server/)   |      | 8848                 |  
|   zookeeper   |         |  [redis](https://gitee.com/s11e-DAO/bsin-paas-doc)  |      |                      |  
|     seata     |         |  [redis](https://gitee.com/s11e-DAO/bsin-paas-doc)  |      |                      |  
|   rabbitmq    |         |  [redis](https://gitee.com/s11e-DAO/bsin-paas-doc)  |      | 9876                 |  
|    milvus     |  2.4.5  |  [redis](https://gitee.com/s11e-DAO/bsin-paas-doc)  |      | 9001 19530 9091 3000 |  
| Elasticsearch | 8.15.0  |  [redis](https://gitee.com/s11e-DAO/bsin-paas-doc)  |      | 9200                 |  