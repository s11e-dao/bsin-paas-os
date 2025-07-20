import { request } from '@umijs/max'
let waasPath = process.env.contextPath_waas;

// 分页查询分账明细
export const getRevenueSharePageList = (params: any) => {
  return request(waasPath + '/transaction/getPageList', {
    serviceName: 'TransactionService',
    methodName: 'getPageList',
    version: '1.0',
    bizParams: {
      ...params,
    },
  });
};

// 查询分账明细详情
export const getRevenueShareDetail = (params: any) => {
  return request(waasPath + '/transaction/getDetail', {
    serviceName: 'TransactionService',
    methodName: 'getDetail',
    version: '1.0',
    bizParams: {
      ...params,
    },
  });
};
