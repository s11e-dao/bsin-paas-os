import { request } from '@umijs/max'
let waasPath = process.env.contextPath_waas;

// 分页查询
export const getChainCoinPageList = (params: any) => {
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
export const addChainCoin = (params: any) => {
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
export const deleteChainCoin = (params: any) => {
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
export const getChainCoinDetail = (params: any) => {
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

// 编辑币种
export const editChainCoin = (params: any) => {
  return request(waasPath + '/chainCoin/edit', {
    serviceName: 'ChainCoinService',
    methodName: 'edit',
    version: '1.0',
    bizParams: {
      ...params,
    },
  });
};

// 币种下拉列表
export const coinDropDown = () => {
  return request(waasPath + '/chainCoin/coinDropDown', {
    serviceName: 'ChainCoinService',
    methodName: 'coinDropDown',
    version: '1.0',
    bizParams: {},
  });
};

// 链下拉列表
export const chainDropDown = () => {
  return request(waasPath + '/chainCoin/chainDropDown', {
    serviceName: 'ChainCoinService',
    methodName: 'chainDropDown',
    version: '1.0',
    bizParams: {},
  });
};

