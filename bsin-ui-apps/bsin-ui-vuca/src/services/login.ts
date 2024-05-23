import { request } from '@umijs/max'

// 登录接口
export const userLogin = (params: any) => {
  return request('/login', {
    serviceName: 'CustomerService',
    methodName: 'registerOrLogin',
    bizParams: {
      ...params,
    },
  });
};
