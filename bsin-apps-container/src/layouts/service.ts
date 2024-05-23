import { request } from '@umijs/max'
let appAgentPath = process.env.contextPath_appAgent;

// 列表数据请求
export const getCopilotList = (params) => {
  return request(appAgentPath + '/copilot/getList', {
    serviceName: 'CopilotService',
    methodName: 'getList',
    version: '1.0',
    bizParams: {
      ...params,
    },
  })
}

// 列表数据请求
export const getCopilotPageList = (params) => {
  return request(appAgentPath + '/copilot/getPageList', {
    serviceName: 'CopilotService',
    methodName: 'getPageList',
    version: '1.0',
    bizParams: {
      ...params,
    },
  })
}

// 删除操作
export const delCopilot = (params) => {
  return request(appAgentPath + '/copilot/delete', {
    serviceName: 'CopilotService',
    methodName: 'delete',
    version: '1.0',
    bizParams: {
      ...params,
    },
  })
}

//添加操作
export const addCopilot = (params) => {
  return request(appAgentPath + '/copilot/add', {
    serviceName: 'CopilotService',
    methodName: 'add',
    version: '1.0',
    bizParams: {
      ...params,
    },
  })
}

//详情
export const getCopilotDetail = (params) => {
  return request(appAgentPath + '/copilot/getDetail', {
    serviceName: 'CopilotService',
    methodName: 'getDetail',
    version: '1.0',
    bizParams: {
      ...params,
    },
  })
}

//编辑操作
export const editCopilot = (params) => {
  return request(appAgentPath + '/copilot/edit', {
    serviceName: 'CopilotService',
    methodName: 'edit',
    version: '1.0',
    bizParams: {
      ...params,
    },
  })
}

//设置为默认copilot
export const setDefaultCopilot = (params) => {
  return request(appAgentPath + '/copilot/setDefaultCopilot', {
    serviceName: 'CopilotService',
    methodName: 'setDefaultCopilot',
    version: '1.0',
    bizParams: {
      ...params,
    },
  })
}

//获取默认copilot
export const getAppAgent = (params) => {
  return request(appAgentPath + '/copilot/getAppAgent', {
    serviceName: 'CopilotService',
    methodName: 'getAppAgent',
    version: '1.0',
    bizParams: {
      ...params,
    },
  })
}


//和copilot对话
export const chatWithCopilot = (params) => {
  return request(appAgentPath + '/copilot/chat', {
    serviceName: 'CopilotService',
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
  return request(appAgentPath + '/chat/getChatHistoryList', {
    serviceName: 'ChatService',
    methodName: 'getChatHistoryList',
    version: '1.0',
    bizParams: {
      ...params,
    },
  })
}
