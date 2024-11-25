import { request } from '@umijs/max'
let waasPath = process.env.contextPath_waas;

// 分页查询
export const getAdsPageList = (params) => {
  return request(waasPath + '/wallet/getPageList', {
    serviceName: 'WalletService',
    methodName: 'getPageList',
    version: '1.0',
    bizParams: {
      ...params,
    },
  });
};

// 创建
export const addAds = (params) => {
  return request(waasPath + '/wallet/createMPCWallet', {
    serviceName: 'WalletService',
    methodName: 'createMPCWallet',
    version: '1.0',
    bizParams: {
      ...params,
    },
  });
};

// 删除
export const deleteAds = (params) => {
  return request(waasPath + '/wallet/delete', {
    serviceName: 'WalletService',
    methodName: 'delete',
    version: '1.0',
    bizParams: {
      ...params,
    },
  });
};

// 查询详情
export const getAdsDetail = (params) => {
  console.log('params', params);
  return request(waasPath + '/wallet/getDetail', {
    serviceName: 'WalletService',
    methodName: 'getDetail',
    version: '1.0',
    bizParams: {
      ...params,
    },
  });
};
