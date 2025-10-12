import { request } from '@umijs/max'
let waasPath = process.env.contextPath_waas;

// 分页查询
export const getWalletPageList = (params: any) => {
  return request(waasPath + '/wallet/getPageList', {
    serviceName: 'WalletService',
    methodName: 'getPageList',
    version: '1.0',
    bizParams: {
      ...params,
    },
  });
};

// 创建MPC钱包
export const addWallet = (params: any) => {
  return request(waasPath + '/wallet/createMPCWallet', {
    serviceName: 'WalletService',
    methodName: 'createMPCWallet',
    version: '1.0',
    bizParams: {
      ...params,
    },
  });
};

// 编辑钱包
export const editWallet = (params: any) => {
  return request(waasPath + '/wallet/edit', {
    serviceName: 'WalletService',
    methodName: 'edit',
    version: '1.0',
    bizParams: {
      ...params,
    },
  });
};

// 删除钱包
export const deleteWallet = (params: any) => {
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
export const getWalletDetail = (params: any) => {
  return request(waasPath + '/wallet/getDetail', {
    serviceName: 'WalletService',
    methodName: 'getDetail',
    version: '1.0',
    bizParams: {
      ...params,
    },
  });
};
