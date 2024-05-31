import { request } from '@umijs/max'
let crmPath = process.env.contextPath_crm;

// 分页查询客户账户
export const getCustomerAccountPageList = (params) => {
  return request('/list', {
    serviceName: 'CustomerAccountService',
    methodName: 'getPageList',
    version: '1.0',
    bizParams: {
      ...params,
    },
  });
};

// 创建客户账户
export const addCustomerAccount = (params) => {
  return request('/add', {
    serviceName: 'CustomerAccountService',
    methodName: 'add',
    version: '1.0',
    bizParams: {
      ...params,
    },
  });
};

// 删除客户账户
export const deleteCustomerAccount = (params) => {
  return request('/del', {
    serviceName: 'CustomerAccountService',
    methodName: 'delete',
    version: '1.0',
    bizParams: {
      ...params,
    },
  });
};

// 查询客户账户详情
export const getCustomerAccountDetail = (params) => {
  console.log('params', params);
  return request('/view', {
    serviceName: 'CustomerAccountService',
    methodName: 'getDetail',
    version: '1.0',
    bizParams: {
      ...params,
    },
  });
};
