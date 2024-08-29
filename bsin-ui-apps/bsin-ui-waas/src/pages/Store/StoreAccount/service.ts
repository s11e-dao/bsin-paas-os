import { request } from '@umijs/max'
let omsPath = process.env.contextPath_oms;

// 分页查询合约配置
export const getContractProtocolPageList = (params) => {
  return request('/list', {
    serviceName: 'AdminContractProtocolService',
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
    serviceName: 'AdminContractProtocolService',
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
    serviceName: 'AdminContractProtocolService',
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
    serviceName: 'AdminContractProtocolService',
    methodName: 'getContractProtocolDetail',
    version: '1.0',
    bizParams: {
      ...params,
    },
  });
};
