import { request } from '@umijs/max'
let waasPath = process.env.contextPath_waas;

// 分页查询
export const getDigitalAssetsCollectionPageList = (params) => {
  return request(waasPath + '/digitalAssetsCollection/getPageList', {
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
  return request(waasPath + '/digitalAssetsCollection/issue', {
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
  return request(waasPath + '/digitalAssetsCollection/putOnShelves', {
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
  return request(waasPath + '/digitalAssetsCollection/delete', {
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
  return request(waasPath + '/digitalAssetsCollection/getDetail', {
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
  return request(waasPath + '/digitalAssetsCollection/getDigitalAssetsMetadataImageInfo', {
    serviceName: 'DigitalAssetsCollectionService',
    methodName: 'getDigitalAssetsMetadataImageInfo',
    version: '1.0',
    bizParams: {
      ...params,
    },
  });
};



