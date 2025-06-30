import { request } from '@umijs/max'
let waasPath = process.env.contextPath_waas;

// 分页查询配置
export const getDigitalAssetsItemPageList = (params) => {
  return request(waasPath + '/digitalAssetsItem/getPageList', {
    serviceName: 'DigitalAssetsItemService',
    methodName: 'getPageList',
    version: '1.0',
    bizParams: {
      ...params,
    },
  });
};

// 查询数字资产列表
export const getDigitalAssetsItemList = (params) => {
  return request(waasPath + '/digitalAssetsItem/getList', {
    serviceName: 'DigitalAssetsItemService',
    methodName: 'getList',
    version: '1.0',
    bizParams: {
      ...params,
    },
  });
};

// 查询数字资产曲线积分列表
export const getBondingCurveTokenList = (params) => {
  return request(waasPath + '/digitalAssetsItem/getCurveList', {
    serviceName: 'BondingCurveTokenService',
    methodName: 'getCurveList',
    version: '1.0',
    bizParams: {
      ...params,
    },
  });
};

// 创建
export const addDigitalAssetsItem = (params) => {
  return request(waasPath + '/digitalAssetsItem/add', {
    serviceName: 'DigitalAssetsItemService',
    methodName: 'add',
    version: '1.0',
    bizParams: {
      ...params,
    },
  });
};

// 删除
export const deleteDigitalAssetsItem = (params) => {
  return request(waasPath + '/digitalAssetsItem/delete', {
    serviceName: 'DigitalAssetsItemService',
    methodName: 'delete',
    version: '1.0',
    bizParams: {
      ...params,
    },
  });
};

// 查询数字资产详情
export const getDigitalAssetsItemDetail = (params) => {
  console.log('params', params);
  return request(waasPath + '/digitalAssetsItem/getDetail', {
    serviceName: 'DigitalAssetsItemService',
    methodName: 'getDetail',
    version: '1.0',
    bizParams: {
      ...params,
    },
  });
};


// 分页查询数字资产领取码
export const getDigitalAssetsItemObtainCodePageList = (params) => {
  return request(waasPath + '/digitalAssetsItem/getObtainCodePageList', {
    serviceName: 'DigitalAssetsItemService',
    methodName: 'getObtainCodePageList',
    version: '1.0',
    bizParams: {
      ...params,
    },
  });
};
