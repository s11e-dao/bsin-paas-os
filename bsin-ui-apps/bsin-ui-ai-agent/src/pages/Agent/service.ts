import { request } from '@umijs/max'
let aiAgent = process.env.contextPath_aiAgent;

// 列表数据请求
export const getKnowledgeBaseList = (params) => {
  return request(aiAgent + '/agent/getList', {
    serviceName: 'AgentService',
    methodName: 'getList',
    bizParams: {
      ...params,
    },
  })
}

// 列表数据请求
export const getKnowledgeBasePageList = (params) => {
  return request(aiAgent + '/agent/getPageList', {
    serviceName: 'AgentService',
    methodName: 'getPageList',
    bizParams: {
      ...params,
    },
  })
}

// 删除知识库操作
export const delKnowledgeBase = (params) => {
  return request(aiAgent + '/agent/delete', {
    serviceName: 'AgentService',
    methodName: 'delete',
    bizParams: {
      ...params,
    },
  })
}

//添加知识库操作
export const addKnowledgeBase = (params) => {
  return request(aiAgent + '/agent/add', {
    serviceName: 'AgentService',
    methodName: 'add',
    bizParams: {
      ...params,
    },
  })
}

//知识库详情
export const getKnowledgeBaseDetail = (params) => {
  return request(aiPath + '/agent/getDetail', {
    serviceName: 'AgentService',
    methodName: 'getDetail',
    bizParams: {
      ...params,
    },
  })
}

//编辑知识库操作
export const editKnowledgeBase = (params) => {
  return request(aiPath + '/agent/edit', {
    serviceName: 'AgentService',
    methodName: 'edit',
    bizParams: {
      ...params,
    },
  })
}
