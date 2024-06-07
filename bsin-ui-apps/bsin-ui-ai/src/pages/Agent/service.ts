import { request } from '@umijs/max'
let aiAgent = process.env.contextPath_aiAgent;

// 列表数据请求
export const getKnowledgeBaseList = (params) => {
  return request(aiAgent + '/knowledgeBase/getList', {
    serviceName: 'KnowledgeBaseService',
    methodName: 'getList',
    bizParams: {
      ...params,
    },
  })
}

// 列表数据请求
export const getKnowledgeBasePageList = (params) => {
  return request(aiAgent + '/knowledgeBase/getPageList', {
    serviceName: 'KnowledgeBaseService',
    methodName: 'getPageList',
    bizParams: {
      ...params,
    },
  })
}

// 删除知识库操作
export const delKnowledgeBase = (params) => {
  return request(aiAgent + '/knowledgeBase/delete', {
    serviceName: 'KnowledgeBaseService',
    methodName: 'delete',
    bizParams: {
      ...params,
    },
  })
}

//添加知识库操作
export const addKnowledgeBase = (params) => {
  return request(aiAgent + '/knowledgeBase/add', {
    serviceName: 'KnowledgeBaseService',
    methodName: 'add',
    bizParams: {
      ...params,
    },
  })
}

//知识库详情
export const getKnowledgeBaseDetail = (params) => {
  return request(aiPath + '/knowledgeBase/getDetail', {
    serviceName: 'KnowledgeBaseService',
    methodName: 'getDetail',
    bizParams: {
      ...params,
    },
  })
}

//编辑知识库操作
export const editKnowledgeBase = (params) => {
  return request(aiPath + '/knowledgeBase/edit', {
    serviceName: 'KnowledgeBaseService',
    methodName: 'edit',
    bizParams: {
      ...params,
    },
  })
}
