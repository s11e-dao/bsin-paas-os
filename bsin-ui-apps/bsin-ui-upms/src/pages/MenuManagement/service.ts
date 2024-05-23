import { request } from '@umijs/max'
let upmsPath = process.env.contextPath_upms;

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

// add increment
export const addMenu = (params) => {
  return request(upmsPath + '/menu/add', {
    serviceName: 'MenuService',
    methodName: 'add',
    bizParams: {
      ...params,
    },
  });
};

// del
export const delMenu = (params) => {
  return request(upmsPath + '/menu/delete', {
    serviceName: 'MenuService',
    methodName: 'delete',
    bizParams: {
      ...params,
    },
  });
};

// edit
export const editMenu = (params) => {
  return request(upmsPath + '/menu/edit', {
    serviceName: 'MenuService',
    methodName: 'edit',
    bizParams: {
      ...params,
    },
  });
};

// 根据应用ID查询功能列表
export const getAppFunctionList = (params) => {
  return request(upmsPath + '/app/getAppFunctionList', {
    serviceName: 'AppService',
    methodName: 'getAppFunctionList',
    bizParams: {
      ...params,
    },
  });
};

