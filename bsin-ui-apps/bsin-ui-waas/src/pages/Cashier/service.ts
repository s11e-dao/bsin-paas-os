import { request } from '@umijs/max'
let crmPath = process.env.contextPath_crm;

// 分页查询合约配置
export const getContractProtocolPageList = (params) => {
  return request('/list', {
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
  return request('/add', {
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
  return request('/del', {
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
  return request('/view', {
    serviceName: 'ContractProtocolService',
    methodName: 'getContractProtocolDetail',
    version: '1.0',
    bizParams: {
      ...params,
    },
  });
};
