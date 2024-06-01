import { request } from '@umijs/max'
let crmPath = process.env.contextPath_crm;

// 分页查询客户账户
export const getCustomerAccountPageList = (params) => {
  return request(crmPath + 'account/getPageList', {
    serviceName: 'AccountService',
    methodName: 'getPageList',
    version: '1.0',
    bizParams: {
      ...params,
    },
  });
};

// 创建客户账户
export const addCustomerAccount = (params) => {
  return request(crmPath + 'account/add', {
    serviceName: 'AccountService',
    methodName: 'add',
    version: '1.0',
    bizParams: {
      ...params,
    },
  });
};

// 删除客户账户
export const deleteCustomerAccount = (params) => {
  return request(crmPath + 'account/delete', {
    serviceName: 'AccountService',
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
  return request(crmPath + 'account/getDetail', {
    serviceName: 'AccountService',
    methodName: 'getDetail',
    version: '1.0',
    bizParams: {
      ...params,
    },
  });
};
