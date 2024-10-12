import { request } from '@umijs/max'
let aiAgent = process.env.contextPath_aiAgent;

// 列表数据请求
export const getSensitiveWordsList = (params) => {
  return request(aiAgent + '/sensitiveWords/getList', {
    serviceName: 'SensitiveWordsService',
    methodName: 'getList',
    version: '1.0',
    bizParams: {
      ...params,
    },
  })
}
export const getSensitiveWordsPageList = (params) => {
  return request(aiAgent + '/sensitiveWords/getPageList', {
    serviceName: 'SensitiveWordsService',
    methodName: 'getPageList',
    version: '1.0',
    bizParams: {
      ...params,
    },
  })
}

// 删除操作
export const delSensitiveWords = (params) => {
  return request(aiAgent + '/sensitiveWords/delete', {
    serviceName: 'SensitiveWordsService',
    methodName: 'delete',
    version: '1.0',
    bizParams: {
      ...params,
    },
  })
}

//添加操作
export const addSensitiveWords = (params) => {
  return request(aiAgent + '/sensitiveWords/add', {
    serviceName: 'SensitiveWordsService',
    methodName: 'add',
    version: '1.0',
    bizParams: {
      ...params,
    },
  })
}

//编辑操作
export const editSensitiveWords = (params) => {
  return request(aiAgent + '/sensitiveWords/edit', {
    serviceName: 'SensitiveWordsService',
    methodName: 'edit',
    version: '1.0',
    bizParams: {
      ...params,
    },
  })
}
