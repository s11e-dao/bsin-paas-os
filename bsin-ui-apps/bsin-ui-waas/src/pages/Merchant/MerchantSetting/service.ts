import { request } from '@umijs/max'
let crmPath = process.env.contextPath_crm;

export const merchantAuth = (params) => {
  return request('/list', {
    serviceName: 'MerchantService',
    methodName: 'authentication',
    version: '1.0',
    bizParams: {
      ...params,
    },
  });
};

