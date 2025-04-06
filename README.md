# 一站式企业数字化开发平台
[toc]

## 开源项目介绍
Bsin-PaaS（毕昇） 是一套企业级的RWA和AI应用搭建平台，可帮助企业快速搭建基于云原生的有竞争力的业务中台，智能决策中台、流程中台、企业AI知识库、业务前台和RWA应用。bsin-paas包括微前端设计、微服务框架、服务编排、工作流引擎、安全网关及区块链引擎。该方案由区块链(公链、联盟链)作为技术支撑,为企业提供daPaaS层的一站式解决方案，助力企业打造数字经济底层技术架构，构建一套开放式和生态化的技术体系。作为一个平台，Bsin-PaaS本身拥有自己的数字资产，以用户为中心，实现让价值掌握在拥有者手中，帮企业构建一种全新的数字化商业生态模式。

## 产品优势：
* 开箱即用
* 生态应用独立开发、部署、运行
* 生态应用丰富：配套丰富的企业级业务应用，生态应用持续完善
* 产品持续迭代
* 数字经济底座

## 设计理念
* 统一企业技术路线
* 统一数字企业IT架构
* 统一企业开发流程
* 统一开发资源库

## 数字化转型三部曲
> bsin-paas 可以快速用于搭建企业技术中台

![avatar](./doc/images/sys/数字化转型三部曲.png)

## 项目演示

>bsin-paas平台 租户:bsin-paas 账户:admin 123456
```
http://operation.flyray.me/
演示环境暂时无法访问

```
> 演示视频

https://www.bilibili.com/video/BV1VM4y1a7PF/?vd_source=360ae092de70c0b6c577d1d26f3565fe

## 平台定位
提供daPaaS层的一站式企业级技术解决方案，帮助企业快速实现数字商业创新，完成数字化转型。

![avatar](./doc/images/architecture/bsin-paas定位.png)

## 平台总架构设计
![avatar](./doc/images/architecture/平台总架构设计.png)

## 逻辑架构示图
![avatar](./doc/images/architecture/架构设计.png)

## 系统业务角色定义
![avatar](./doc/images/architecture/bizRole.jpg)

## 应用业务流程视图
![avatar](./doc/images/sys/应用业务流程视图.png)

## 平台架构基于DDD设计理念
![avatar](./doc/images/sys/DDD设计理念.png)

## 区块链应用架构
![avatar](./doc/images/sys/区块链应用架构.png)

### 区块链钱包架构图
![avatar](./doc/images/architecture/区块链钱包架构图.png)

## 基于大语言模型的AI应用架构图
![avatar](./doc/images/architecture/基于大语言模型的AI应用架构图.png)

### AI交互流程
![avatar](./doc/images/sys/基于大语言模型的AI应用架构图.png)

## 智能决策引擎架构图
![avatar](./doc/images/architecture/智能决策引擎架构图.png)

## 业务场景架构图
![avatar](./doc/images/architecture/业务场景架构图.png)

## 业务能力示意图
![avatar](./doc/images/sys/业务示意图.png)

## 业务场景应用架构图
![avatar](./doc/images/architecture/业务场景应用架构图.png)

## bsin-paas-mvp
![avatar](./doc/images/sys/bsin-paas-mvp.png)

## 后端开发脚手架
![avatar](./doc/images/sys/后端开发脚手架.png)

![avatar](./doc/images/sys/DDD设计依赖关系.png)

## 前端开发脚手架
![avatar](./doc/images/sys/前端开发模式.png)

## 微前端设计
![avatar](./doc/images/sys/微前端设计.png)


## 工程介绍

```
bsin-paas-os
├── bsin-paas-dependencies -- 系统全局maven依赖
├── bsin-common-all -- 系统公共模块
|    ├── bsin-common-blockchain -- 区块链工具类
|    ├── bsin-common-redis -- redis工具类
|    ├── bsin-common-mq -- 消息队列工具类
|    ├── bsin-common-oss -- 对象存储工具类
|    ├── bsin-common-payment -- 支付工具类
|    ├── bsin-common-sms -- 短信工具类
|    ├── bsin-common-third-auth -- 第三方授权
|    ├── bsin-common-utils -- 基础工具类
|    └── bsin-common-security -- 安全工具类
├── bsin-targe-gateway -- 网关
├── bsin-targe-gateway-admin -- 网关管理后台
├── bsin-apps-container -- 微前端基座
├── bsin-server-apps -- 子应用后端（不同业务系统应用集）
|    ├── bsin-server-upms -- 通用权限管理
|    ├── bsin-server-waas -- 钱包即服务
|    ├── bsin-server-workflow -- 工作流引擎服务
|    ├── bsin-server-workflow-admin -- 工作流引擎管理
|    ├── bsin-server-brms -- 智能决策引擎
|    ├── bsin-server-search -- 搜索引擎服务：知识库向量搜索和通用搜索
|    ├── bsin-server-ai-agent -- AI引擎
|    └── bsin-server-orchestration -- 服务编排
├── bsin-ui-apps -- 子应用前端（不同业务系统应用集）
|    ├── bsin-ui-upms -- 通用权限管理
|    ├── bsin-ui-waas -- 钱包即服务
|    ├── bsin-ui-decision-admin -- 智能决策引擎：流程、规则、表单
|    ├── bsin-ui-ai-agent -- AI引擎
|    └── bsin-ui-orchestration -- 服务编排
├── bsin-app-scaffold --  c端应用脚手架
|    ├── bsin-app-minApp -- 小程序脚手架
|    ├── bsin-app-android -- android脚手架
|    └── bsin-app-ios -- ios脚手架
├── bsin-server-scaffold -- 后端开发脚手架
|    ├── bsin-server-scaffold-sofa -- sofa脚手架
|    ├── bsin-server-scaffold-dubbo -- dubbo脚手架
|    └── bsin-server-scaffold-spring-cloud -- spring-cloud脚手架
└── bsin-ui-scaffold -- 前端开发脚手架
     ├── bsin-ui-vue -- vue脚手架
     └── bsin-ui-react -- react脚手架
```
- [代码仓库介绍](./doc/repository.md)

> 帮助文档
* http://help.flyray.me

## bsin-paas总体规划
![avatar](./doc/images/sys/bsin-paas体系.png)

## UI展示
* 登录页
![avatar](./doc/images/sys/login.png)

* 首页工作台
![avatar](./doc/images/sys/工作台.png)

* 主题设置
![avatar](./doc/images/sys/主题设置.png)

* 新主题工作台
![avatar](./doc/images/sys/bsin-thems.png)
![avatar](./doc/images/sys/bsin-thems1.png)

* 菜单主题
![avatar](./doc/images/sys/菜单主题.png)

* 菜单主题上
![avatar](./doc/images/sys/菜单主题上.png)

* 菜单主题
![avatar](./doc/images/sys/菜单主题1.png)

* 菜单主题
![avatar](./doc/images/sys/菜单主题2.png)

* app-agent
![avatar](./doc/images/sys/app-agent.png)

* 权限管理
![avatar](./doc/images/sys/权限管理.png)

* 工作流引擎
![avatar](./doc/images/sys/工作流引擎.png)

* 工作流画布
![avatar](./doc/images/sys/工作流画布.png)

* 智能决策引擎
![avatar](./doc/images/sys/智能决策引擎.png)

* 通用人工智能
![avatar](./doc/images/sys/通用人工智能模块.png)

* AI员工.png
![avatar](./doc/images/sys/AI员工.png)

* 火源钱包
  ![avatar](./doc/images/sys/火源钱包.png)

  ![avatar](./doc/images/sys/aiWorkflow.png)

* bsin-bot低代码平台
![avatar](./doc/images/sys/前端低代码.png)

* bsin-bot 表单设计
![avatar](./doc/images/sys/form.png)

* 数字资产管理平台
![avatar](./doc/images/sys/数字资产管理平台.png)

* 数据大屏
![avatar](./doc/images/sys/数据大屏.png)

* react 不同子应用相互嵌套展示
![avatar](./doc/images/sys/react应用嵌套.png)

* AI 产品应用
![avatar](./doc/images/ui/vuca-ui-1.png)

![avatar](./doc/images/ui/vuca-ui-2.png)

![avatar](./doc/images/ui/vuca-ui-3.png)

![avatar](./doc/images/ui/vuca-ui-4.png)

## 开源协议

apache license 2.0

* 允许免费用于学习.
* 商业用途需要授权.
* 对未经过授权进行二次开源或者商业化的将追究法律责任.

## 文章署名格式
>#本文作者#
> s11eDao

## 项目团队
Bsin-PaaSDAO是一个围绕Bsin-PaaS开源框架，以技术驱动，以共建、共治、共享为理念的DAO组织，主要任务是持续迭代Bsin-PaaS框架，为企业提供商业化服务，实现企业的降本增效

s11eDAO是一个专注于Web3.0商业构建的数字化社区。社区拥有NFT徽章体系、联合曲线数字积分和独特的DAO治理经济模型。社区旨在做一款有温度的工具，帮助实体品牌实现Web3.0，为实体品牌提供一站式的Web3.0品牌构建服务。

### 开源贡献者
* [@博羸](https://gitee.com/boleixiongdi)
* [@leonard](https://gitee.com/leijiwu)
* [@arrowspider](https://gitee.com/arrowspider)

## 个人开发着授权名单
* [PiritXu](https://gitee.com/XHKai)
* [Ale-Domisholy](https://gitee.com/domisholy)
* [keep-klaus_pd](https://gitee.com/klaus_pd)


注：非授权商用举报直接奖励10000元

## 企业开发者授权名单
* [火源社区](https://eeihz6cbu0.feishu.cn/docx/HYYfdCmxEovMpix4GkDc5qlPnSe)
* [上海某公司（匿名）]()
* [元星牵引（深圳）互联网技术有限](https://gitee.com/xt-justing)


注：非授权商用举报直接奖励50000元

## 专题培训：
* 工程部署
* shenyu网关配置
* upms权限模型
* crm 业务角色模型及账户模型

## 技术交流

微信群

![avatar](./doc/images/微信群.png)

微信公众号

![avatar](./doc/images/bsin-paas公众号.jpg)

## 版本对比

| 功能清单        | 开源版 | 个人版| 企业版 |旗舰版 |
|:------------| ----: |:---:|:----:|:----: |
| 前端基座        | ✅ |  ✅ |  ✅  | ✅ |
| 网关          | ✅ |  ✅  |  ✅ | ✅ |
| 权限管理        | ✅ |  ✅  |  ✅ | ✅ |
| 客户中心        | ❌ |  ✅  |  ✅ | ✅ |
| 智能决策:规则引擎   | ❌ |  ❌  |  ✅ | ✅ |
| 智能决策:工作流程引擎 | ❌ |  ✅  |  ✅ | ✅ |
| 智能决策:表单引擎   | ❌ |  ✅  |  ✅ | ✅ |
| AI知识库       | ❌ |  ✅  |  ✅ | ✅ |
| 区块链引擎       | ❌ |  ❌  |  ❌ | ✅ |
| 两次培训服务      | ❌ |  ❌  |  ✅ | ✅ |
| 一个版本一次的咨询培训 | ❌ |  ❌  |  ❌ | ✅ |


## 模块功能清单
<table>
    <thead>
        <tr>
            <th>子应用</th>
            <th>功能模块</th>
            <th>描述</th>
        </tr>
    </thead>
    <tbody>
        <!-- 权限管理 -->
        <tr>
            <td rowspan="7">权限管理</td> <!-- 合并3行 -->
            <td>子应用管理</td>
            <td>微前端子应用的管理</td>
        </tr>
        <tr>
            <td>租户管理</td>
            <td>接入租户的管理</td>
        </tr>
        <tr>
            <td>机构管理</td>
            <td>组织架构中的机构部门管理</td>
        </tr>
        <tr>
            <td>岗位管理</td>
            <td>租户下的所有岗位管理</td>
        </tr>
        <tr>
            <td>角色管理</td>
            <td>应用下的角色</td>
        </tr>
        <tr>
            <td>菜单管理</td>
            <td>应用的菜单管理</td>
        </tr>
        <tr>
            <td>操作日志</td>
            <td>对接口调用的操作日志</td>
        </tr>
        <!-- 客户中心 -->
        <tr>
            <td rowspan="7">客户中心</td> <!-- 合并2行 -->
            <td>平台管理</td>
            <td>接入系统的平台，对应一个租户</td>
        </tr>
        <tr>
            <td>代理商管理</td>
            <td>系统代理商</td>
        </tr>
        <tr>
            <td>商户管理</td>
            <td>租户下的商户管理</td>
        </tr>
        <tr>
            <td>客户管理</td>
            <td>租户下的客户管理</td>
        </tr>
        <tr>
            <td>数字资产管理</td>
            <td>基于区块链发行的数字资产</td>
        </tr>
        <tr>
            <td>账户管理</td>
            <td>基于基础会计科目的账户管理：积分账户，余额账户，卡账户</td>
        </tr>
        <tr>
            <td>支付</td>
            <td>微信支付、支付宝支付</td>
        </tr>
        <!-- 智能决策 -->
        <tr>
            <td rowspan="5">智能决策</td> <!-- 合并2行 -->
            <td>事件管理</td>
            <td>触发决策的事件的管理</td>
        </tr>
        <tr>
            <td>模型管理</td>
            <td>工作流模型、规则模型、表单模型</td>
        </tr>
        <tr>
            <td>事件模型</td>
            <td>给事件配置触发的模型</td>
        </tr>
        <tr>
            <td>模型设计</td>
            <td>工作流编排、表单设计、规则设计（可视化设计）</td>
        </tr>
        <tr>
            <td>我的待办</td>
            <td>审批待办事项</td>
        </tr>
        <!-- AI知识库 -->
        <tr>
            <td rowspan="6">AI知识库</td> <!-- 合并2行 -->
            <td>大模型管理</td>
            <td>管理接入的大模型</td>
        </tr>
        <tr>
            <td>提示词模板</td>
            <td>定义提示词模板</td>
        </tr>
        <tr>
            <td>知识库</td>
            <td>创建知识库和知识库对应文档</td>
        </tr>
        <tr>
            <td>智能体</td>
            <td>智能体管理</td>
        </tr>
        <tr>
            <td>智能体编排</td>
            <td>自定义智能体的流程和功能</td>
        </tr>
    </tbody>
</table>

## 常见问题

一、bsin-paas个人授权和企业授权区别
* 1、价格上的区别，个人授权价格1500，企业授权8000
* 2、使用方式的区别：
  * 个人授权可以商用，但是不能将bsin-paas底座和中间件源码的方式给甲方（jar形式和前端编译好交付）
  *  企业授权可以源码交付甲方，授权的甲方不能在源码授权给任何第三方，需要授权单独购买
* 3、都是一次授权永久升级更新
* 4、都是一次授权一个开发者或一家企业

二、bsin-paas个人授权与企业授权的区别

1.价格区别：
- 个人授权：价格为1500元。
- 企业授权：价格为8000元。

2.使用方式区别：
- 个人授权：可用于商业用途，但不能以源码形式（底座和中间件源码）交付给第三方，只能以打包好的jar文件或编译后的前端代码形式交付。
- 企业授权：允许将源码交付给甲方使用，但甲方不得将源码授权给任何第三方。如果需要授权第三方，需单独购买授权。

3.升级与更新：一次性授权，永久享受升级和更新服务。

4.授权范围：授权限于一个开发者或一家企业。

三、什么是珊瑚计划（共同富裕）
* 每个帮助bsin-paas生态建设的用户，通过自己授权出去的企业license，可以跟项目团队4:6分成，具体例子就是，一个企业通过你获得8000元版本的授权后，你可以获得3200元的利益，剩余4800元属于bsin-paas团队