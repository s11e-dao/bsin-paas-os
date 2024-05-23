import { request } from '@umijs/max'
let aiPath = process.env.contextPath_ai;

// 列表数据请求
export const getKnowledgeBaseList = (params) => {
  return request(aiPath + '/getList', {
    serviceName: 'KnowledgeBaseService',
    methodName: 'getList',
    version: '1.0',
    bizParams: {
      ...params,
    },
  })
}

// 列表数据请求
export const getKnowledgeBasePageList = (params) => {
  return request(aiPath + '/getPageList', {
    serviceName: 'KnowledgeBaseService',
    methodName: 'getPageList',
    version: '1.0',
    bizParams: {
      ...params,
    },
  })
}

// 删除知识库操作
export const delKnowledgeBase = (params) => {
  return request(aiPath + '/delete', {
    serviceName: 'KnowledgeBaseService',
    methodName: 'delete',
    version: '1.0',
    bizParams: {
      ...params,
    },
  })
}

//添加知识库操作
export const addKnowledgeBase = (params) => {
  return request(aiPath + '/add', {
    serviceName: 'KnowledgeBaseService',
    methodName: 'add',
    version: '1.0',
    bizParams: {
      ...params,
    },
  })
}

//知识库详情
export const getKnowledgeBaseDetail = (params) => {
  return request(aiPath + '/getDetail', {
    serviceName: 'KnowledgeBaseService',
    methodName: 'getDetail',
    version: '1.0',
    bizParams: {
      ...params,
    },
  })
}

//编辑知识库操作
export const editKnowledgeBase = (params) => {
  return request(aiPath + '/edit', {
    serviceName: 'KnowledgeBaseService',
    methodName: 'edit',
    version: '1.0',
    bizParams: {
      ...params,
    },
  })
}

// 获取知识库文件
export const getKnowledgeBaseFileList = (params) => {
  return request(aiPath + '/getList', {
    serviceName: 'KnowledgeBaseFileService',
    methodName: 'getList',
    version: '1.0',
    bizParams: {
      ...params,
    },
  })
}

// 获取知识库文件
export const getKnowledgeBaseFilePageList = (params) => {
  return request(aiPath + '/getPageList', {
    serviceName: 'KnowledgeBaseService',
    methodName: 'getPageList',
    version: '1.0',
    bizParams: {
      ...params,
    },
  })
}

// 知识库对话
export const chatWithKnowledgeBase = (params) => {
  return request(aiPath + '/chatWithKnowledgeBase', {
    serviceName: 'ChatService',
    methodName: 'chatWithKnowledgeBase',
    version: '1.0',
    bizParams: {
      ...params,
    },
  })
}