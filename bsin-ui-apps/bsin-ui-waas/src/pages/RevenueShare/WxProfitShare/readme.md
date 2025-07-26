## 微信分账API列表
- 请求分账
- 查询分账结果
- 请求分账回退
- 查询分账回退结果
- 解冻剩余资金
- 查询剩余待分金额
- 添加分账接收方
- 删除分账接收方
- 分账动态通知
- 申请分账账单
- 下载账单

请在 WxProfitShare 目录下实现微信分账后台管理UI，
1.支持查询出当前添加的平台应用
平台应用查询接口：/crm/bizRoleApp/getPageList
接口返回示例如下：
{
    "code": 0,
    "data": [
        {
            "agentId": null,
            "mchId": null,
            "corpId": null,
            "appName": "2222",
            "appDescription": "2",
            "updateTime": "2025-07-20 15:17:16",
            "delFlag": 0,
            "appChannel": "4",
            "serialNo": "1946831760809660416",
            "token": "2",
            "createTime": "2025-07-20 15:17:16",
            "appId": "222",
            "tenantId": "6345824413764157440",
            "bizRoleTypeNo": "1535984304528691200",
            "notifyUrl": "2",
            "aesKey": "2",
            "appSecret": "2",
            "bizRoleType": "1",
            "status": "0"
        },
        {
            "agentId": null,
            "mchId": null,
            "corpId": null,
            "appName": "微信分账测试应用",
            "appDescription": "测试微信商户支付的微信小程序应用",
            "updateTime": "2025-07-19 23:29:15",
            "delFlag": 0,
            "appChannel": "4",
            "serialNo": "1946593180883488768",
            "token": "123",
            "createTime": "2025-07-19 23:29:15",
            "appId": "123",
            "tenantId": "6345824413764157440",
            "bizRoleTypeNo": "1535984304528691200",
            "notifyUrl": "123",
            "aesKey": "123",
            "appSecret": "123",
            "bizRoleType": "1",
            "status": "0"
        }
    ],
    "message": "操作成功",
    "pagination": {
        "pageNum": 1,
        "pageSize": 1,
        "totalSize": 2
    }
}
2.微信支付商户从平台应用配置接口中获取
平台应用配置接口：/waas/payChannelConfig/getBizRoleAppPayChannelConfig
请求参数：
{
bizRoleAppId: "123"
payChannelCode:"brandsPoint"
}
接口返回支付通道参数示例如下：
{
    "code": 0,
    "data": {
        "specialMerchantParams": null,
        "serviceSubMerchantParams": "{\"serviceMchId\":\"广东佛山\",\"subMchId\":\"广东佛山\",\"serviceAppId\":\"广东佛山\",\"serviceKey\":\"官大\",\"subNotifyUrl\":\"广东佛山\"}",
        "isNormalMerchantMode": true,
        "remark": null,
        "updateTime": "2025-07-20 20:51:53.051",
        "bizRoleAppId": "123",
        "params": "{\"isNormalMerchantMode\":\"1\",\"isIsvSubMerchantMode\":\"1\",\"normalMerchantParams\":{\"mchId\":\"萨达\",\"appId\":\"发大水发\",\"appSecret\":\"防守打法\",\"oauth2Url\":\"防守打法\",\"apiVersion\":\"V2\",\"key\":\"分身\",\"apiV3Key\":\" 发顺丰\",\"notifyUrl\":\"DSADA \",\"keyPath\":\"DSAD \"},\"serviceSubMerchantParams\":{\"serviceMchId\":\"发多少\",\"subMchId\":\"分身\",\"serviceAppId\":\"的发撒\",\"serviceKey\":\"防守打法\",\"subNotifyUrl\":\"防守打法\"}}",
        "serialNo": "1946800877729878016",
        "createBy": null,
        "normalMerchantParams": "{\"mchId\":\"发多少\",\"appId\":\"供电公司\",\"appSecret\":\"鬼地方个\",\"oauth2Url\":\"鬼地方个\",\"apiVersion\":\"V3\",\"key\":\"给对方\",\"apiV3Key\":\"给对方\",\"notifyUrl\":\"给对方\",\"keyPath\":\"官ga\"}",
        "createTime": "2025-07-20 13:14:33.876",
        "payChannelCode": "wxpay",
        "updateBy": null,
        "tenantId": "6345824413764157440",
        "feeRatio": 0.000000,
        "isIsvSubMerchantMode": true,
        "status": "0"
    },
    "message": "操作成功",
    "pagination": null
}
3. 应用这个支付通道参数，实现微信分账的API接口管理

