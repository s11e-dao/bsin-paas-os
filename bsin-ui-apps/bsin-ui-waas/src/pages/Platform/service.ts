import { request } from '@umijs/max'
let crmPath = process.env.contextPath_crm;

// 分页查询
export const getPlatformPageList = (params) => {
  return request('/list', {
    serviceName: 'PlatformService',
    methodName: 'getPageList',
    version: '1.0',
    bizParams: {
      ...params,
    },
  });
};

// 添加平台
export const addPlatform = (params) => {
  return request('/add', {
    serviceName: 'PlatformService',
    methodName: 'openTenant',
    version: '1.0',
    bizParams: {
      ...params,
    },
  });
};

// 删除
export const deletePlatform = (params) => {
  return request('/del', {
    serviceName: 'PlatformService',
    methodName: 'delete',
    version: '1.0',
    bizParams: {
      ...params,
    },
  });
};

// 查询详情
export const getPlatformDetail = (params) => {
  console.log('params', params);
  return request('/view', {
    serviceName: 'PlatformService',
    methodName: 'getDetail',
    version: '1.0',
    bizParams: {
      ...params,
    },
  });
};

// 查询产品列表
export const getProductList = (params) => {
  return request('/getList', {
    serviceName: 'ProductService',
    methodName: 'getList',
    bizParams: {
      ...params,
    },
  });
};

