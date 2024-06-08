import { request } from '@umijs/max'
let crmPath = process.env.contextPath_crm;
let upmsPath = process.env.contextPath_upms;

// 分页查询
export const getPlatformPageList = (params) => {
  return request(crmPath + '/platform/getPageList', {
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
  return request(crmPath + '/platform/create', {
    serviceName: 'PlatformService',
    methodName: 'create',
    version: '1.0',
    bizParams: {
      ...params,
    },
  });
};

// 删除
export const deletePlatform = (params) => {
  return request(crmPath + '/platform/delete', {
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
  return request(crmPath + '/platform/getDetail', {
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
  return request(upmsPath + '/product/getList', {
    serviceName: 'ProductService',
    methodName: 'getList',
    bizParams: {
      ...params,
    },
  });
};

