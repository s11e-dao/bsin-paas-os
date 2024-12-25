import { request } from '@umijs/max'
let upmsPath = process.env.contextPath_upms;

// 分页查询广告配置
export const getAdsPageList = (params) => {
  return request(upmsPath + '/logLogin/getPageList', {
    serviceName: 'LogLoginService',
    methodName: 'getPageList',
    version: '1.0',
    bizParams: {
      ...params,
    },
  });
};

// 创建广告配置
export const addAds = (params) => {
  return request(upmsPath + '/logLogin/add', {
    serviceName: 'LogLoginService',
    methodName: 'add',
    version: '1.0',
    bizParams: {
      ...params,
    },
  });
};

// 编辑广告配置
export const editAds = (params) => {
  return request(upmsPath + '/logLogin/edit', {
    serviceName: 'LogLoginService',
    methodName: 'edit',
    version: '1.0',
    bizParams: {
      ...params,
    },
  });
};

// 删除广告配置
export const deleteAds = (params) => {
  return request(upmsPath + '/logLogin/delete', {
    serviceName: 'LogLoginService',
    methodName: 'delete',
    version: '1.0',
    bizParams: {
      ...params,
    },
  });
};

// 查询广告配置详情
export const getAdsDetail = (params) => {
  console.log('params', params);
  return request(upmsPath + '/logLogin/getDetail', {
    serviceName: 'LogLoginService',
    methodName: 'getDetail',
    version: '1.0',
    bizParams: {
      ...params,
    },
  });
};
