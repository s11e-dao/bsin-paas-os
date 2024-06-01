import { request } from '@umijs/max'
let crmPath = process.env.contextPath_crm;

// 分页查询
export const getApiFeeConfigPageList = (params) => {
  return request(crmPath + '/merchantAppApiFee/getPageList', {
    serviceName: 'MerchantAppApiFeeService',
    methodName: 'getPageList',
    version: '1.0',
    bizParams: {
      ...params,
    },
  });
};

// 创建
export const addApiFeeConfig = (params) => {
  return request(crmPath + '/merchantAppApiFee/add', {
    serviceName: 'MerchantAppApiFeeService',
    methodName: 'add',
    version: '1.0',
    bizParams: {
      ...params,
    },
  });
};

// 删除
export const deleteApiFeeConfig = (params) => {
  return request(crmPath + '/merchantAppApiFee/delete', {
    serviceName: 'MerchantAppApiFeeService',
    methodName: 'delete',
    version: '1.0',
    bizParams: {
      ...params,
    },
  });
};

// 详情
export const getApiFeeConfigDetails = (params) => {
  console.log('params', params);
  return request(crmPath + '/merchantAppApiFee/getApiFeeConfigInfo', {
    serviceName: 'MerchantAppApiFeeService',
    methodName: 'getApiFeeConfigInfo',
    version: '1.0',
    bizParams: {
      ...params,
    },
  });
};

// 详情
export const editApiFeeConfig = (params) => {
  console.log('params', params);
  return request(crmPath + '/merchantAppApiFee/edit', {
    serviceName: 'MerchantAppApiFeeService',
    methodName: 'edit',
    version: '1.0',
    bizParams: {
      ...params,
    },
  });
};
