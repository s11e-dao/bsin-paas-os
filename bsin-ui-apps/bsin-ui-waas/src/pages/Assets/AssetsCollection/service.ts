import { request } from '@umijs/max'

// 分页查询
export const getDigitalAssetsCollectionPageList = (params) => {
  return request('/list', {
    serviceName: 'DigitalAssetsCollectionService',
    methodName: 'getPageList',
    version: '1.0',
    bizParams: {
      ...params,
    },
  });
};

// 发行
export const issueDigitalAssetsCollection = (params) => {
  return request('/add', {
    serviceName: 'DigitalAssetsCollectionService',
    methodName: 'issue',
    version: '1.0',
    bizParams: {
      ...params,
    },
  });
};

// 上架
export const putOnShelvesDigitalAssetsCollection = (params) => {
  return request('/add', {
    serviceName: 'DigitalAssetsCollectionService',
    methodName: 'putOnShelves',
    version: '1.0',
    bizParams: {
      ...params,
    },
  });
};

// 删除
export const deleteDigitalAssetsCollection = (params) => {
  return request('/del', {
    serviceName: 'DigitalAssetsCollectionService',
    methodName: 'delete',
    version: '1.0',
    bizParams: {
      ...params,
    },
  });
};

// 查询详情
export const getDigitalAssetsCollectionDetail = (params) => {
  console.log('params', params);
  return request('/view', {
    serviceName: 'DigitalAssetsCollectionService',
    methodName: 'getDetail',
    version: '1.0',
    bizParams: {
      ...params,
    },
  });
};

// 获取数字资产元数据图片
export const getDigitalAssetsMetadataImageInfo = (params) => {
  console.log('params', params);
  return request('/view', {
    serviceName: 'DigitalAssetsCollectionService',
    methodName: 'getDigitalAssetsMetadataImageInfo',
    version: '1.0',
    bizParams: {
      ...params,
    },
  });
};



