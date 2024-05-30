import { request } from '@umijs/max'
let crmPath = process.env.contextPath_crm;

// 分页查询合约配置
export const getAdsPageList = (params) => {
  return request('/list', {
    serviceName: 'AdsService',
    methodName: 'getPageList',
    version: '1.0',
    bizParams: {
      ...params,
    },
  });
};

// 创建合约配置
export const addAds = (params) => {
  return request('/add', {
    serviceName: 'AdsService',
    methodName: 'add',
    version: '1.0',
    bizParams: {
      ...params,
    },
  });
};

// 删除合约配置
export const deleteAds = (params) => {
  return request('/del', {
    serviceName: 'AdsService',
    methodName: 'delete',
    version: '1.0',
    bizParams: {
      ...params,
    },
  });
};

// 查询合约配置详情
export const getAdsDetail = (params) => {
  console.log('params', params);
  return request('/view', {
    serviceName: 'AdsService',
    methodName: 'getDetail',
    version: '1.0',
    bizParams: {
      ...params,
    },
  });
};
