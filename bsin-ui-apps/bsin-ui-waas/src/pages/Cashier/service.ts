import { request } from '@umijs/max'
let waasPath = process.env.contextPath_waas;

// 分页查询合约配置
export const getContractProtocolPageList = (params) => {
  return request(waasPath + '/transaction/getPageList', {
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
  return request(waasPath + '/transaction/add', {
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
  return request(waasPath + '/transaction/del', {
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
  return request(waasPath + '/transaction/view', {
    serviceName: 'ContractProtocolService',
    methodName: 'getContractProtocolDetail',
    version: '1.0',
    bizParams: {
      ...params,
    },
  });
};
