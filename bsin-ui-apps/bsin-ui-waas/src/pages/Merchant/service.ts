import { request } from '@umijs/max'
let crmPath = process.env.contextPath_crm;

// 分页查询
export const getMerchantPageList = (params) => {
  return request('/list', {
    serviceName: 'MerchantService',
    methodName: 'getPageList',
    version: '1.0',
    bizParams: {
      ...params,
    },
  });
};

// 创建
export const registerMerchant = (params) => {
  return request('/add', {
    serviceName: 'MerchantService',
    methodName: 'register',
    version: '1.0',
    bizParams: {
      ...params,
    },
  });
};

// 删除
export const deleteMerchant = (params) => {
  return request('/del', {
    serviceName: 'MerchantService',
    methodName: 'delete',
    version: '1.0',
    bizParams: {
      ...params,
    },
  });
};

// 查询详情
export const getMerchantDetail = (params) => {
  console.log('params', params);
  return request('/view', {
    serviceName: 'MerchantService',
    methodName: 'getDetail',
    version: '1.0',
    bizParams: {
      ...params,
    },
  });
};


export const getPostListByTenantId = (params) => {
  console.log('params', params);
  return request('/getPostListByTenantId', {
    serviceName: 'PostService',
    methodName: 'getPostListByTenantId',
    bizParams: {
      ...params,
    },
  });
};

// 字典项查询
export const getDictItemPageList = (params: any) => {
  return request('/getDictItemPageList', {
    serviceName: 'DictService',
    methodName: 'getDictItemPageList',
    bizParams: {
      ...params,
    },
  });
};

