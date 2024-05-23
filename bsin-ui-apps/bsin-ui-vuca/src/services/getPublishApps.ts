import { request } from '@umijs/max'

// 请求示例
export const getPublishApps = (params: any) => {
  return request('/getPublishedApps', {
    serviceName: 'AppService',
    methodName: 'getPublishedApps',
    bizParams: {
      ...params,
    },
  });
};
