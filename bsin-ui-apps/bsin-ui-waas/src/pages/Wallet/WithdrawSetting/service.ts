import { request } from '@umijs/max';

const waasPath = process.env.contextPath_waas;
const crmPath = process.env.contextPath_crm;

// 设置提现账号
export const setUpWithdrawAccount = (params: any) => {
  return request(crmPath + '/settlementAccount/setUp', {
    serviceName: 'SettlementAccountService',
    methodName: 'setUp',
    version: '1.0',
    bizParams: {
      ...params,
    },
  });
};

// 查询提现账号
export const getWithdrawAccountList = (params: any) => {
  return request(crmPath + '/settlementAccount/getList', {
    serviceName: 'SettlementAccountService',
    methodName: 'getList',
    version: '1.0',
    bizParams: {
      ...params,
    },
  });
};

// 更新提现账号
export const updateWithdrawAccount = (params: any) => {
  return request(crmPath + '/settlementAccount/edit', {
    serviceName: 'SettlementAccountService',
    methodName: 'edit',
    version: '1.0',
    bizParams: {
      ...params,
    },
  });
};

// 删除提现账号
export const deleteWithdrawAccount = (params: any) => {
  return request(crmPath + '/settlementAccount/delete', {
    serviceName: 'SettlementAccountService',
    methodName: 'delete',
    version: '1.0',
    bizParams: {
      serialNo: params,
    },
  });
};

// 设置默认提现账号
export const setDefaultWithdrawAccount = (params: any) => {
  return request(crmPath + '/settlementAccount/setDefault', {
    serviceName: 'SettlementAccountService',
    methodName: 'setDefault',
    version: '1.0',
    bizParams: {
      serialNo: params,
    },
  });
};