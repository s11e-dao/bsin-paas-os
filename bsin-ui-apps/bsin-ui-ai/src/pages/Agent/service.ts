import { request } from '@umijs/max'
let aiPath = process.env.contextPath_ai;

// 列表数据请求
export const getKnowledgeBaseList = (params) => {
  return request(aiPath + '/getList', {
    serviceName: 'KnowledgeBaseService',
    methodName: 'getList',
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
    bizParams: {
      ...params,
    },
  })
}
