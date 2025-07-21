import { request } from '@umijs/max';
import type { 
  WithdrawAccountType, 
  WithdrawSettingType, 
  WithdrawRecordType,
  ApiResponse 
} from './types';

const waasPath = process.env.contextPath_waas;

// 提现账号相关 API
export const withdrawAccountService = {
  // 获取提现账号列表
  getWithdrawAccountList: (params: any): Promise<ApiResponse<WithdrawAccountType[]>> => {
    return request(waasPath + '/wallet/withdrawAccount/getPageList', {
      serviceName: 'WalletService',
      methodName: 'getWithdrawAccountPageList',
      version: '1.0',
      bizParams: {
        ...params,
      },
    });
  },

  // 创建提现账号
  createWithdrawAccount: (params: Partial<WithdrawAccountType>): Promise<ApiResponse<WithdrawAccountType>> => {
    return request(waasPath + '/wallet/withdrawAccount/create', {
      serviceName: 'WalletService',
      methodName: 'createWithdrawAccount',
      version: '1.0',
      bizParams: {
        ...params,
      },
    });
  },

  // 更新提现账号
  updateWithdrawAccount: (params: Partial<WithdrawAccountType>): Promise<ApiResponse<WithdrawAccountType>> => {
    return request(waasPath + '/wallet/withdrawAccount/update', {
      serviceName: 'WalletService',
      methodName: 'updateWithdrawAccount',
      version: '1.0',
      bizParams: {
        ...params,
      },
    });
  },

  // 删除提现账号
  deleteWithdrawAccount: (serialNo: string): Promise<ApiResponse<boolean>> => {
    return request(waasPath + '/wallet/withdrawAccount/delete', {
      serviceName: 'WalletService',
      methodName: 'deleteWithdrawAccount',
      version: '1.0',
      bizParams: {
        serialNo,
      },
    });
  },

  // 获取提现账号详情
  getWithdrawAccountDetail: (serialNo: string): Promise<ApiResponse<WithdrawAccountType>> => {
    return request(waasPath + '/wallet/withdrawAccount/getDetail', {
      serviceName: 'WalletService',
      methodName: 'getWithdrawAccountDetail',
      version: '1.0',
      bizParams: {
        serialNo,
      },
    });
  },

  // 设置默认提现账号
  setDefaultWithdrawAccount: (serialNo: string): Promise<ApiResponse<boolean>> => {
    return request(waasPath + '/wallet/withdrawAccount/setDefault', {
      serviceName: 'WalletService',
      methodName: 'setDefaultWithdrawAccount',
      version: '1.0',
      bizParams: {
        serialNo,
      },
    });
  },

  // 切换提现账号状态
  toggleWithdrawAccountStatus: (serialNo: string, status: 'active' | 'inactive'): Promise<ApiResponse<boolean>> => {
    return request(waasPath + '/wallet/withdrawAccount/toggleStatus', {
      serviceName: 'WalletService',
      methodName: 'toggleWithdrawAccountStatus',
      version: '1.0',
      bizParams: {
        serialNo,
        status,
      },
    });
  },
};

// 提现设置相关 API
export const withdrawSettingService = {
  // 获取提现设置
  getWithdrawSetting: (): Promise<ApiResponse<WithdrawSettingType>> => {
    return request(waasPath + '/wallet/withdrawSetting/get', {
      serviceName: 'WalletService',
      methodName: 'getWithdrawSetting',
      version: '1.0',
      bizParams: {},
    });
  },

  // 更新提现设置
  updateWithdrawSetting: (params: Partial<WithdrawSettingType>): Promise<ApiResponse<WithdrawSettingType>> => {
    return request(waasPath + '/wallet/withdrawSetting/update', {
      serviceName: 'WalletService',
      methodName: 'updateWithdrawSetting',
      version: '1.0',
      bizParams: {
        ...params,
      },
    });
  },
};

// 提现记录相关 API
export const withdrawRecordService = {
  // 获取提现记录列表
  getWithdrawRecordList: (params: any): Promise<ApiResponse<WithdrawRecordType[]>> => {
    return request(waasPath + '/wallet/withdrawRecord/getPageList', {
      serviceName: 'WalletService',
      methodName: 'getWithdrawRecordPageList',
      version: '1.0',
      bizParams: {
        ...params,
      },
    });
  },

  // 创建提现申请
  createWithdrawRecord: (params: Partial<WithdrawRecordType>): Promise<ApiResponse<WithdrawRecordType>> => {
    return request(waasPath + '/wallet/withdrawRecord/create', {
      serviceName: 'WalletService',
      methodName: 'createWithdrawRecord',
      version: '1.0',
      bizParams: {
        ...params,
      },
    });
  },

  // 获取提现记录详情
  getWithdrawRecordDetail: (serialNo: string): Promise<ApiResponse<WithdrawRecordType>> => {
    return request(waasPath + '/wallet/withdrawRecord/getDetail', {
      serviceName: 'WalletService',
      methodName: 'getWithdrawRecordDetail',
      version: '1.0',
      bizParams: {
        serialNo,
      },
    });
  },

  // 取消提现申请
  cancelWithdrawRecord: (serialNo: string): Promise<ApiResponse<boolean>> => {
    return request(waasPath + '/wallet/withdrawRecord/cancel', {
      serviceName: 'WalletService',
      methodName: 'cancelWithdrawRecord',
      version: '1.0',
      bizParams: {
        serialNo,
      },
    });
  },
}; 