import { request } from '@umijs/max'
let appAgentPath = process.env.contextPath_appAgent;

// 列表数据请求
export const getCopilotList = (params) => {
  return request(appAgentPath + '/aiAgent/getList', {
    serviceName: 'AgentService',
    methodName: 'getList',
    version: '1.0',
    bizParams: {
      ...params,
    },
  })
}

// 列表数据请求
export const getCopilotPageList = (params) => {
  return request(appAgentPath + '/aiAgent/getPageList', {
    serviceName: 'AgentService',
    methodName: 'getPageList',
    version: '1.0',
    bizParams: {
      ...params,
    },
  })
}

// 删除操作
export const delCopilot = (params) => {
  return request(appAgentPath + '/aiAgent/delete', {
    serviceName: 'AgentService',
    methodName: 'delete',
    version: '1.0',
    bizParams: {
      ...params,
    },
  })
}

//添加操作
export const addCopilot = (params) => {
  return request(appAgentPath + '/aiAgent/add', {
    serviceName: 'AgentService',
    methodName: 'add',
    version: '1.0',
    bizParams: {
      ...params,
    },
  })
}

//详情
export const getCopilotDetail = (params) => {
  return request(appAgentPath + '/aiAgent/getDetail', {
    serviceName: 'AgentService',
    methodName: 'getDetail',
    version: '1.0',
    bizParams: {
      ...params,
    },
  })
}

//编辑操作
export const editCopilot = (params) => {
  return request(appAgentPath + '/aiAgent/edit', {
    serviceName: 'AgentService',
    methodName: 'edit',
    version: '1.0',
    bizParams: {
      ...params,
    },
  })
}

//设置为默认copilot
export const setDefaultCopilot = (params) => {
  return request(appAgentPath + '/aiAgent/setDefaultCopilot', {
    serviceName: 'AgentService',
    methodName: 'setDefaultCopilot',
    version: '1.0',
    bizParams: {
      ...params,
    },
  })
}

//获取默认copilot
export const getAppAgent = (params) => {
  return request(appAgentPath + '/aiAgent/getSysAppAgent', {
    serviceName: 'AgentService',
    methodName: 'getSysAppAgent',
    version: '1.0',
    bizParams: {
      ...params,
    },
  })
}


//和copilot对话
export const chatWithCopilot = (params) => {
  return request(appAgentPath + '/aiAgent/chat', {
    serviceName: 'AgentService',
    methodName: 'chat',
    version: '1.0',
    bizParams: {
      ...params,
    },
  })
}

//文生图
export const generateImage = (params) => {
  return request(appAgentPath + '/imageGeneration/generateImage', {
    serviceName: 'ImageGenerationService',
    methodName: 'generateImage',
    version: '1.0',
    bizParams: {
      ...params,
    },
  })
}

// 列表数据请求
export const getChatHistoryList = (params) => {
  return request(appAgentPath + '/conversationMessage/getList', {
    serviceName: 'AgentChatService',
    methodName: 'getList',
    version: '1.0',
    bizParams: {
      ...params,
    },
  })
}
