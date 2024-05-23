import { request } from '@umijs/max';
let aiPath = process.env.contextPath_ai;

// 列表数据请求
export const getWxmpUserPageList = (params) => {
  return request(aiPath + '/getPageList', {
    serviceName: 'TenantWxmpUserService',
    methodName: 'getPageList',
    version: '1.0',
    bizParams: {
      ...params,
    },
  });
};

// 删除
export const delWxmpUserInfo = (params) => {
  return request(aiPath + '/delete', {
    serviceName: 'TenantWxmpUserService',
    methodName: 'delete',
    version: '1.0',
    bizParams: {
      ...params,
    },
  });
};

//添加
export const addWxmpUserInfo = (params) => {
  return request(aiPath + '/add', {
    serviceName: 'TenantWxmpUserService',
    methodName: 'add',
    version: '1.0',
    bizParams: {
      ...params,
    },
  });
};

//编辑
export const editWxmpUserInfo = (params) => {
  return request(aiPath + '/edit', {
    serviceName: 'TenantWxmpUserService',
    methodName: 'edit',
    version: '1.0',
    bizParams: {
      ...params,
    },
  });
};

// 用户标签详情
export const getWxmpUserTagDetail = (params) => {
  return request(aiPath + '/detail', {
    serviceName: 'TenantWxmpUserTagService',
    methodName: 'detail',
    version: '1.0',
    bizParams: {
      ...params,
    },
  });
};
