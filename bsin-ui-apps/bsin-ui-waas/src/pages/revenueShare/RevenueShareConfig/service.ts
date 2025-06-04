import { request } from '@umijs/max'
let crmPath = process.env.contextPath_crm;

// 分页查询活动配置
export const getActivityPageList = (params) => {
  return request(crmPath + '/disBrokerageConfig/getPageList', {
    serviceName: 'DisBrokerageConfigService',
    methodName: 'getPageList',
    version: '1.0',
    bizParams: {
      ...params,
    },
  });
};

// 创建活动配置
export const configDisBrokerageConfig = (params) => {
  return request(crmPath + '/disBrokerageConfig/config', {
    serviceName: 'DisBrokerageConfigService',
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
  return request(crmPath + '/disBrokerageConfig/getDetail', {
    serviceName: 'DisBrokerageConfigService',
    methodName: 'getDetail',
    version: '1.0',
    bizParams: {
      ...params,
    },
  });
};

