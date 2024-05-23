import { request } from '@umijs/max'
let upmsPath = process.env.contextPath_upms;

// 获取用户的应用列表
export const getAppByUserId = (params: any) => {
  return request(upmsPath + '/user/getAppByUserId', {
    serviceName: 'UserService',
    methodName: 'getAppByUserId',
    bizParams: {
      ...params,
    }
  });
};

export const getAppListByUserId = (params: any) => {
  return request(upmsPath + '/user/getAppListByUserId', {
    serviceName: 'UserService',
    methodName: 'getAppListByUserId',
    bizParams: {
      ...params,
    }
  });
};


