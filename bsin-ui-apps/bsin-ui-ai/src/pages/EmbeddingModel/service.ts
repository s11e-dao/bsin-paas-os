import { request } from '@umijs/max'
let aiAgent = process.env.contextPath_aiAgent;

// 列表数据请求
export const getEmbeddingModelList = (params) => {
  return request(aiAgent + '/getList', {
    serviceName: 'EmbeddingModelService',
    methodName: 'getList',
    version: '1.0',
    bizParams: {
      ...params,
    },
  })
}

// 列表数据请求
export const getEmbeddingModelPageList = (params) => {
  return request(aiAgent + '/getPageList', {
    serviceName: 'EmbeddingModelService',
    methodName: 'getPageList',
    version: '1.0',
    bizParams: {
      ...params,
    },
  })
}

// 删除操作
export const delEmbeddingModel = (params) => {
  return request(aiAgent + '/delete', {
    serviceName: 'EmbeddingModelService',
    methodName: 'delete',
    version: '1.0',
    bizParams: {
      ...params,
    },
  })
}

//添加操作
export const addEmbeddingModel = (params) => {
  return request(aiAgent + '/add', {
    serviceName: 'EmbeddingModelService',
    methodName: 'add',
    version: '1.0',
    bizParams: {
      ...params,
    },
  })
}

//详情
export const getEmbeddingModelDetail = (params) => {
  return request(aiAgent + '/getDetail', {
    serviceName: 'EmbeddingModelService',
    methodName: 'getDetail',
    version: '1.0',
    bizParams: {
      ...params,
    },
  })
}

//编辑操作
export const editEmbeddingModel = (params) => {
  return request(aiAgent + '/edit', {
    serviceName: 'EmbeddingModelService',
    methodName: 'edit',
    version: '1.0',
    bizParams: {
      ...params,
    },
  })
}
