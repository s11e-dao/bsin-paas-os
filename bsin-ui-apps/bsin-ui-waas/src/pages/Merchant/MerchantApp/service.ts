import { request } from '@umijs/max'
let crmPath = process.env.contextPath_crm;

// 分页查询
export const getMerchantProductPageList = (params) => {
  return request(crmPath + '/merchantApp/getPageList', {
    serviceName: 'MerchantAppService',
    methodName: 'getPageList',
    version: '1.0',
    bizParams: {
      ...params,
    },
  });
};

// 创建
export const addMerchantProduct = (params) => {
  return request(crmPath + '/merchantApp/add', {
    serviceName: 'MerchantAppService',
    methodName: 'add',
    version: '1.0',
    bizParams: {
      ...params,
    },
  });
};

// 编辑
export const editMerchantProduct = (params) => {
  return request(crmPath + '/merchantApp/edit', {
    serviceName: 'MerchantAppService',
    methodName: 'edit',
    version: '1.0',
    bizParams: {
      ...params,
    },
  });
};

// 删除
export const deleteMerchantProduct = (params) => {
  return request(crmPath + '/merchantApp/delete', {
    serviceName: 'MerchantAppService',
    methodName: 'delete',
    version: '1.0',
    bizParams: {
      ...params,
    },
  });
};

// 查询详情
export const getMerchantProductDetail = (params) => {
  console.log('params', params);
  return request(crmPath + '/merchantApp/getDetail', {
    serviceName: 'MerchantAppService',
    methodName: 'getDetail',
    version: '1.0',
    bizParams: {
      ...params,
    },
  });
};

