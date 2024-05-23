import { request } from '@umijs/max'
let upmsPath = process.env.contextPath_upms;
let crmPath = process.env.contextPath_crm;

// 登录接口
export const userLogin = (params: any) => {
  return request(upmsPath + '/user/login', {
    bizParams: {
      ...params
    },
  });
};

// 节点租户登录
export const nodeUserLogin = (params: any) => {
  return request(crmPath + '/platform/login', {
    serviceName: 'PlatformService',
    methodName: 'login',
    version: '1.0',
    bizParams: {
      ...params,
    },
  });
};

// 商户登录
export const merchantLogin = (params: any) => {
  return request(crmPath + '/merchant/login', {
    serviceName: 'MerchantService',
    methodName: 'login',
    version: '1.0',
    bizParams: {
      ...params,
    },
  });
};

// 获取所有租户
export const getTenantList = (params: any) => {
  return request(upmsPath + '/tenant/getAllTenantList', {
    ...params,
  });
};

export const getTenantBaseApp = (params: any) => {
  return request(upmsPath + '/tenant/getTenantBaseApp', {
    serviceName: 'TenantService',
    methodName: 'getTenantBaseApp',
    bizParams: {
      ...params,
    },
  });
};