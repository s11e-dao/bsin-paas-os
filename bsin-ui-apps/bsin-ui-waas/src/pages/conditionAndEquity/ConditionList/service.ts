import { request } from '@umijs/max'
let crmPath = process.env.contextPath_crm;

// 分页查询
export const getConditionPageList = (params) => {
  return request(crmPath + '/condition/getPageList', {
    serviceName: 'ConditionService',
    methodName: 'getPageList',
    version: '1.0',
    bizParams: {
      ...params,
    },
  });
};

// 分页配置
export const getConditionListByCategoryNo = (params) => {
  return request(crmPath + '/conditionConfig/getListByCategoryNo', {
    serviceName: 'ConditionConfigService',
    methodName: 'getListByCategoryNo',
    version: '1.0',
    bizParams: {
      ...params,
    },
  });
};

// 添加条件
export const addCondition = (params) => {
  return request(crmPath + '/condition/add', {
    serviceName: 'ConditionService',
    methodName: 'add',
    version: '1.0',
    bizParams: {
      ...params,
    },
  });
};

// 删除条件
export const deleteCondition = (params) => {
  return request(crmPath + '/condition/delete', {
    serviceName: 'ConditionService',
    methodName: 'delete',
    version: '1.0',
    bizParams: {
      ...params,
    },
  });
};

// 编辑
export const EditCondition = (params) => {
  console.log('params', params);
  return request(crmPath + '/condition/edit', {
    serviceName: 'ConditionService',
    methodName: 'edit',
    version: '1.0',
    bizParams: {
      ...params,
    },
  });
};

// 查询条件配置详情
export const getConditionDetail = (params) => {
  console.log('params', params);
  return request(crmPath + '/condition/getDetail', {
    serviceName: 'ConditionService',
    methodName: 'getDetail',
    version: '1.0',
    bizParams: {
      ...params,
    },
  });
};



export const configCondition = (params) => {
  console.log('params', params);
  return request(crmPath + '/conditionConfig/config', {
    serviceName: 'ConditionConfigService',
    methodName: 'config',
    version: '1.0',
    bizParams: {
      ...params,
    },
  });
};

export const deleteConditionConfig = (params) => {
  console.log('params', params);
  return request(crmPath + '/conditionConfig/delete', {
    serviceName: 'ConditionConfigService',
    methodName: 'delete',
    version: '1.0',
    bizParams: {
      ...params,
    },
  });
};
