import { request } from '@umijs/max'
let upmsPath = process.env.contextPath_upms;

// 请求示例
export const getPublishApps = (params: any) => {
  return request(upmsPath + '/app/getPublishedApps', {
    bizParams: {
      ...params,
    }
  });
};
