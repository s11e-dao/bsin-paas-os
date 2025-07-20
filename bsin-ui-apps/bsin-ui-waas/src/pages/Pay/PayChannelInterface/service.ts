import { request } from '@umijs/max'
let waasPath = process.env.contextPath_waas;

// 分页查询支付接口列表
export const getPayInterfacePageList = (params) => {
  return request(waasPath + '/payChannelInterface/getPageList', {
    serviceName: 'PayChannelInterfaceService',
    methodName: 'getPageList',
    version: '1.0',
    bizParams: {
      ...params,
    },
  });
};

// 获取支付接口列表（不分页）
export const getPayInterfaceList = (params) => {
  return request(waasPath + '/payChannelInterface/getList', {
    serviceName: 'PayChannelInterfaceService',
    methodName: 'getList',
    version: '1.0',
    bizParams: {
      ...params,
    },
  });
};

// 添加支付接口
export const addPayInterface = (params) => {
  return request(waasPath + '/payChannelInterface/add', {
    serviceName: 'PayChannelInterfaceService',
    methodName: 'add',
    version: '1.0',
    bizParams: {
      ...params,
    },
  });
};

// 编辑支付接口
export const editPayInterface = (params) => {
  return request(waasPath + '/payChannelInterface/edit', {
    serviceName: 'PayChannelInterfaceService',
    methodName: 'edit',
    version: '1.0',
    bizParams: {
      ...params,
    },
  });
};

// 删除支付接口
export const deletePayInterface = (params) => {
  return request(waasPath + '/payChannelInterface/delete', {
    serviceName: 'PayChannelInterfaceService',
    methodName: 'delete',
    version: '1.0',
    bizParams: {
      ...params,
    },
  });
};

// 查询支付接口详情
export const getPayInterfaceDetail = (params) => {
  return request(waasPath + '/payChannelInterface/getDetail', {
    serviceName: 'PayChannelInterfaceService',
    methodName: 'getDetail',
    version: '1.0',
    bizParams: {
      ...params,
    },
  });
};



