import { request } from '@umijs/max'
let waasPath = process.env.contextPath_waas;

// 分页查询
export const getChainCoinPageList = (params) => {
  return request(waasPath + '/chainCoin/getPageList', {
    serviceName: 'ChainCoinService',
    methodName: 'getPageList',
    version: '1.0',
    bizParams: {
      ...params,
    },
  });
};

// 添加平台
export const addChainCoin = (params) => {
  return request(waasPath + '/chainCoin/add', {
    serviceName: 'ChainCoinService',
    methodName: 'add',
    version: '1.0',
    bizParams: {
      ...params,
    },
  });
};

// 删除
export const deleteChainCoin = (params) => {
  return request(waasPath + '/chainCoin/delete', {
    serviceName: 'ChainCoinService',
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
  return request(waasPath + '/chainCoin/getDetail', {
    serviceName: 'ChainCoinService',
    methodName: 'getDetail',
    version: '1.0',
    bizParams: {
      ...params,
    },
  });
};


