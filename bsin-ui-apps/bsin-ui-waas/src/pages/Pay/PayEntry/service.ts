import { request } from '@umijs/max'
let crmPath = process.env.contextPath_crm;

// 分页查询合约实例
export const getCustomerEnterprisePageList = (params) => {
  return request(crmPath + '/merchantPayEntry/getPageList', {
    serviceName: 'MerchantPayEntryService',
    methodName: 'getPageList',
    version: '1.0',
    bizParams: {
      ...params,
    },
  });
};

// 点击查询企业认证详情
export const getCustomerEnterpriseInfo = (params) => {
  return request(crmPath + '/merchantPayEntry/getDetail', {
    serviceName: 'MerchantPayEntryService',
    methodName: 'getDetail',
    version: '1.0',
    bizParams: {
      ...params,
    },
  });
};

// 审核
export const auditCustomerEnterprise = (params) => {
  return request(crmPath + '/merchant/audit', {
    serviceName: 'MerchantPayEntryService',
    methodName: 'audit',
    version: '1.0',
    bizParams: {
      ...params,
    },
  });
};
