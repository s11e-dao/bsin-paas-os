import { request } from '@umijs/max'
let waasPath = process.env.contextPath_waas;

// 分页查询
export const getRechargeRecordPageList = (params: any) => {
  return request(waasPath + '/wallet/recharge/getPageList', {
    serviceName: 'WalletService',
    methodName: 'getRechargePageList',
    version: '1.0',
    bizParams: {
      ...params,
    },
  });
};

// 提现
export const doRecharge = (params: any) => {
  return request(waasPath + '/wallet/recharge/createOrder', {
    serviceName: 'WalletService',
    methodName: 'createRechargeOrder',
    version: '1.0',
    bizParams: {
      ...params,
    },
  });
};

export const getRechargeRecordDetail = (params: any) => {
  return request(waasPath + '/wallet/recharge/getOrderDetail', {
    serviceName: 'WalletService',
    methodName: 'getRechargeOrderDetail',
    version: '1.0',
    bizParams: {
      ...params,
    },
  });
};


