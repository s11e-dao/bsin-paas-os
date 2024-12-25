import { request } from '@umijs/max'
let upmsPath = process.env.contextPath_upms;

// 分页查询广告配置
export const getAdsPageList = (params) => {
  return request(upmsPath + '/logOperate/getPageList', {
    serviceName: 'LogOperateService',
    methodName: 'getPageList',
    version: '1.0',
    bizParams: {
      ...params,
    },
  });
};

// 创建广告配置
export const addAds = (params) => {
  return request(upmsPath + '/logOperate/add', {
    serviceName: 'LogOperateService',
    methodName: 'add',
    version: '1.0',
    bizParams: {
      ...params,
    },
  });
};

// 编辑广告配置
export const editAds = (params) => {
  return request(upmsPath + '/logOperate/edit', {
    serviceName: 'LogOperateService',
    methodName: 'edit',
    version: '1.0',
    bizParams: {
      ...params,
    },
  });
};

// 删除广告配置
export const deleteAds = (params) => {
  return request(upmsPath + '/logOperate/delete', {
    serviceName: 'LogOperateService',
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
  return request(upmsPath + '/logOperate/getDetail', {
    serviceName: 'LogOperateService',
    methodName: 'getDetail',
    version: '1.0',
    bizParams: {
      ...params,
    },
  });
};
