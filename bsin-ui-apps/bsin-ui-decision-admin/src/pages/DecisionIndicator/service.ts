import { request } from '@umijs/max'
let brmsAgent = process.env.contextPath_brms;

// 列表数据请求
export const getRoleList = (params) => {
  return request(brmsAgent + '/roleService/list', {
    serviceName: 'RoleService',
    methodName: 'getPageList',
    bizParams: {
      ...params,
    },
  });
};

// 删除角色操作
export const delRoleInfo = (params) => {
  return request(brmsAgent + '/roleService/delete', {
    serviceName: 'RoleService',
    methodName: 'delete',
    bizParams: {
      ...params,
    },
  });
};

//添加角色操作
export const addRoleInfo = (params) => {
  return request(brmsAgent + '/roleService/add', {
    serviceName: 'RoleService',
    methodName: 'add',
    bizParams: {
      ...params,
    },
  });
};

//编辑角色操作
export const editRoleInfo = (params) => {
  return request(brmsAgent + '/roleService/edit', {
    serviceName: 'RoleService',
    methodName: 'edit',
    bizParams: {
      ...params,
    },
  });
};

// 菜单数据请求
export const getMenuList = (params) => {
  return request(brmsAgent + '/roleService/list', {
    serviceName: 'MenuService',
    methodName: 'findMenuTree',
    bizParams: {
      ...params,
    },
  });
};

// 已有菜单数据请求
export const getMenusByAppIdAndRoleId = (params) => {
  return request(brmsAgent + '/roleService/byAppIdAndRoleId', {
    serviceName: 'MenuService',
    methodName: 'getMenusByAppIdAndRoleId',
    bizParams: {
      ...params,
    },
  });
};

// 授权
export const empowerMenu = (params) => {
  return request(brmsAgent + '/roleService/empower', {
    serviceName: 'RoleService',
    methodName: 'authorizeMenus',
    bizParams: {
      ...params,
    },
  });
};
