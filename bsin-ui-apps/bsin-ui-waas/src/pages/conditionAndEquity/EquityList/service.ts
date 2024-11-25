import { request } from '@umijs/max'
let crmPath = process.env.contextPath_crm;

// 分页查询合约配置
export const getEquityPageList = (params) => {
  return request(crmPath + '/equity/getPageList', {
    serviceName: 'EquityService',
    methodName: 'getPageList',
    version: '1.0',
    bizParams: {
      ...params,
    },
  });
};

export const getListByCategoryNo = (params) => {
  return request(crmPath + '/equityConfig/getListByCategoryNo', {
    serviceName: 'EquityConfigService',
    methodName: 'getListByCategoryNo',
    version: '1.0',
    bizParams: {
      ...params,
    },
  });
};

// 创建合约配置
export const addEquity = (params) => {
  return request(crmPath + '/equity/add', {
    serviceName: 'EquityService',
    methodName: 'add',
    version: '1.0',
    bizParams: {
      ...params,
    },
  });
};

// 删除合约配置
export const deleteEquity = (params) => {
  return request(crmPath + '/equity/delete', {
    serviceName: 'EquityService',
    methodName: 'delete',
    version: '1.0',
    bizParams: {
      ...params,
    },
  });
};

// 查询合约配置详情
export const getEquityDetail = (params) => {
  console.log('params', params);
  return request(crmPath + '/equity/getDetail', {
    serviceName: 'EquityService',
    methodName: 'getDetail',
    version: '1.0',
    bizParams: {
      ...params,
    },
  });
};



export const configEquity = (params) => {
  console.log('params', params);
  return request(crmPath + '/equityConfig/config', {
    serviceName: 'EquityConfigService',
    methodName: 'config',
    version: '1.0',
    bizParams: {
      ...params,
    },
  });
};

export const deleteEquityConfig = (params) => {
  console.log('params', params);
  return request(crmPath + '/equityConfig/delete', {
    serviceName: 'EquityConfigService',
    methodName: 'delete',
    version: '1.0',
    bizParams: {
      ...params,
    },
  });
};
