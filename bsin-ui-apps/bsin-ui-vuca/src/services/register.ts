import { request } from '@umijs/max'

// 注册接口
export const userRegister = (params: any) => {
  return request('/register', {
    serviceName: 'MerchantService',
    methodName: 'register',
    version: '1.0',
    bizParams: {
      ...params,
    },
  });
};
