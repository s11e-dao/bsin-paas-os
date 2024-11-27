import { request } from '@umijs/max'
let crmPath = process.env.contextPath_crm;

// 分页查询账号流水配置
export const getAccountJournalPageList = (params) => {
  return request(crmPath + '/account/getAccountJournalPageList', {
    serviceName: 'AccountService',
    methodName: 'getAccountJournalPageList',
    version: '1.0',
    bizParams: {
      ...params,
    },
  });
};

export const getAccountJournalDetail = (params) => {
  return request(crmPath + '/account/getAccountJournalDetail', {
    serviceName: 'AccountService',
    methodName: 'getAccountJournalDetail',
    version: '1.0',
    bizParams: {
      ...params,
    },
  });
};

// 分页查询账号冻结流水配置
export const getAccountFreezeJournalPageList = (params) => {
  return request(crmPath + '/account/getAccountFreezeJournalPageList', {
    serviceName: 'AccountService',
    methodName: 'getAccountFreezeJournalPageList',
    version: '1.0',
    bizParams: {
      ...params,
    },
  });
};

export const getAccountFreezeJournalDetail = (params) => {
  return request(crmPath + '/account/getAccountFreezeJournalDetail', {
    serviceName: 'AccountService',
    methodName: 'getAccountFreezeJournalDetail',
    version: '1.0',
    bizParams: {
      ...params,
    },
  });
};
