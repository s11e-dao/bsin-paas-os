import { request } from '@umijs/max'
let crmPath = process.env.contextPath_crm;

// 分页查询账号流水配置
export const getAccountJournalPageList = (params) => {
  return request('/list', {
    serviceName: 'CustomerAccountService',
    methodName: 'getAccountJournalPageList',
    version: '1.0',
    bizParams: {
      ...params,
    },
  });
};

export const getAccountJournalDetail = (params) => {
  return request('/list', {
    serviceName: 'CustomerAccountService',
    methodName: 'getAccountJournalDetail',
    version: '1.0',
    bizParams: {
      ...params,
    },
  });
};

// 分页查询账号冻结流水配置
export const getAccountFreezeJournalPageList = (params) => {
  return request('/list', {
    serviceName: 'CustomerAccountService',
    methodName: 'getAccountFreezeJournalPageList',
    version: '1.0',
    bizParams: {
      ...params,
    },
  });
};

export const getAccountFreezeJournalDetail = (params) => {
  return request('/list', {
    serviceName: 'CustomerAccountService',
    methodName: 'getAccountFreezeJournalDetail',
    version: '1.0',
    bizParams: {
      ...params,
    },
  });
};
