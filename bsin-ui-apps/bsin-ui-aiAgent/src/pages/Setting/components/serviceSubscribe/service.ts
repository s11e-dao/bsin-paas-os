import { request } from 'umi'

// 分页查询
export const getMerchantAppList = (params) => {
  return request('/list', {
    serviceName: 'OrgService',
    methodName: 'getAppListByOrgCode',
    bizParams: {
      ...params,
    },
  })
}

export const getMerchantAuthorizableAppList = (params) => {
  return request('/list', {
    serviceName: 'OrgService',
    methodName: 'getAuthorizableAppListByOrgCode',
    bizParams: {
      ...params,
    },
  })
}

// 商户授权服务查询
export const subscribeApps = (params) => {
  return request('/list', {
    serviceName: 'OrgService',
    methodName: 'authorizeMercahntOrgApps',
    bizParams: {
      ...params,
    },
  })
}

// 商户订阅服务
export const getServiceSubscribePageList = (params) => {
  return request('/list', {
    serviceName: 'ServiceSubscribeService',
    methodName: 'getPageList',
    bizParams: {
      ...params,
    },
  })
}

// 可订阅列表(上架的功能)
export const getAiFunctionSubscribablePageList = (params) => {
  return request('/list', {
    serviceName: 'AiCustomerFunctionService',
    methodName: 'getSubscribableList',
    version: '1.0',
    bizParams: {
      ...params,
    },
  })
}
// 商户订阅AI功能服务列表
export const getAiFunctionSubscribePageList = (params) => {
  return request('/list', {
    serviceName: 'AiCustomerFunctionService',
    methodName: 'getPageList',
    version: '1.0',
    bizParams: {
      ...params,
    },
  })
}

// 商户订阅AI功能服务详情
export const getAiFunctionSubscribeDetail = (params) => {
  return request('/list', {
    serviceName: 'AiCustomerFunctionService',
    methodName: 'getDetail',
    version: '1.0',
    bizParams: {
      ...params,
    },
  })
}

// 创建提交功能服务订阅订单
export const createFunctionServiceOrder = (params) => {
  return request('/list', {
    serviceName: 'AiCustomerFunctionService',
    methodName: 'createOrder',
    version: '1.0',
    bizParams: {
      ...params,
    },
  })
}


// 查询详情
export const getSubscribeDetail = (params) => {
  console.log('params', params)
  return request('/view', {
    serviceName: 'MerchantService',
    methodName: 'getDetail',
    version: '1.0',
    bizParams: {
      ...params,
    },
  })
}
