import { request } from '@umijs/max';
let aiPath = process.env.contextPath_ai;

// 列表数据请求
export const getRoleList = (params) => {
  return request(aiPath + '/getPageList', {
    serviceName: 'RoleService',
    methodName: 'getPageList',
    version: '1.0',
    bizParams: {
      ...params,
    },
  });
};

// 删除角色操作
export const delRoleInfo = (params) => {
  return request(aiPath + '/delete', {
    serviceName: 'RoleService',
    methodName: 'delete',
    version: '1.0',
    bizParams: {
      ...params,
    },
  });
};

//添加角色操作
export const addRoleInfo = (params) => {
  return request(aiPath + '/add', {
    serviceName: 'RoleService',
    methodName: 'add',
    version: '1.0',
    bizParams: {
      ...params,
    },
  });
};

//编辑角色操作
export const editRoleInfo = (params) => {
  return request(aiPath + '/edit', {
    serviceName: 'RoleService',
    methodName: 'edit',
    version: '1.0',
    bizParams: {
      ...params,
    },
  });
};

// 菜单数据请求
export const getMenuList = (params) => {
  return request(aiPath + '/findMenuTree', {
    serviceName: 'MenuService',
    methodName: 'findMenuTree',
    version: '1.0',
    bizParams: {
      ...params,
    },
  });
};

// 已有菜单数据请求
export const getMenusByAppIdAndRoleId = (params) => {
  return request(aiPath + '/getMenusByAppIdAndRoleId', {
    serviceName: 'MenuService',
    methodName: 'getMenusByAppIdAndRoleId',
    version: '1.0',
    bizParams: {
      ...params,
    },
  });
};

// 授权
export const empowerMenu = (params) => {
  return request(aiPath + '/authorizeMenus', {
    serviceName: 'RoleService',
    methodName: 'authorizeMenus',
    version: '1.0',
    bizParams: {
      ...params,
    },
  });
};
