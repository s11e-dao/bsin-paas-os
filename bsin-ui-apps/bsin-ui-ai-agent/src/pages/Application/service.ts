import { request } from '@umijs/max';
let aiAgent = process.env.contextPath_aiAgent;

// 列表数据请求
export const getLLMPageList = (params) => {
  return request(aiAgent + '/llm/getPageList', {
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
  return request(aiAgent + '/llm/getList', {
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
  return request(aiAgent + '/llm/delete', {
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
  return request(aiAgent + '/llm/add', {
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
  return request(aiAgent + '/llm/edit', {
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
  return request(aiAgent + '/llm/getDetail', {
    serviceName: 'LLMService',
    methodName: 'getDetail',
    version: '1.0',
    bizParams: {
      ...params,
    },
  });
};
