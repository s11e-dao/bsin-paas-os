import { request } from '@umijs/max'
let crmPath = process.env.contextPath_crm;

// 分页查询合约配置
export const getEquityPageList = (params) => {
  return request('/list', {
    serviceName: 'EquityService',
    methodName: 'getPageList',
    version: '1.0',
    bizParams: {
      ...params,
    },
  });
};

export const getListByCategoryNo = (params) => {
  return request('/list', {
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
  return request('/add', {
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
  return request('/del', {
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
  return request('/view', {
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
  return request('/configEquity', {
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
  return request('/deleteEquity', {
    serviceName: 'EquityConfigService',
    methodName: 'delete',
    version: '1.0',
    bizParams: {
      ...params,
    },
  });
};
