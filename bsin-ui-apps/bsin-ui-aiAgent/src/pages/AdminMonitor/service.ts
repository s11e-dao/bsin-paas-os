import { request } from '@umijs/max'
let aiAgent = process.env.contextPath_aiAgent;

// 列表数据请求
export const getWechatLoginList = (params) => {
  return request(aiAgent + '/wxPlatform/getLoginList', {
    serviceName: 'WxPlatformService',
    methodName: 'getLoginList',
    version: '1.0',
    bizParams: {
      ...params,
    },
  })
}

export const getAllFunctionSubscribeList = (params) => {
  return request(aiAgent + '/aiCustomerFunction/getAllFunctionSubscribeList', {
    serviceName: 'AiCustomerFunctionService',
    methodName: 'getAllFunctionSubscribeList',
    version: '1.0',
    bizParams: {
      ...params,
    },
  })
}

// 审核功能服务订阅订单
export const auditFunctionServiceOrder = (params) => {
  return request(aiAgent + '/aiCustomerFunction/auditOrder', {
    serviceName: 'AiCustomerFunctionService',
    methodName: 'auditOrder',
    version: '1.0',
    bizParams: {
      ...params,
    },
  })
}
