import { request } from '@umijs/max'
let crmPath = process.env.contextPath_crm;

// 注册接口
export const userRegister = (params: any) => {
  return request(crmPath + '/merchant/register', {
    serviceName: 'MerchantService',
    methodName: 'register',
    version: '1.0',
    bizParams: {
      ...params,
    },
  });
};
