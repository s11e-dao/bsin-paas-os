# jiujiu CRM

  **文档版本**

| 版本号 | 修改日期       | 编写   | 修改内容                     | 备注 |
| ------ |------------| ------ | ---------------------------- | ---- |
| V1.0.0 | 2023/07/01 | bolei | 新建                         |      |


## 一、相关术语

| 序号 | 术语/缩略语 | 全称和说明                                                   |
| ---- |--------| ----------------------------------------------------------- |
| 1    | CRM    | 客户关系管理（Customer Relationship Management）                  |


## 二、业务介绍


## 三、技术框架
- 端口说明

|                | port  | 备注 |
|----------------|-------| --- |
| server         | 8072  |      | 
| sofa rest      | 8342  |      | 
| sofa bolt      | 12202 |      | 
| shenyu gateway | 9195  |      | 


- [swagger接口](https://www.sofastack.tech/projects/sofa-rpc/restful-swagger/)
>* 应用启动后，访问 http://localhost:8342/swagger/openapi 即可得到当前的应用发布的所有的 RESTful 的服务的信息

## 曲线积分铸造流程说明
1、任务审核通过根据任务权益的激励数量铸造曲线积分
2、通过切面监听曲线积分铸造
3、监听积分铸造判断是否达到触发token释放分配条件
4、达到条件则根据token释放条件配置执行分配
5、token分配给持有pass card的成员对应的账户

## 微信小程序和公众号对接
https://github.com/Wechat-Group/WxJava