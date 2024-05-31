import { request } from '@umijs/max'
let crmPath = process.env.contextPath_crm;

// 分页查询合约配置
export const getPassCardPageList = (params) => {
  return request('/list', {
    serviceName: 'CustomerPassCardService',
    methodName: 'getPageList',
    version: '1.0',
    bizParams: {
      ...params,
    },
  });
};

// 创建合约配置
export const addPassCard = (params) => {
  return request('/add', {
    serviceName: 'CustomerPassCardService',
    methodName: 'add',
    version: '1.0',
    bizParams: {
      ...params,
    },
  });
};

// 删除合约配置
export const deletePassCard = (params) => {
  return request('/del', {
    serviceName: 'CustomerPassCardService',
    methodName: 'delete',
    version: '1.0',
    bizParams: {
      ...params,
    },
  });
};

// 查询合约配置详情
export const getPassCardDetail = (params) => {
  console.log('params', params);
  return request('/view', {
    serviceName: 'CustomerPassCardService',
    methodName: 'getDetail',
    version: '1.0',
    bizParams: {
      ...params,
    },
  });
};
