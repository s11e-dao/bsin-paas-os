import { request } from '@umijs/max'
let waasPath = process.env.contextPath_waas;

// 分页查询
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

// 添加平台
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

// 删除
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

// 查询详情
export const getPayInterfaceDetail = (params) => {
  console.log('params', params);
  return request(waasPath + '/payChannelInterface/getDetail', {
    serviceName: 'PayChannelInterfaceService',
    methodName: 'getDetail',
    version: '1.0',
    bizParams: {
      ...params,
    },
  });
};


