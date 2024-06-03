import { request } from '@umijs/max'
let aiAgent = process.env.contextPath_aiAgent;

// 列表数据请求
export const getCopilotList = (params) => {
  return request(aiAgent + '/getList', {
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
  return request(aiAgent + '/getPageList', {
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
  return request(aiAgent + '/delete', {
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
  return request(aiAgent + '/add', {
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
  return request(aiAgent + '/detail', {
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
  return request(aiAgent + '/edit', {
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
  return request(aiAgent + '/setDefaultCopilot', {
    serviceName: 'CopilotService',
    methodName: 'setDefaultCopilot',
    version: '1.0',
    bizParams: {
      ...params,
    },
  })
}

//获取默认copilot
export const getDefaultCopilot = (params) => {
  return request(aiAgent + '/getDefaultCopilot', {
    serviceName: 'CopilotService',
    methodName: 'getDefaultCopilot',
    version: '1.0',
    bizParams: {
      ...params,
    },
  })
}

//和copilot对话
export const chatWithCopilot = (params) => {
  return request(aiAgent + '/chat', {
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
  return request(aiAgent + '/generateImage', {
    serviceName: 'ImageGenerationService',
    methodName: 'generateImage',
    version: '1.0',
    bizParams: {
      ...params,
    },
  })
}
