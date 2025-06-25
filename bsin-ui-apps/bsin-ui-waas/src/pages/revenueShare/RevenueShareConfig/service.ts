import { request } from '@umijs/max'
let waasath = process.env.contextPath_waas;

// 分页查询活动配置
export const getActivityPageList = (params) => {
  return request(waasath + '/profitSharingConfig/getPageList', {
    serviceName: 'ProfitSharingConfigService',
    methodName: 'getPageList',
    version: '1.0',
    bizParams: {
      ...params,
    },
  });
};

// 创建活动配置
export const configDisBrokerageConfig = (params) => {
  return request(waasath + '/profitSharingConfig/config', {
    serviceName: 'ProfitSharingConfigService',
    methodName: 'config',
    version: '1.0',
    bizParams: {
      ...params,
    },
  });
};


// 查询活动配置详情
export const getDisBrokerageConfigDetail = (params) => {
  console.log('params', params);
  return request(waasath + '/disBrokerageConfig/getDetail', {
    serviceName: 'ProfitSharingConfigService',
    methodName: 'getDetail',
    version: '1.0',
    bizParams: {
      ...params,
    },
  });
};

