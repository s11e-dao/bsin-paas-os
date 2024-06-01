import { request } from '@umijs/max'
let crmPath = process.env.contextPath_crm;

// 分页查询合约实例
export const getCustomerEnterprisePageList = (params) => {
  return request(crmPath + '/merchant/getPageList', {
    serviceName: 'MerchantService',
    methodName: 'getPageList',
    version: '1.0',
    bizParams: {
      ...params,
    },
  });
};

// 点击查询企业认证详情
export const getCustomerEnterpriseInfo = (params) => {
  return request(crmPath + '/merchant/getDetail', {
    serviceName: 'MerchantService',
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
    serviceName: 'MerchantService',
    methodName: 'audit',
    version: '1.0',
    bizParams: {
      ...params,
    },
  });
};
