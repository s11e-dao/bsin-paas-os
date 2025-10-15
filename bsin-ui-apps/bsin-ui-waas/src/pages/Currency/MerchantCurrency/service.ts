import { request } from '@umijs/max'
let waasPath = process.env.contextPath_waas;

// 分页查询
export const getChainCoinPageList = (params) => {
  return request(waasPath + '/bizRoleTypeChainCoin/getPageList', {
    serviceName: 'CustomerChainCoinService',
    methodName: 'getPageList',
    version: '1.0',
    bizParams: {
      ...params,
    },
  });
};

// 添加平台
export const addChainCoin = (params) => {
  return request(waasPath + '/bizRoleTypeChainCoin/add', {
    serviceName: 'CustomerChainCoinService',
    methodName: 'add',
    version: '1.0',
    bizParams: {
      ...params,
    },
  });
};

// 删除
export const deleteChainCoin = (params) => {
  return request(waasPath + '/bizRoleTypeChainCoin/delete', {
    serviceName: 'CustomerChainCoinService',
    methodName: 'delete',
    version: '1.0',
    bizParams: {
      ...params,
    },
  });
};

// 查询详情
export const getChainCoinDetail = (params) => {
  console.log('params', params);
  return request(waasPath + '/bizRoleTypeChainCoin/getDetail', {
    serviceName: 'CustomerChainCoinService',
    methodName: 'getDetail',
    version: '1.0',
    bizParams: {
      ...params,
    },
  });
};


