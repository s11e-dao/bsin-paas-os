import { request } from '@umijs/max'
let waasPath = process.env.contextPath_waas;

// 分页查询
export const getAdsPageList = (params) => {
  return request(waasPath + '/walletAccount/getPageList', {
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
  return request(waasPath + '/walletAccount/createMPCWallet', {
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
  return request(waasPath + '/walletAccount/delete', {
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
  return request(waasPath + '/walletAccount/getDetail', {
    serviceName: 'WalletService',
    methodName: 'getDetail',
    version: '1.0',
    bizParams: {
      ...params,
    },
  });
};
