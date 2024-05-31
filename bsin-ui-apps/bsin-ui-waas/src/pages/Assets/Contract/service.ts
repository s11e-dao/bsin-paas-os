import { request } from '@umijs/max'
let waasPath = process.env.contextPath_waas;

//查询所有合约协议
export const getContractList = (params) => {
  return request('/list', {
    serviceName: 'ContractService',
    methodName: 'getList',
    version: '1.0',
    bizParams: {
      ...params,
    },
  });
};

// 分页查询合约配置
export const getContractPageList = (params) => {
  return request('/list', {
    serviceName: 'ContractService',
    methodName: 'getPageList',
    version: '1.0',
    bizParams: {
      ...params,
    },
  });
};

// 创建合约配置
export const addContract = (params) => {
  return request('/add', {
    serviceName: 'ContractService',
    methodName: 'add',
    version: '1.0',
    bizParams: {
      ...params,
    },
  });
};

// 删除合约配置
export const deleteContract = (params) => {
  return request('/del', {
    serviceName: 'ContractService',
    methodName: 'delete',
    version: '1.0',
    bizParams: {
      ...params,
    },
  });
};

// 查询合约配置详情
export const getContractDetail = (params) => {
  console.log('params', params);
  return request('/view', {
    serviceName: 'ContractService',
    methodName: 'getDetail',
    version: '1.0',
    bizParams: {
      ...params,
    },
  });
};

// 部署合约
export const deployContract = (params) => {
  console.log('params', params);
  return request('/deploy', {
    serviceName: 'ContractService',
    methodName: 'deploy',
    version: '1.0',
    bizParams: {
      ...params,
    },
  });
};
