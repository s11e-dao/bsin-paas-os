import { request } from '@umijs/max'
let crmPath = process.env.contextPath_crm;

// 分页查询
export const getApiFeeConfigPageList = (params) => {
  return request('/getPageList', {
    serviceName: 'MerchantProductApiFeeService',
    methodName: 'getPageList',
    version: '1.0',
    bizParams: {
      ...params,
    },
  });
};

// 创建
export const addApiFeeConfig = (params) => {
  return request('/add', {
    serviceName: 'MerchantProductApiFeeService',
    methodName: 'add',
    version: '1.0',
    bizParams: {
      ...params,
    },
  });
};

// 删除
export const deleteApiFeeConfig = (params) => {
  return request('/del', {
    serviceName: 'MerchantProductApiFeeService',
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
  return request('/view', {
    serviceName: 'MerchantProductApiFeeService',
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
  return request('/edit', {
    serviceName: 'MerchantProductApiFeeService',
    methodName: 'edit',
    version: '1.0',
    bizParams: {
      ...params,
    },
  });
};
