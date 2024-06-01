import { request } from '@umijs/max'
let crmPath = process.env.contextPath_crm;

// 分页查询客户配置
export const getMemberPageList = (params) => {
  return request(crmPath + '/member/getPageList', {
    serviceName: 'MemberService',
    methodName: 'getPageList',
    version: '1.0',
    bizParams: {
      ...params,
    },
  });
};

export const getMemberDetail = (params) => {
  return request(crmPath + '/member/getDetail', {
    serviceName: 'MemberService',
    methodName: 'getDetail',
    version: '1.0',
    bizParams: {
      ...params,
    },
  });
};

