import { request } from '@umijs/max'
let upmsPath = process.env.contextPath_upms;

// 请求appsList
export const getUserMenuTreeByAppCode = (params: object) => {
  return request(upmsPath + '/menu/getUserMenuTree', {
    serviceName: 'MenuService',
    methodName: 'getUserMenuTreeByAppCode',
    bizParams: {
      ...params,
    },
  });
};

// 请求示例
// export const add = (params: object) => {
//   return bsinRequest('/atomicServiceService', {
//     serviceName: 'AtomicServiceService',
//     methodName: 'add',
//     bizParams: {
//       ...params,
//     },
//   });
// };
