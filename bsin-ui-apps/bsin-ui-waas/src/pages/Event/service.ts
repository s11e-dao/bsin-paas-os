import { request } from '@umijs/max'
let crmPath = process.env.contextPath_crm;
let brmsPath = process.env.contextPath_brms;

// 获取列表
export const getPageList = (params: any) => {
  return request(crmPath + '/event/getPageList', {
    serviceName: 'EventService',
    methodName: 'getPageList',
    bizParams: {
      ...params,
    },
  });
};

// 新增
export const addPageList = (params: any) => {
  return request(crmPath + '/event/add', {
    serviceName: 'EventService',
    methodName: 'add',
    bizParams: {
      ...params,
    },
  });
};

// 编辑
export const editPageList = (params: any) => {
  return request(crmPath + '/dict/edit', {
    serviceName: 'EventService',
    methodName: 'edit',
    bizParams: {
      ...params,
    },
  });
};

// 删除
export const deletePageList = (params: any) => {
  return request(crmPath + '/event/delete', {
    serviceName: 'EventService',
    methodName: 'delete',
    bizParams: {
      ...params,
    },
  });
};

// 模型查询
export const getEventModelPageList = (params: any) => {
  return request(crmPath + '/eventModel/getPageList', {
    serviceName: 'EventModelService',
    methodName: 'getPageList',
    bizParams: {
      ...params,
    },
  });
};

// 模型新增
export const addEventModelPageList = (params: any) => {
  return request(crmPath + '/eventModel/add', {
    serviceName: 'EventModelService',
    methodName: 'add',
    bizParams: {
      ...params,
    },
  });
};


// 模型删除
export const deleteEventModelPageList = (params: any) => {
  return request(crmPath + '/eventModel/delete', {
    serviceName: 'EventModelService',
    methodName: 'delete',
    bizParams: {
      ...params,
    },
  });
};


// 模型
export const getRuleModelList = (params: any) => {
  return request(brmsPath + '/rule/getList', {
    serviceName: 'RuleService',
    methodName: 'getList',
    bizParams: {
      ...params,
    },
  });
};

export const getInferenceModelList = (params: any) => {
  return request(crmPath + '/eventModel/getList', {
    serviceName: 'EventModelService',
    methodName: 'getList',
    bizParams: {
      ...params,
    },
  });
};

export const getFlowModelList = (params: any) => {
  return request(crmPath + '/eventModel/getList', {
    serviceName: 'EventModelService',
    methodName: 'getList',
    bizParams: {
      ...params,
    },
  });
};

export const getFormModelList = (params: any) => {
  return request(crmPath + '/eventModel/getList', {
    serviceName: 'EventModelService',
    methodName: 'getList',
    bizParams: {
      ...params,
    },
  });
};