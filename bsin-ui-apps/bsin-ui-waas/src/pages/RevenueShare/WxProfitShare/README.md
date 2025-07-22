# 微信分账功能

## 功能概述

微信分账功能是一个完整的后台管理界面，用于管理微信支付的分账操作。该功能基于微信支付官方分账API，提供了完整的业务流程管理。

## 主要功能

### 1. 平台应用管理
- **应用列表查询**: 展示所有可配置分账的平台应用
- **应用状态显示**: 显示应用的启用/停用状态
- **应用配置**: 选择应用进行分账配置

### 2. 分账操作功能

#### 核心分账操作
- **请求分账**: 向微信支付发起分账请求
- **查询分账结果**: 查询分账请求的处理结果
- **分账回退**: 请求分账回退，将已分账资金退回
- **查询回退结果**: 查询分账回退请求的处理结果

#### 资金管理
- **解冻剩余资金**: 解冻订单中剩余未分账资金
- **查询剩余金额**: 查询订单中剩余可分账金额

#### 接收方管理
- **添加分账接收方**: 添加分账接收方信息
- **删除分账接收方**: 删除已添加的分账接收方

#### 账单管理
- **申请分账账单**: 申请分账账单文件
- **下载账单**: 下载分账账单文件

### 3. 操作历史记录
- **操作日志**: 记录所有分账操作的详细信息
- **请求参数**: 显示每次操作的请求参数
- **响应结果**: 显示每次操作的响应结果
- **操作状态**: 显示操作的成功/失败状态
- **时间记录**: 记录操作的具体时间

## 技术架构

### 前端技术栈
- **React**: 前端框架
- **Ant Design Pro**: UI组件库
- **TypeScript**: 类型安全的JavaScript
- **ProTable**: 数据表格组件
- **ProForm**: 表单组件

### 后端接口
- **接口定义**: `ProfitSharingApiService`
- **接口实现**: `ProfitSharingApiServiceImpl`
- **接口风格**: 统一使用bsin-paas的Dubbo服务调用风格

## 文件结构

```
WxProfitShare/
├── index.tsx          # 主页面组件
├── service.ts         # API接口定义
├── data.tsx          # 数据配置（列定义、表单字段等）
└── README.md         # 功能说明文档
```

## API接口列表

### 接口调用风格
所有接口都采用bsin-paas统一的Dubbo服务调用风格：

```typescript
return request(waasPath + '/servicePath', {
  serviceName: 'ServiceName',
  methodName: 'methodName',
  version: '1.0',
  bizParams: {
    // 业务参数
  },
});
```

### 1. 平台应用相关接口

#### 查询平台应用列表
- **服务名**: `BizRoleAppService`
- **方法名**: `getPageList`
- **路径**: `/bizRoleApp/getPageList`
- **功能**: 分页查询平台应用列表

#### 获取支付通道配置
- **服务名**: `PayChannelConfigService`
- **方法名**: `getBizRoleAppPayChannelConfig`
- **路径**: `/payChannelConfig/getBizRoleAppPayChannelConfig`
- **参数**: `{ bizRoleAppId, payChannelCode }`
- **功能**: 获取指定应用的支付通道配置

### 2. 微信分账API接口

#### 请求分账
- **服务名**: `ProfitSharingApiService`
- **方法名**: `requestProfitShare`
- **路径**: `/profitShare/request`
- **参数**: `{ transactionNo: string }`
- **功能**: 向微信支付发起分账请求

#### 查询分账结果
- **服务名**: `ProfitSharingApiService`
- **方法名**: `queryProfitShareResult`
- **路径**: `/profitShare/query`
- **参数**: `{ transactionNo: string }`
- **功能**: 查询分账请求的处理结果

#### 请求分账回退
- **服务名**: `ProfitSharingApiService`
- **方法名**: `requestProfitShareReturn`
- **路径**: `/profitShare/return`
- **参数**: `{ orderId, outReturnNo, returnMchid, amount, description }`
- **功能**: 请求分账回退，将已分账资金退回

#### 查询分账回退结果
- **服务名**: `ProfitSharingApiService`
- **方法名**: `queryProfitShareReturnResult`
- **路径**: `/profitShare/returnQuery`
- **参数**: `{ orderId, outReturnNo }`
- **功能**: 查询分账回退请求的处理结果

#### 解冻剩余资金
- **服务名**: `ProfitSharingApiService`
- **方法名**: `unfreezeRemainingFunds`
- **路径**: `/profitShare/unfreeze`
- **参数**: `{ transactionId, outOrderNo, description? }`
- **功能**: 解冻订单中剩余未分账资金

#### 查询剩余待分金额
- **服务名**: `ProfitSharingApiService`
- **方法名**: `queryRemainingAmount`
- **路径**: `/profitShare/remaining`
- **参数**: `{ transactionId }`
- **功能**: 查询订单中剩余可分账金额

#### 添加分账接收方
- **服务名**: `ProfitSharingApiService`
- **方法名**: `addProfitShareReceiver`
- **路径**: `/profitShare/addReceiver`
- **参数**: `{ receiverId, receiverName, receiverType, relationType, customRelation? }`
- **功能**: 添加分账接收方信息

#### 删除分账接收方
- **服务名**: `ProfitSharingApiService`
- **方法名**: `deleteProfitShareReceiver`
- **路径**: `/profitShare/deleteReceiver`
- **参数**: `{ receiverId }`
- **功能**: 删除已添加的分账接收方

#### 申请分账账单
- **服务名**: `ProfitSharingApiService`
- **方法名**: `applyProfitShareBill`
- **路径**: `/profitShare/applyBill`
- **参数**: `{ billDate, tarType }`
- **功能**: 申请分账账单文件

#### 下载账单
- **服务名**: `ProfitSharingApiService`
- **方法名**: `downloadProfitShareBill`
- **路径**: `/profitShare/downloadBill`
- **参数**: `{ billDate, tarType }`
- **功能**: 下载分账账单文件

## 使用流程

### 1. 选择应用
1. 在"平台应用"标签页中查看应用列表
2. 点击"配置分账"按钮选择要配置的应用
3. 系统自动获取该应用的支付通道配置

### 2. 配置分账
1. 切换到"分账配置"标签页
2. 查看应用配置信息和支付通道配置
3. 根据业务需求选择相应的分账操作

### 3. 执行分账操作
1. 点击相应的操作卡片（如"请求分账"）
2. 在弹出的表单中填写必要参数
3. 点击"确定"执行操作
4. 查看操作结果和历史记录

### 4. 查看操作历史
1. 切换到"操作历史"标签页
2. 查看所有操作的详细记录
3. 可以清空历史记录

## 表单字段说明

### 接收方类型 (receiverType)
- `MERCHANT_ID`: 商户号
- `PERSONAL_OPENID`: 个人openid

### 分账关系 (relationType)
- `STORE_OWNER`: 店主
- `STAFF`: 员工
- `PARTNER`: 合作伙伴
- `BRAND`: 品牌方
- `DISTRIBUTOR`: 分销商
- `USER`: 用户
- `SUPPLIER`: 供应商
- `CUSTOM`: 自定义

### 压缩类型 (tarType)
- `GZIP`: GZIP压缩
- `LZMA`: LZMA压缩

## 错误处理

### 常见错误
1. **参数验证失败**: 检查必填参数是否完整
2. **交易记录不存在**: 确认交易单号是否正确
3. **网络请求失败**: 检查网络连接和服务器状态
4. **权限不足**: 确认用户是否有操作权限

### 错误响应格式
```json
{
  "code": 1,
  "message": "错误信息"
}
```

## 注意事项

1. **参数格式**: 确保所有参数格式正确，特别是日期和金额
2. **操作顺序**: 分账操作需要按照正确的顺序执行
3. **幂等性**: 重复操作不会产生副作用
4. **日志记录**: 所有操作都会被记录，便于问题排查
5. **权限控制**: 确保用户有相应的操作权限
6. **接口风格**: 统一使用bsin-paas的Dubbo服务调用风格

## 扩展功能

### 未来计划
1. **批量操作**: 支持批量分账操作
2. **定时任务**: 支持定时分账
3. **报表统计**: 分账数据统计和报表
4. **通知机制**: 分账结果通知
5. **权限管理**: 更细粒度的权限控制

## 技术支持

如有问题，请联系开发团队或查看相关文档：
- 微信支付分账文档: https://pay.weixin.qq.com/doc/v3/merchant/4012067962
- 项目文档: 查看项目根目录的README文件 