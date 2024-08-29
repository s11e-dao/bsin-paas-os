import { request } from '@umijs/max'
let omsPath = process.env.contextPath_oms;

// 分页查询
export const getStorePageList = (params) => {
  return request(omsPath + '/store/getPageList', {
    serviceName: 'StoreService',
    methodName: 'getPageList',
    version: '1.0',
    bizParams: {
      ...params,
    },
  });
};

// 创建
export const openStore = (params) => {
  return request(omsPath + '/store/openStore', {
    serviceName: 'StoreService',
    methodName: 'openStore',
    version: '1.0',
    bizParams: {
      ...params,
    },
  });
};

// 删除
export const deleteStore = (params) => {
  return request(omsPath + '/store/delete', {
    serviceName: 'StoreService',
    methodName: 'delete',
    version: '1.0',
    bizParams: {
      ...params,
    },
  });
};

// 修改
export const editStore = (params) => {
  return request(omsPath + '/store/edit', {
    serviceName: 'StoreService',
    methodName: 'edit',
    version: '1.0',
    bizParams: {
      ...params,
    },
  });
};

// 查询详情
export const getStoreDetail = (params) => {
  console.log('params', params);
  return request(omsPath + '/store/getDetail', {
    serviceName: 'StoreService',
    methodName: 'getDetail',
    version: '1.0',
    bizParams: {
      ...params,
    },
  });
};
