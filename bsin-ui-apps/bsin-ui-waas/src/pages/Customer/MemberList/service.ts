import { request } from '@umijs/max'

// 分页查询客户配置
export const getMemberPageList = (params) => {
  return request('/list', {
    serviceName: 'MemberService',
    methodName: 'getPageList',
    version: '1.0',
    bizParams: {
      ...params,
    },
  });
};

export const getMemberDetail = (params) => {
  return request('/getDetail', {
    serviceName: 'MemberService',
    methodName: 'getDetail',
    version: '1.0',
    bizParams: {
      ...params,
    },
  });
};

