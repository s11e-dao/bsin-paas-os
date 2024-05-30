import { request } from '@umijs/max'

// 分页查询合约配置
export const getConditionPageList = (params) => {
  return request('/list', {
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
  return request('/list', {
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
  return request('/add', {
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
  return request('/del', {
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
  return request('/edit', {
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
  return request('/view', {
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
  return request('/configCondition', {
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
  return request('/deleteCondition', {
    serviceName: 'ConditionConfigService',
    methodName: 'delete',
    version: '1.0',
    bizParams: {
      ...params,
    },
  });
};
