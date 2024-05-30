import { request } from '@umijs/max'

// 分页查询
export const getMerchantProductPageList = (params) => {
  return request('/list', {
    serviceName: 'MerchantProductService',
    methodName: 'getPageList',
    version: '1.0',
    bizParams: {
      ...params,
    },
  });
};

// 创建
export const addMerchantProduct = (params) => {
  return request('/add', {
    serviceName: 'MerchantProductService',
    methodName: 'add',
    version: '1.0',
    bizParams: {
      ...params,
    },
  });
};

// 编辑
export const editMerchantProduct = (params) => {
  return request('/edit', {
    serviceName: 'MerchantProductService',
    methodName: 'edit',
    version: '1.0',
    bizParams: {
      ...params,
    },
  });
};

// 删除
export const deleteMerchantProduct = (params) => {
  return request('/del', {
    serviceName: 'MerchantProductService',
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
  return request('/view', {
    serviceName: 'MerchantProductService',
    methodName: 'getDetail',
    version: '1.0',
    bizParams: {
      ...params,
    },
  });
};

