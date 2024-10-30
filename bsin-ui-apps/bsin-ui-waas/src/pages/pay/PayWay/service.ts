import { request } from '@umijs/max'
let waasPath = process.env.contextPath_waas;

// 分页查询
export const getPayWayPageList = (params) => {
  return request(waasPath + '/payWay/getPageList', {
    serviceName: 'PayWayService',
    methodName: 'getPageList',
    version: '1.0',
    bizParams: {
      ...params,
    },
  });
};

// 添加平台
export const addPayWay = (params) => {
  return request(waasPath + '/payWay/add', {
    serviceName: 'PayWayService',
    methodName: 'add',
    version: '1.0',
    bizParams: {
      ...params,
    },
  });
};

// 删除
export const deletePayWay = (params) => {
  return request(waasPath + '/payWay/delete', {
    serviceName: 'PayWayService',
    methodName: 'delete',
    version: '1.0',
    bizParams: {
      ...params,
    },
  });
};

// 查询详情
export const getPayWayDetail = (params) => {
  console.log('params', params);
  return request(waasPath + '/payWay/getDetail', {
    serviceName: 'PayWayService',
    methodName: 'getDetail',
    version: '1.0',
    bizParams: {
      ...params,
    },
  });
};


