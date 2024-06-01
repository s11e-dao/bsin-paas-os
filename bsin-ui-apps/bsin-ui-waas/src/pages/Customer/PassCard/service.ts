import { request } from '@umijs/max'
let waasPath = process.env.contextPath_waas;

// 分页查询合约配置
export const getPassCardPageList = (params) => {
  return request(waasPath + '/customerPassCard/getPageList', {
    serviceName: 'CustomerPassCardService',
    methodName: 'getPageList',
    version: '1.0',
    bizParams: {
      ...params,
    },
  });
};

// 创建合约配置
export const addPassCard = (params) => {
  return request(waasPath + '/customerPassCard/add', {
    serviceName: 'CustomerPassCardService',
    methodName: 'add',
    version: '1.0',
    bizParams: {
      ...params,
    },
  });
};

// 删除合约配置
export const deletePassCard = (params) => {
  return request(waasPath + '/customerPassCard/delete', {
    serviceName: 'CustomerPassCardService',
    methodName: 'delete',
    version: '1.0',
    bizParams: {
      ...params,
    },
  });
};

// 查询合约配置详情
export const getPassCardDetail = (params) => {
  console.log('params', params);
  return request(waasPath + '/customerPassCard/getDetail', {
    serviceName: 'CustomerPassCardService',
    methodName: 'getDetail',
    version: '1.0',
    bizParams: {
      ...params,
    },
  });
};
