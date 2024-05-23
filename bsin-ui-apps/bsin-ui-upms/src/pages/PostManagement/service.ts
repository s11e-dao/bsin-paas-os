import { request } from '@umijs/max'
let upmsPath = process.env.contextPath_upms;

// 列表数据请求
export const getPostList = (params) => {
  return request(upmsPath + '/post/getPostListByTenantId', {
    serviceName: 'PostService',
    methodName: 'getPostListByTenantId',
    bizParams: {
      ...params,
    },
  });
};

// add
export const addPostInfo = (params) => {
  return request(upmsPath + '/post/add', {
    serviceName: 'PostService',
    methodName: 'add',
    bizParams: {
      ...params,
    },
  });
};

// edit
export const editPostInfo = (params) => {
  return request(upmsPath + '/post/edit', {
    serviceName: 'PostService',
    methodName: 'edit',
    bizParams: {
      ...params,
    },
  });
};

// del
export const delPostInfo = (params) => {
  return request(upmsPath + '/post/delete', {
    serviceName: 'PostService',
    methodName: 'delete',
    bizParams: {
      ...params,
    },
  });
};

// 获取所有当前应用下的角色列表
export const getRoleList = (params) => {
  return request(upmsPath + '/role/getListByAppId', {
    serviceName: 'RoleService',
    methodName: 'getListByAppId',
    bizParams: {
      ...params,
    },
  });
};

// 获取当前岗位已分配的角色列表
export const getAssignedRoleList = (params) => {
  return request(upmsPath + '/post/getRolesByPostId', {
    serviceName: 'PostService',
    methodName: 'getRolesByPostId',
    bizParams: {
      ...params,
    },
  });
};

// assignRole
export const assignRole = (params) => {
  return request(upmsPath + '/post/assignRoles', {
    serviceName: 'PostService',
    methodName: 'assignRoles',
    bizParams: {
      ...params,
    },
  });
};

// 搜索查看应用
export const getAppList = (params) => {
  return request(upmsPath + '/app/getAuthorizedList', {
    serviceName: 'AppService',
    methodName: 'getAuthorizedList',
    bizParams: {
      ...params,
    },
  });
};
