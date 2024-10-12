import { request } from '@umijs/max'
let aiAgent = process.env.contextPath_aiAgent;

// 列表数据请求
export const getChatHistoryList = (params) => {
  return request(aiAgent + '/chat/getChatHistoryList', {
    serviceName: 'ChatService',
    methodName: 'getChatHistoryList',
    version: '1.0',
    bizParams: {
      ...params,
    },
  })
}

// 删除操作
export const delChat = (params) => {
  return request(aiAgent + '/chat/delete', {
    serviceName: 'ChatService',
    methodName: 'delete',
    version: '1.0',
    bizParams: {
      ...params,
    },
  })
}

// 获取快捷回复： 后续增加在聊天请求报文中
export const getQuickReplies = (params) => {
  return request(aiAgent + '/chat/getQuickReplies', {
    serviceName: 'ChatService',
    methodName: 'getQuickReplies',
    version: '1.0',
    bizParams: {
      ...params,
    },
  })
}

// 获取默认copilot
export const getDefaultCopilot = (params) => {
  return request(aiAgent + '/copilot/getDefault', {
    serviceName: 'CopilotService',
    methodName: 'getDefault',
    version: '1.0',
    bizParams: {
      ...params,
    },
  })
}
