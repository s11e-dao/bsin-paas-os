import { request } from '@umijs/max'
let waasPath = process.env.contextPath_waas;

// 分页查询分账配置
export const getProfitSharingConfigPageList = (params: any) => {
  return request(waasPath + '/profitSharingConfig/getPageList', {
    serviceName: 'ProfitSharingConfigService',
    methodName: 'getPageList',
    version: '1.0',
    bizParams: {
      ...params,
    },
  });
};

// 查询分账配置详情
export const getProfitSharingConfigDetail = (params: any) => {
  return request(waasPath + '/profitSharingConfig/getDetail', {
    serviceName: 'ProfitSharingConfigService',
    methodName: 'getDetail',
    version: '1.0',
    bizParams: {
      ...params,
    },
  });
};
