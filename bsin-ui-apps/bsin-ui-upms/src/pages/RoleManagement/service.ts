import { request } from '@umijs/max'
let upmsPath = process.env.contextPath_upms;

// 列表数据请求
export const getRoleList = (params) => {
  return request(upmsPath + '/role/getPageList', {
    serviceName: 'RoleService',
    methodName: 'getPageList',
    bizParams: {
      ...params,
    },
  });
};

// 删除角色操作
export const delRoleInfo = (params) => {
  return request(upmsPath + '/role/delete', {
    serviceName: 'RoleService',
    methodName: 'delete',
    bizParams: {
      ...params,
    },
  });
};

//添加角色操作
export const addRoleInfo = (params) => {
  return request(upmsPath + '/role/add', {
    serviceName: 'RoleService',
    methodName: 'add',
    bizParams: {
      ...params,
    },
  });
};

//编辑角色操作
export const editRoleInfo = (params) => {
  return request(upmsPath + '/role/edit', {
    serviceName: 'RoleService',
    methodName: 'edit',
    bizParams: {
      ...params,
    },
  });
};

// 菜单数据请求
export const getMenuList = (params) => {
  return request(upmsPath + '/menu/getMenuTree', {
    serviceName: 'MenuService',
    methodName: 'getMenuTree',
    bizParams: {
      ...params,
    },
  });
};

// 已有菜单数据请求
export const getMenusByAppIdAndRoleId = (params) => {
  return request(upmsPath + '/menu/getMenusByAppIdAndRoleId', {
    serviceName: 'MenuService',
    methodName: 'getMenusByAppIdAndRoleId',
    bizParams: {
      ...params,
    },
  });
};

// 授权
export const empowerMenu = (params) => {
  return request(upmsPath + '/role/authorizeMenus', {
    serviceName: 'RoleService',
    methodName: 'authorizeMenus',
    bizParams: {
      ...params,
    },
  });
};
