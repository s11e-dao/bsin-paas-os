import { request } from '@umijs/max'
let upmsPath = process.env.contextPath_upms;

// 列表数据请求
export const getOrgTree = (params) => {
  return request(upmsPath + '/org/getOrgTree', {
    serviceName: 'OrgService',
    methodName: 'getOrgTree',
    bizParams: {
      ...params,
    },
  });
};

// add
export const addOrganizationInfo = (params) => {
  return request(upmsPath + '/org/add', {
    serviceName: 'OrgService',
    methodName: 'add',
    bizParams: {
      ...params,
    },
  });
};

// edit
export const editOrganizationInfo = (params) => {
  return request(upmsPath + '/org/edit', {
    serviceName: 'OrgService',
    methodName: 'edit',
    bizParams: {
      ...params,
    },
  });
};

// del
export const delOrganizationInfo = (params) => {
  return request(upmsPath + '/org/delete', {
    serviceName: 'OrgService',
    methodName: 'delete',
    bizParams: {
      ...params,
    },
  });
};

// 穿索框左侧
// 查询该租户下已授权app应用
export const getAppList = (params) => {
  return request(upmsPath + '/app/getAuthorizedList', {
    serviceName: 'AppService',
    methodName: 'getAuthorizedList',
    bizParams: {
      ...params,
    },
  });
};

// 穿索框右侧
// 根据机构ID已分配
export const getAppListByOrgId = (params) => {
  return request(upmsPath + '/org/getAppListByOrgId', {
    serviceName: 'OrgService',
    methodName: 'getAppListByOrgId',
    bizParams: {
      ...params,
    },
  });
};

// 授权
export const empowerAppList = (params) => {
  return request(upmsPath + '/org/authorizationApps', {
    serviceName: 'OrgService',
    methodName: 'authorizationApps',
    bizParams: {
      ...params,
    },
  });
};
