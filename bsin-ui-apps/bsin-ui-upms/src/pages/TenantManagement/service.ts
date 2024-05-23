import { request } from '@umijs/max'
let upmsPath = process.env.contextPath_upms;

// 列表数据请求
export const getTenantList = (params) => {
  return request(upmsPath + '/tenant/getPageList', {
    serviceName: 'TenantService',
    methodName: 'getPageList',
    bizParams: {
      ...params,
    },
  });
};

// add
export const addTenantInfo = (params) => {
  return request(upmsPath + '/tenant/add', {
    serviceName: 'TenantService',
    methodName: 'add',
    bizParams: {
      ...params,
    },
  });
};

// del
export const delTenantInfo = (params) => {
  return request(upmsPath + '/tenant/del', {
    serviceName: 'TenantService',
    methodName: 'delete',
    bizParams: {
      ...params,
    },
  });
};

// edit
export const editTenantInfo = (params) => {
  return request(upmsPath + '/tenant/edit', {
    serviceName: 'TenantService',
    methodName: 'edit',
    bizParams: {
      ...params,
    },
  });
};

/**
 * 穿梭框
 * 左侧查询全部应用
 */
export const getAppList = (params) => {
  return request(upmsPath + '/app/getAuthorizableList', {
    serviceName: 'AppService',
    methodName: 'getAuthorizableList',
    bizParams: {
      ...params,
    },
  });
};

// 右侧根据租户ID查询应用
export const getAppListByTenantId = (params) => {
  return request(upmsPath + '/app/getAuthorizedList', {
    serviceName: 'AppService',
    methodName: 'getAuthorizedList',
    bizParams: {
      ...params,
    },
  });
};

// 授权
export const empowerApp = (params) => {
  return request(upmsPath + '/tenant/authorizeApps', {
    serviceName: 'TenantService',
    methodName: 'authorizeApps',
    bizParams: {
      ...params,
    },
  });
};
