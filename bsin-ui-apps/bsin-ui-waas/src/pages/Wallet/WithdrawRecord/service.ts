import { request } from '@umijs/max'
let waasPath = process.env.contextPath_waas;

// 分页查询
export const getWithdrawJournalPageList = (params) => {
  return request(waasPath + '/transaction/getPageList', {
    serviceName: 'TransactionService',
    methodName: 'getPageList',
    version: '1.0',
    bizParams: {
      ...params,
    },
  });
};

// 提现
export const doWithdraw = (params) => {
  return request(waasPath + '/transaction/withdrawApply', {
    serviceName: 'TransactionService',
    methodName: 'withdraw',
    version: '1.0',
    bizParams: {
      ...params,
    },
  });
};

export const getWithdrawJournalDetail = (params) => {
  return request(waasPath + '/transaction/getDetail', {
    serviceName: 'TransactionService',
    methodName: 'getDetail',
    version: '1.0',
    bizParams: {
      ...params,
    },
  });
};

export const withdrawAudit = (params) => {
  return request(waasPath + '/transaction/withdrawAudit', {
    serviceName: 'TransactionService',
    methodName: 'withdrawAudit',
    version: '1.0',
    bizParams: {
      ...params,
    },
  });
};


