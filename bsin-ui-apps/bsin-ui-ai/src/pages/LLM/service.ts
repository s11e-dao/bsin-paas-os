import { request } from '@umijs/max';
let aiPath = process.env.contextPath_ai;

// 列表数据请求
export const getLLMPageList = (params) => {
  return request(aiPath + '/getPageList', {
    serviceName: 'LLMService',
    methodName: 'getPageList',
    version: '1.0',
    bizParams: {
      ...params,
    },
  });
};

// 列表数据请求
export const getLLMList = (params) => {
  return request(aiPath + '/getList', {
    serviceName: 'LLMService',
    methodName: 'getList',
    version: '1.0',
    bizParams: {
      ...params,
    },
  });
};

// 删除
export const delLLMInfo = (params) => {
  return request(aiPath + '/delete', {
    serviceName: 'LLMService',
    methodName: 'delete',
    version: '1.0',
    bizParams: {
      ...params,
    },
  });
};

//添加
export const addLLMInfo = (params) => {
  return request(aiPath + '/add', {
    serviceName: 'LLMService',
    methodName: 'add',
    version: '1.0',
    bizParams: {
      ...params,
    },
  });
};

//编辑
export const editLLMInfo = (params) => {
  return request(aiPath + '/edit', {
    serviceName: 'LLMService',
    methodName: 'edit',
    version: '1.0',
    bizParams: {
      ...params,
    },
  });
};

// 详情
export const getLLMDetail = (params) => {
  return request(aiPath + '/getDetail', {
    serviceName: 'LLMService',
    methodName: 'getDetail',
    version: '1.0',
    bizParams: {
      ...params,
    },
  });
};
