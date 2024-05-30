import { request } from '@umijs/max'

// 分页查询客户配置
export const getMemberAssetsPageList = (params) => {
  return request('/getPageList  ', {
    serviceName: 'CustomerDigitalAssetsService',
    methodName: 'getPageList',
    version: '1.0',
    bizParams: {
      ...params,
    },
  });
};

export const getMemberAssetsDetail = (params) => {
  return request('/getDetail', {
    serviceName: 'CustomerDigitalAssetsService',
    methodName: 'getDetail',
    version: '1.0',
    bizParams: {
      ...params,
    },
  });
};
