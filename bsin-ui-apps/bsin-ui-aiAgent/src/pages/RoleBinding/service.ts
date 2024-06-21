import { request } from '@umijs/max';
let aiAgent = process.env.contextPath_aiAgent;

// 列表数据请求
export const getRoleList = (params) => {
  return request(aiAgent + '/role/getPageList', {
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
  return request(aiAgent + '/role/delete', {
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
  return request(aiAgent + '/role/add', {
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
  return request(aiAgent + '/role/edit', {
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
  return request(aiAgent + '/role/findMenuTree', {
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
  return request(aiAgent + '/role/getMenusByAppIdAndRoleId', {
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
  return request(aiAgent + '/role/authorizeMenus', {
    serviceName: 'RoleService',
    methodName: 'authorizeMenus',
    version: '1.0',
    bizParams: {
      ...params,
    },
  });
};
