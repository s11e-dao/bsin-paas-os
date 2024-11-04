import { request } from '@umijs/max'
let crmPath = process.env.contextPath_crm;
let waasPath = process.env.contextPath_waas;

// 分页查询
export const getBizRoleAppPageList = (params) => {
  return request(crmPath + '/bizRoleApp/getPageList', {
    serviceName: 'MerchantAppService',
    methodName: 'getPageList',
    version: '1.0',
    bizParams: {
      ...params,
    },
  });
};

// 创建
export const addBizRoleApp = (params) => {
  return request(crmPath + '/bizRoleApp/add', {
    serviceName: 'MerchantAppService',
    methodName: 'add',
    version: '1.0',
    bizParams: {
      ...params,
    },
  });
};

// 编辑
export const editBizRoleApp = (params) => {
  return request(crmPath + '/bizRoleApp/edit', {
    serviceName: 'BizRoleAppService',
    methodName: 'edit',
    version: '1.0',
    bizParams: {
      ...params,
    },
  });
};

// 删除
export const deleteBizRoleApp = (params) => {
  return request(crmPath + '/bizRoleApp/delete', {
    serviceName: 'BizRoleAppService',
    methodName: 'delete',
    version: '1.0',
    bizParams: {
      ...params,
    },
  });
};

// 查询详情
export const getBizRoleAppDetail = (params) => {
  console.log('params', params);
  return request(crmPath + '/bizRoleApp/getDetail', {
    serviceName: 'BizRoleAppService',
    methodName: 'getDetail',
    version: '1.0',
    bizParams: {
      ...params,
    },
  });
};


//查询支付接口列表数据请求
export const getPayChannelInterfaceList = (params) => {
  return request(waasPath + '/payChannelInterface/getList', {
    serviceName: 'payChannelInterfaceService',
    methodName: 'getList',
    version: '1.0',
    bizParams: {
      ...params,
    },
  })
}


//查询应用支付配置数据请求
export const getBizRoleAppPayChannelConfig = (params) => {
  return request(waasPath + '/payChannelConfig/getBizRoleAppPayChannelConfig', {
    serviceName: 'PayChannelConfigService',
    methodName: 'getBizRoleAppPayChannelConfig',
    version: '1.0',
    bizParams: {
      ...params,
    },
  })
}

