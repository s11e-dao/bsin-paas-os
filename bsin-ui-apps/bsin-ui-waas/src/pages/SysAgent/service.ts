import { request } from '@umijs/max'
let crmPath = process.env.contextPath_crm;
let upmsPath = process.env.contextPath_upms;

// 分页查询
export const getSysAgentPageList = (params) => {
  return request(crmPath + '/sysAgent/getPageList', {
    serviceName: 'SysAgentService',
    methodName: 'getPageList',
    version: '1.0',
    bizParams: {
      ...params,
    },
  });
};

// 添加平台
export const addSysAgent = (params) => {
  return request(crmPath + '/sysAgent/add', {
    serviceName: 'SysAgentService',
    methodName: 'add',
    version: '1.0',
    bizParams: {
      ...params,
    },
  });
};

// 删除
export const deleteSysAgent = (params) => {
  return request(crmPath + '/sysAgent/delete', {
    serviceName: 'SysAgentService',
    methodName: 'delete',
    version: '1.0',
    bizParams: {
      ...params,
    },
  });
};

// 查询详情
export const getSysAgentDetail = (params) => {
  console.log('params', params);
  return request(crmPath + '/sysAgent/getDetail', {
    serviceName: 'SysAgentService',
    methodName: 'getDetail',
    version: '1.0',
    bizParams: {
      ...params,
    },
  });
};

// 查询产品列表
export const getProductList = (params) => {
  return request(upmsPath + '/product/getList', {
    serviceName: 'ProductService',
    methodName: 'getList',
    bizParams: {
      ...params,
    },
  });
};

