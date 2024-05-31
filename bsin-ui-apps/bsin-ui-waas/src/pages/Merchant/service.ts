import { request } from '@umijs/max'
let crmPath = process.env.contextPath_crm;
let upmsPath = process.env.contextPath_upms;

// 分页查询
export const getMerchantPageList = (params) => {
  return request(crmPath + '/merchant/getPageList', {
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
  return request(crmPath + '/merchant/register', {
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
  return request(crmPath + '/merchant/delete', {
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
  return request(crmPath + '/merchant/getDetail', {
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
  return request(upmsPath + '/post/getPostListByTenantId', {
    serviceName: 'PostService',
    methodName: 'getPostListByTenantId',
    bizParams: {
      ...params,
    },
  });
};

// 字典项查询
export const getDictItemPageList = (params: any) => {
  return request(upmsPath + '/dict/getDictItemPageList', {
    serviceName: 'DictService',
    methodName: 'getDictItemPageList',
    bizParams: {
      ...params,
    },
  });
};

