import { request } from '@umijs/max'
let upmsPath = process.env.contextPath_upms;

// 获取字典列表
export const getPageList = (params: any) => {
  return request(upmsPath + '/product/getPageList', {
    serviceName: 'ProductService',
    methodName: 'getPageList',
    bizParams: {
      ...params,
    },
  });
};

// 新增字典
export const addPageList = (params: any) => {
  return request(upmsPath + '/product/add', {
    serviceName: 'ProductService',
    methodName: 'add',
    bizParams: {
      ...params,
    },
  });
};

// 编辑字典
export const editPageList = (params: any) => {
  return request(upmsPath + '/product/edit', {
    serviceName: 'ProductService',
    methodName: 'edit',
    bizParams: {
      ...params,
    },
  });
};

// 删除字典
export const deletePageList = (params: any) => {
  return request(upmsPath + '/product/delete', {
    serviceName: 'ProductService',
    methodName: 'delete',
    bizParams: {
      ...params,
    },
  });
};

// 字典项查询
export const getProductAppPageList = (params: any) => {
  return request(upmsPath + '/product/getProductAppPageList', {
    serviceName: 'ProductService',
    methodName: 'getProductAppPageList',
    bizParams: {
      ...params,
    },
  });
};

// 字典项新增
export const addDictItemPageList = (params: any) => {
  return request(upmsPath + '/product/addProductApp', {
    serviceName: 'ProductService',
    methodName: 'addProductApp',
    bizParams: {
      ...params,
    },
  });
};

// 字典项编辑
export const editDictItemPageList = (params: any) => {
  return request(upmsPath + '/product/editProductApp', {
    serviceName: 'ProductService',
    methodName: 'editProductApp',
    bizParams: {
      ...params,
    },
  });
};

// 字典项删除
export const deleteProductApp = (params: any) => {
  return request(upmsPath + '/product/deleteProductApp', {
    serviceName: 'ProductService',
    methodName: 'deleteProductApp',
    bizParams: {
      ...params,
    },
  });
};


export const getAppList = (params) => {
  return request(upmsPath + '/app/getPublishedApps', {
    serviceName: 'AppService',
    methodName: 'getPublishedApps',
    bizParams: {
      ...params,
    },
  });
};