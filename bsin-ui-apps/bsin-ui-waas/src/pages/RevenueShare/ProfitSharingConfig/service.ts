import { request } from '@umijs/max'
let waasPath = process.env.contextPath_waas;

// 分页查询分账明细
export const getRevenueSharePageList = (params: any) => {
  return request(waasPath + '/transaction/getPageList', {
    serviceName: 'TransactionService',
    methodName: 'getPageList',
    version: '1.0',
    bizParams: {
      ...params,
    },
  });
};

// 查询分账明细详情
export const getRevenueShareDetail = (params: any) => {
  return request(waasPath + '/transaction/getDetail', {
    serviceName: 'TransactionService',
    methodName: 'getDetail',
    version: '1.0',
    bizParams: {
      ...params,
    },
  });
};

// 让利配置相关服务

// 保存让利配置
export const configProfitSharingConfig = (params: any) => {
  return request(waasPath + '/merchant/config', {
    serviceName: 'MerchantConfigService',
    methodName: 'config',
    version: '1.0',
    bizParams: {
      ...params,
    },
  });
};

// 获取让利配置详情
export const getProfitSharingConfigDetail = (params: any) => {
  return request(waasPath + '/merchant/getDetail', {
    serviceName: 'MerchantConfigService',
    methodName: 'getDetail',
    version: '1.0',
    bizParams: {
      ...params,
    },
  });
};

// 分页查询让利配置列表
export const getProfitSharingConfigPageList = (params: any) => {
  return request(waasPath + '/merchant/getPageList', {
    serviceName: 'MerchantConfigService',
    methodName: 'getPageList',
    version: '1.0',
    bizParams: {
      ...params,
    },
  });
};
