import { request } from '@umijs/max'

// 分页查询合约实例
export const getCustomerEnterprisePageList = (params) => {
  return request('/list', {
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
  return request('/getCustomerEnterpriseInfo', {
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
  return request('/audit', {
    serviceName: 'MerchantService',
    methodName: 'audit',
    version: '1.0',
    bizParams: {
      ...params,
    },
  });
};
