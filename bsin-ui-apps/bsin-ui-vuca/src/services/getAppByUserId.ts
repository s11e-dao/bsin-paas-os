import { request } from '@umijs/max'

// 获取用户的应用列表
export const getAppByUserId = (params: any) => {
  return request('/getAppByUserId', {
    serviceName: 'UserService',
    methodName: 'getAppByUserId',
    bizParams: {
      ...params,
    }
  });
};

export const getAppListByUserId = (params: any) => {
  return request('/getAppListByUserId', {
    serviceName: 'UserService',
    methodName: 'getAppListByUserId',
    bizParams: {
      ...params,
    }
  });
};

export const getTenantBaseApp = (params: any) => {
  return request('/getTenantBaseApp', {
    serviceName: 'TenantService',
    methodName: 'getTenantBaseApp',
    bizParams: {
      ...params,
    },
  });
};

