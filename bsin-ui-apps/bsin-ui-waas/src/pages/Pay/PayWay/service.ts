import { request } from '@umijs/max'
let waasPath = process.env.contextPath_waas;

// 分页查询支付方式列表
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

// 获取支付方式列表（不分页）
export const getPayWayList = (params) => {
  return request(waasPath + '/payWay/getList', {
    serviceName: 'PayWayService',
    methodName: 'getList',
    version: '1.0',
    bizParams: {
      ...params,
    },
  });
};

// 添加支付方式
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

// 编辑支付方式
export const editPayWay = (params) => {
  return request(waasPath + '/payWay/edit', {
    serviceName: 'PayWayService',
    methodName: 'edit',
    version: '1.0',
    bizParams: {
      ...params,
    },
  });
};

// 删除支付方式
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

// 查询支付方式详情
export const getPayWayDetail = (params) => {
  return request(waasPath + '/payWay/getDetail', {
    serviceName: 'PayWayService',
    methodName: 'getDetail',
    version: '1.0',
    bizParams: {
      ...params,
    },
  });
};


