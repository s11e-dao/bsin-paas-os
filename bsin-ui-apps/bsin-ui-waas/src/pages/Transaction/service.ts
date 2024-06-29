import { request } from '@umijs/max'
let waasPath = process.env.contextPath_waas;

// 分页查询
export const getTransactionPageList = (params) => {
  return request(waasPath + '/transaction/getPageList', {
    serviceName: 'TransactionService',
    methodName: 'getPageList',
    version: '1.0',
    bizParams: {
      ...params,
    },
  });
};

// 添加平台
export const addTransaction = (params) => {
  return request(waasPath + '/transaction/openTenant', {
    serviceName: 'TransactionService',
    methodName: 'openTenant',
    version: '1.0',
    bizParams: {
      ...params,
    },
  });
};

// 删除
export const deleteTransaction = (params) => {
  return request(waasPath + '/transaction/delete', {
    serviceName: 'TransactionService',
    methodName: 'delete',
    version: '1.0',
    bizParams: {
      ...params,
    },
  });
};

// 查询详情
export const getTransactionDetail = (params) => {
  console.log('params', params);
  return request(waasPath + '/transaction/getDetail', {
    serviceName: 'TransactionService',
    methodName: 'getDetail',
    version: '1.0',
    bizParams: {
      ...params,
    },
  });
};

// 查询产品列表
export const getProductList = (params) => {
  return request(waasPath + '/transaction/getList', {
    serviceName: 'TransactionService',
    methodName: 'getList',
    bizParams: {
      ...params,
    },
  });
};

