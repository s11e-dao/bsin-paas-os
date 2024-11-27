import { request } from '@umijs/max'
let crmPath = process.env.contextPath_crm;

// 分页查询合约配置
export const getCustomerAccountDetail = (params) => {
  return request(crmPath + '/account/getAccountJournalPageList', {
    serviceName: 'AccountService',
    methodName: 'getDetail',
    version: '1.0',
    bizParams: {
      ...params,
    },
  });
};
