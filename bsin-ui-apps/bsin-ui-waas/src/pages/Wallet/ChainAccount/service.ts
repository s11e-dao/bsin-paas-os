import { request } from '@umijs/max'
let waasPath = process.env.contextPath_waas;

// 分页查询
export const getWalletAccountPageList = (params: any) => {
  return request(waasPath + '/walletAccount/getPageList', {
    serviceName: 'WalletAccountService',
    methodName: 'getPageList',
    version: '1.0',
    bizParams: {
      ...params,
    },
  });
};

// 添加钱包账户
export const addWalletAccount = (params: any) => {
  return request(waasPath + '/walletAccount/add', {
    serviceName: 'WalletAccountService',
    methodName: 'add',
    version: '1.0',
    bizParams: {
      ...params,
    },
  });
};

// 编辑钱包账户
export const editWalletAccount = (params: any) => {
  return request(waasPath + '/walletAccount/edit', {
    serviceName: 'WalletAccountService',
    methodName: 'edit',
    version: '1.0',
    bizParams: {
      ...params,
    },
  });
};

// 删除钱包账户
export const deleteWalletAccount = (params: any) => {
  return request(waasPath + '/walletAccount/delete', {
    serviceName: 'WalletAccountService',
    methodName: 'delete',
    version: '1.0',
    bizParams: {
      ...params,
    },
  });
};

// 查询详情
export const getWalletAccountDetail = (params: any) => {
  return request(waasPath + '/walletAccount/getDetail', {
    serviceName: 'WalletAccountService',
    methodName: 'getDetail',
    version: '1.0',
    bizParams: {
      ...params,
    },
  });
};

// 获取地址二维码
export const getAddressQrCode = (serialNo: string) => {
  return request(waasPath + '/walletAccount/getAddressQrCode', {
    serviceName: 'WalletAccountService',
    methodName: 'getAddressQrCode',
    version: '1.0',
    bizParams: serialNo,
  });
};

// 获取币种列表
export const getChainCoinList = () => {
  return request(waasPath + '/chainCoin/getList', {
    serviceName: 'ChainCoinService',
    methodName: 'getList',
    version: '1.0',
    bizParams: {},
  });
};
