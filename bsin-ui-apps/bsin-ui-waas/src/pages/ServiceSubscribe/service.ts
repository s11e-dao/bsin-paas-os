import { request } from '@umijs/max'
let upmsPath = process.env.contextPath_upms;
let crmPath = process.env.contextPath_crm;

// 分页查询
export const getMerchantAppList = (params) => {
  return request(upmsPath + '/org/getAppListByOrgId', {
    serviceName: 'OrgService',
    methodName: 'getAppListByOrgId',
    bizParams: {
      ...params,
    },
  });
};

export const getMerchantAuthorizableAppList = (params) => {
  return request(upmsPath + '/org/getAuthorizableAppListByOrgCode', {
    serviceName: 'OrgService',
    methodName: 'getAuthorizableAppListByOrgCode',
    bizParams: {
      ...params,
    },
  });
};

// 商户订阅服务
export const subscribeApps = (params) => {
  return request(upmsPath + '/org/authorizeMercahntOrgApps', {
    serviceName: 'OrgService',
    methodName: 'authorizeMercahntOrgApps',
    bizParams: {
      ...params,
    },
  });
};

