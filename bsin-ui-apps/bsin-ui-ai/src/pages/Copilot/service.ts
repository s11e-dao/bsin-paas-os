import { request } from '@umijs/max'
let aiPath = process.env.contextPath_ai;

// 列表数据请求
export const getCopilotList = (params) => {
  return request(aiPath + '/getList', {
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
  return request(aiPath + '/getPageList', {
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
  return request(aiPath + '/delete', {
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
  return request(aiPath + '/add', {
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
  return request(aiPath + '/detail', {
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
  return request(aiPath + '/edit', {
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
  return request(aiPath + '/setDefaultCopilot', {
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
  return request(aiPath + '/getDefaultCopilot', {
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
  return request(aiPath + '/chat', {
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
  return request(aiPath + '/generateImage', {
    serviceName: 'ImageGenerationService',
    methodName: 'generateImage',
    version: '1.0',
    bizParams: {
      ...params,
    },
  })
}
