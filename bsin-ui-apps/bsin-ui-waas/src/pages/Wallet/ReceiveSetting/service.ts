import { request } from '@umijs/max'
let waasPath = process.env.contextPath_waas;

// 获取链结算账户列表 - 基于 waas_chain_settlement_account 表
export const getSettlementAccountList = (params: any) => {
  return request(waasPath + '/chainSettlementAccount/getPageList', {
    serviceName: 'ChainSettlementAccountService',
    methodName: 'getPageList',
    version: '1.0',
    bizParams: {
      ...params,
    },
  });
};

// 添加链结算账户
export const addSettlementAccount = (params: any) => {
  return request(waasPath + '/chainSettlementAccount/setUp', {
    serviceName: 'ChainSettlementAccountService',
    methodName: 'setUp',
    version: '1.0',
    bizParams: {
      ...params,
    },
  });
};

// 更新链结算账户
export const updateSettlementAccount = (params: any) => {
  return request(waasPath + '/chainSettlementAccount/edit', {
    serviceName: 'ChainSettlementAccountService',
    methodName: 'edit',
    version: '1.0',
    bizParams: {
      ...params,
    },
  });
};

// 删除链结算账户（逻辑删除）
export const deleteSettlementAccount = (params: any) => {
  return request(waasPath + '/chainSettlementAccount/delete', {
    serviceName: 'ChainSettlementAccountService',
    methodName: 'delete',
    version: '1.0',
    bizParams: {
      ...params,
    },
  });
};

// 设置默认结算账户
export const setDefaultSettlementAccount = (params: any) => {
  return request(waasPath + '/chainSettlementAccount/setDefault', {
    serviceName: 'ChainSettlementAccountService',
    methodName: 'setDefault',
    version: '1.0',
    bizParams: {
      ...params,
    },
  });
};

// 获取结算账户详情
export const getSettlementAccountDetail = (params: any) => {
  return request(waasPath + '/chainSettlementAccount/getDetail', {
    serviceName: 'ChainSettlementAccountService',
    methodName: 'getDetail',
    version: '1.0',
    bizParams: {
      ...params,
    },
  });
};


// 查询币种
export const getChainCoinList = (params: any) => {
  return request(waasPath + '/chainCoin/getList', {
    serviceName: 'ChainCoinService',
    methodName: 'getList',
    version: '1.0',
    bizParams: {
      ...params,
    },
  });
};