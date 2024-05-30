import { request } from '@umijs/max'

// 分页查询
export const getMerchantAppList = (params) => {
  return request('/list', {
    serviceName: 'OrgService',
    methodName: 'getAppListByOrgCode',
    bizParams: {
      ...params,
    },
  });
};

export const getMerchantAuthorizableAppList = (params) => {
  return request('/list', {
    serviceName: 'OrgService',
    methodName: 'getAuthorizableAppListByOrgCode',
    bizParams: {
      ...params,
    },
  });
};

// 商户订阅服务
export const subscribeApps = (params) => {
  return request('/list', {
    serviceName: 'OrgService',
    methodName: 'authorizeMercahntOrgApps',
    bizParams: {
      ...params,
    },
  });
};

