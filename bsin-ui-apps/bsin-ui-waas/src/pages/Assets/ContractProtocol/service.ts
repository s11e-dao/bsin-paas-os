import { request } from '@umijs/max'
let waasPath = process.env.contextPath_waas;

//查询所有合约协议
export const getContractProtocolList = (params) => {
  return request(waasPath + '/contractProtocol/getList', {
    serviceName: 'ContractProtocolService',
    methodName: 'getList',
    version: '1.0',
    bizParams: {
      ...params,
    },
  });
};

// 分页查询合约配置
export const getContractProtocolPageList = (params) => {
  return request(waasPath + '/contractProtocol/getPageList', {
    serviceName: 'ContractProtocolService',
    methodName: 'getPageList',
    version: '1.0',
    bizParams: {
      ...params,
    },
  });
};

// 创建合约配置
export const addContractProtocol = (params) => {
  return request(waasPath + '/contractProtocol/add', {
    serviceName: 'ContractProtocolService',
    methodName: 'add',
    version: '1.0',
    bizParams: {
      ...params,
    },
  });
};

// 删除合约配置
export const deleteContractProtocol = (params) => {
  return request(waasPath + '/contractProtocol/delete', {
    serviceName: 'ContractProtocolService',
    methodName: 'delete',
    version: '1.0',
    bizParams: {
      ...params,
    },
  });
};

// 查询合约配置详情
export const getContractProtocolDetail = (params) => {
  console.log('params', params);
  return request(waasPath + '/contractProtocol/getDetail', {
    serviceName: 'ContractProtocolService',
    methodName: 'getDetail',
    version: '1.0',
    bizParams: {
      ...params,
    },
  });
};
