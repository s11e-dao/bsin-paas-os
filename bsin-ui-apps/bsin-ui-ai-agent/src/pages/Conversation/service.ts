import { request } from '@umijs/max';
let aiAgent = process.env.contextPath_aiAgent;

// 列表数据请求
export const getconversationPageList = (params: any) => {
  return request(aiAgent + '/conversation/getPageList', {
    serviceName: 'conversationService',
    methodName: 'getPageList',
    version: '1.0',
    bizParams: {
      ...params,
    },
  });
};

// 列表数据请求
export const getconversationList = (params: any) => {
  return request(aiAgent + '/conversation/getList', {
    serviceName: 'conversationService',
    methodName: 'getList',
    version: '1.0',
    bizParams: {
      ...params,
    },
  });
};

// 删除
export const delconversationInfo = (params: any) => {
  return request(aiAgent + '/conversation/delete', {
    serviceName: 'conversationService',
    methodName: 'delete',
    version: '1.0',
    bizParams: {
      ...params,
    },
  });
};

//添加
export const addconversationInfo = (params: any) => {
  return request(aiAgent + '/conversation/add', {
    serviceName: 'conversationService',
    methodName: 'add',
    version: '1.0',
    bizParams: {
      ...params,
    },
  });
};

//编辑
export const editconversationInfo = (params: any) => {
  return request(aiAgent + '/conversation/edit', {
    serviceName: 'conversationService',
    methodName: 'edit',
    version: '1.0',
    bizParams: {
      ...params,
    },
  });
};

// 详情
export const getconversationDetail = (params: any) => {
  return request(aiAgent + '/conversation/getDetail', {
    serviceName: 'conversationService',
    methodName: 'getDetail',
    version: '1.0',
    bizParams: {
      ...params,
    },
  });
};

// 推送消息给智能体
export const messagePush = (params: any) => {
  return request(aiAgent + '/conversation/messagePush', {
    serviceName: 'conversationService',
    methodName: 'messagePush',
    version: '1.0',
    bizParams: {
      ...params,
    },
  });
};
