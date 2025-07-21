import { request } from '@umijs/max';

const waasPath = process.env.contextPath_waas;
const crmPath = process.env.contextPath_crm;

// 电子钱包相关 API
export const eWalletService = {
  // 获取账户余额信息
  getAccountBalance: (): Promise<any> => {
    return request(waasPath + '/wallet/account/getBalance', {
      serviceName: 'WalletService',
      methodName: 'getAccountBalance',
      version: '1.0',
      bizParams: {},
    });
  },

  // 获取账户流水记录
  getAccountJournalPageList: (params: any): Promise<any> => {
    return request(waasPath + '/wallet/account/getJournalPageList', {
      serviceName: 'WalletService',
      methodName: 'getAccountJournalPageList',
      version: '1.0',
      bizParams: {
        ...params,
      },
    });
  },

  // 获取分类账户信息
  getCategoryAccounts: (params: any): Promise<any> => {
    return request(waasPath + '/wallet/account/getCategoryAccounts', {
      serviceName: 'WalletService',
      methodName: 'getCategoryAccounts',
      version: '1.0',
      bizParams: {
        ...params,
      },
    });
  },
};

// 充值相关 API
export const rechargeService = {
  // 创建充值订单
  createRechargeOrder: (params: any): Promise<any> => {
    return request(waasPath + '/wallet/recharge/createOrder', {
      serviceName: 'WalletService',
      methodName: 'createRechargeOrder',
      version: '1.0',
      bizParams: {
        ...params,
      },
    });
  },

  // 获取充值订单详情
  getRechargeOrderDetail: (orderId: string): Promise<any> => {
    return request(waasPath + '/wallet/recharge/getOrderDetail', {
      serviceName: 'WalletService',
      methodName: 'getRechargeOrderDetail',
      version: '1.0',
      bizParams: {
        orderId,
      },
    });
  },

  // 查询充值订单状态
  queryRechargeOrderStatus: (orderId: string): Promise<any> => {
    return request(waasPath + '/wallet/recharge/queryOrderStatus', {
      serviceName: 'WalletService',
      methodName: 'queryRechargeOrderStatus',
      version: '1.0',
      bizParams: {
        orderId,
      },
    });
  },

  // 获取充值记录列表
  getRechargeRecordList: (params: any): Promise<any> => {
    return request(waasPath + '/wallet/recharge/getRecordList', {
      serviceName: 'WalletService',
      methodName: 'getRechargeRecordList',
      version: '1.0',
      bizParams: {
        ...params,
      },
    });
  },

  // 取消充值订单
  cancelRechargeOrder: (orderId: string): Promise<any> => {
    return request(waasPath + '/wallet/recharge/cancelOrder', {
      serviceName: 'WalletService',
      methodName: 'cancelRechargeOrder',
      version: '1.0',
      bizParams: {
        orderId,
      },
    });
  },
};

// 提现相关 API
export const withdrawService = {
  // 创建提现申请
  createWithdrawApplication: (params: any): Promise<any> => {
    return request(crmPath + '/withdrawal/apply', {
      serviceName: 'WithdrawalRecordService',
      methodName: 'apply',
      version: '1.0',
      bizParams: {
        ...params,
      },
    });
  },

  // 获取提现申请详情
  getWithdrawApplicationDetail: (applicationId: string): Promise<any> => {
    return request(crmPath + '/withdrawal/getDetail', {
      serviceName: 'WithdrawalRecordService',
      methodName: 'getDetail',
      version: '1.0',
      bizParams: {
        applicationId,
      },
    });
  },

  // 取消提现申请
  cancelWithdrawApplication: (applicationId: string): Promise<any> => {
    return request(waasPath + '/wallet/withdraw/cancelApplication', {
      serviceName: 'WalletService',
      methodName: 'cancelWithdrawApplication',
      version: '1.0',
      bizParams: {
        applicationId,
      },
    });
  },

  // 获取提现记录列表
  getWithdrawRecordList: (params: any): Promise<any> => {
    return request(waasPath + '/wallet/withdraw/getRecordList', {
      serviceName: 'WalletService',
      methodName: 'getWithdrawRecordList',
      version: '1.0',
      bizParams: {
        ...params,
      },
    });
  },

  // 获取提现手续费计算
  calculateWithdrawFee: (params: any): Promise<any> => {
    return request(waasPath + '/wallet/withdraw/calculateFee', {
      serviceName: 'WalletService',
      methodName: 'calculateWithdrawFee',
      version: '1.0',
      bizParams: {
        ...params,
      },
    });
  },

  // 获取可提现金额
  getAvailableWithdrawAmount: (): Promise<any> => {
    return request(waasPath + '/wallet/withdraw/getAvailableAmount', {
      serviceName: 'WalletService',
      methodName: 'getAvailableWithdrawAmount',
      version: '1.0',
      bizParams: {},
    });
  },
};

