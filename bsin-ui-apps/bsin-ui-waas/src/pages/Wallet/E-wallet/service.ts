import { request } from '@umijs/max'
let crmPath = process.env.contextPath_crm;

// 分页查询合约配置
export const getAccountJournalPageList = (params) => {
  return request(crmPath + '/account/getAccountJournalPageList', {
    serviceName: 'AccountService',
    methodName: 'getDetail',
    version: '1.0',
    bizParams: {
      ...params,
    },
  });
};


// 按 AccountCategory查询账户
export const getCategoryAccounts = (params) => {
  return request(crmPath + '/account/getCategoryAccounts', {
    serviceName: 'AccountService',
    methodName: 'getCategoryAccounts',
    version: '1.0',
    bizParams: {
      ...params,
    },
  });
};
