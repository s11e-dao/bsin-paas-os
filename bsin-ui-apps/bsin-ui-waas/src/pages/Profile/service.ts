import { request } from '@umijs/max'
let waasPath = process.env.contextPath_waas;

// 创建
export const createProfile = (params) => {
  return request(waasPath + '/customerProfile/create', {
    serviceName: 'CustomerProfileService',
    methodName: 'create',
    version: '1.0',
    bizParams: {
      ...params,
    },
  });
};

// 更新
export const updateProfile = (params) => {
  return request(waasPath + '/customerProfile/update', {
    serviceName: 'CustomerProfileService',
    methodName: 'update',
    version: '1.0',
    bizParams: {
      ...params,
    },
  });
};

// 分页查询详情
export const getCustomerProfilePageList = (params) => {
  console.log('params', params);
  return request(waasPath + '/customerProfile/getPageList', {
    serviceName: 'CustomerProfileService',
    methodName: 'getPageList',
    version: '1.0',
    bizParams: {
      ...params,
    },
  });
};

// 查询详情
export const getCustomerProfileDetail = (params) => {
  console.log('params', params);
  return request(waasPath + '/customerProfile/getDetail', {
    serviceName: 'CustomerProfileService',
    methodName: 'getDetail',
    version: '1.0',
    bizParams: {
      ...params,
    },
  });
};

export const getCustomerProfileTransferPageList = (params) => {};
export const getCustomerProfileMemberPageList = (params) => {};
export const getCustomerProfileFollowPageList = (params) => {};

export const getCustomerProfileTransferDetail = (params) => {};
export const getCustomerProfileMemberDetail = (params) => {};
export const getCustomerProfileFollowDetail = (params) => {};
