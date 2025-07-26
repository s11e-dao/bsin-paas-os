# 支付通道接口定义描述自动填充功能

## 功能概述

在添加或编辑支付通道时，系统会根据选择的通道代码和商户模式自动填充相应的接口定义描述参数配置。

## 支持的支付通道

- **微信支付 (wxpay)**
- **支付宝支付 (alipay)**
- **品牌积分支付 (brandspointpay)**
- **火钻支付 (firediamondpay)**

## 商户模式

### 1. 普通商户模式
- 适用于直接与支付平台对接的商户
- 需要配置基本的支付参数

### 2. 服务商子商户模式
- 适用于通过服务商接入的商户
- 包含服务商参数和特约商户参数两部分配置

## 参数配置文件

系统使用以下JSON文件作为参数配置模板：

### 微信支付参数配置
- `isv_interface_params_jeepay_wxPay.json` - 服务商子商户模式参数
- `normal_merchant_interface_params_jeepay_wxPay.json` - 普通商户模式参数
- `special_merchant_interface_params_jeepay_wxPay.json` - 特约商户参数

### 支付宝参数配置
- `isv_interface_params_jeepay_aliPay.json` - 服务商子商户模式参数
- `normal_merchant_interface_params_jeepay_aliPay.json` - 普通商户模式参数
- `special_merchant_interface_params_jeepay_aliPay.json` - 特约商户参数

## 自动填充逻辑

### 新增支付通道时
1. 选择通道代码后，自动填充对应的参数配置
2. 根据商户模式选择，填充相应的参数定义描述
3. 支持同时配置多种商户模式

### 编辑支付通道时
1. 如果已有参数配置，保持原有配置
2. 如果没有参数配置，自动填充默认配置
3. 修改商户模式时，动态更新参数配置

### 商户模式切换
1. **启用普通商户模式**：自动填充普通商户参数配置
2. **禁用普通商户模式**：清空普通商户参数配置
3. **启用服务商子商户模式**：自动填充服务商和特约商户参数配置
4. **禁用服务商子商户模式**：清空服务商和特约商户参数配置

## 参数配置格式

参数配置采用JSON格式，包含以下字段：

```json
{
    "name": "参数名称",
    "desc": "参数描述",
    "type": "参数类型",
    "verify": "验证规则",
    "values": "可选值",
    "titles": "可选值标题",
    "star": "是否敏感信息"
}
```

### 参数类型说明
- `text`: 文本输入
- `textarea`: 多行文本输入
- `radio`: 单选按钮
- `file`: 文件上传

### 验证规则
- `required`: 必填项
- `optional`: 可选项

## 使用示例

1. **新增微信支付通道**
   - 选择通道代码：微信支付
   - 启用普通商户模式：自动填充微信支付普通商户参数
   - 启用服务商子商户模式：自动填充微信支付服务商和特约商户参数

2. **新增支付宝支付通道**
   - 选择通道代码：支付宝支付
   - 启用普通商户模式：自动填充支付宝普通商户参数
   - 启用服务商子商户模式：自动填充支付宝服务商和特约商户参数

## 扩展说明

如需添加新的支付通道支持，需要：

1. 在 `PARAMS_CONFIG_MAP` 中添加新的通道配置
2. 创建对应的参数配置文件
3. 在通道代码选择器中添加新的选项

```javascript
const PARAMS_CONFIG_MAP = {
    wxpay: {
        normal: normalWxPayParams,
        isv: isvWxPayParams,
        special: specialWxPayParams,
    },
    alipay: {
        normal: normalAliPayParams,
        isv: isvAliPayParams,
        special: specialAliPayParams,
    },
    // 新增支付通道
    newPayChannel: {
        normal: normalNewPayParams,
        isv: isvNewPayParams,
        special: specialNewPayParams,
    },
};
``` 