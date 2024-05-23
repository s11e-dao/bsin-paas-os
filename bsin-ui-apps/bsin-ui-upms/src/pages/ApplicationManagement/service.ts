import { request } from '@umijs/max'
let upmsPath = process.env.contextPath_upms;

// 列表数据请求
export const getAppList = (params) => {
  return request(upmsPath + '/app/getPageList', {
    serviceName: 'AppService',
    methodName: 'getPageList',
    bizParams: {
      ...params,
    },
  });
};

// add
export const addAppList = (params) => {
  return request(upmsPath + '/app/add', {
    serviceName: 'AppService',
    methodName: 'add',
    bizParams: {
      ...params,
    },
  });
};

// edit
export const editAppList = (params) => {
  return request(upmsPath + '/app/edit', {
    serviceName: 'AppService',
    methodName: 'edit',
    bizParams: {
      ...params,
    },
  });
};

// 删除列表数据
export const delAppInfo = (params) => {
  return request(upmsPath + '/app/delete', {
    serviceName: 'AppService',
    methodName: 'delete',
    bizParams: {
      ...params,
    },
  });
};

// 列表数据请求
export const getAppFunctionPageList = (params) => {
  return request(upmsPath + '/app/getAppFunctionPageList', {
    serviceName: 'AppService',
    methodName: 'getAppFunctionPageList',
    bizParams: {
      ...params,
    },
  });
};

export const addAppFunction = (params) => {
  return request(upmsPath + '/app/addAppFunction', {
    serviceName: 'AppService',
    methodName: 'addAppFunction',
    bizParams: {
      ...params,
    },
  });
};

export const editAppFunction = (params) => {
  return request(upmsPath + '/app/editAppFunction', {
    serviceName: 'AppService',
    methodName: 'editAppFunction',
    bizParams: {
      ...params,
    },
  });
};

export const delAppFunction = (params) => {
  return request(upmsPath + '/app/deleteAppFunction', {
    serviceName: 'AppService',
    methodName: 'deleteAppFunction',
    bizParams: {
      ...params,
    },
  });
};
