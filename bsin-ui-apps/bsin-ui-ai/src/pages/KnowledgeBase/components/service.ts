import { request } from '@umijs/max'

// 列表数据请求
export const getKnowledgeBaseFileList = (params) => {
  return request('/list', {
    serviceName: 'KnowledgeBaseFileService',
    methodName: 'getList',
    version: '1.0',
    bizParams: {
      ...params,
    },
  })
}

// 列表数据请求
export const getKnowledgeBaseFilePageList = (params) => {
  return request('/list', {
    serviceName: 'KnowledgeBaseFileService',
    methodName: 'getPageList',
    version: '1.0',
    bizParams: {
      ...params,
    },
  })
}

// 删除知识库操作
export const delKnowledgeBaseFile = (params) => {
  return request('/delete', {
    serviceName: 'KnowledgeBaseFileService',
    methodName: 'delete',
    version: '1.0',
    bizParams: {
      ...params,
    },
  })
}

//添加知识库操作
export const addKnowledgeBaseFile = (params) => {
  return request('/add', {
    serviceName: 'KnowledgeBaseFileService',
    methodName: 'add',
    version: '1.0',
    bizParams: {
      ...params,
    },
  })
}

//知识库详情
export const getKnowledgeBaseFileDetail = (params) => {
  return request('/detail', {
    serviceName: 'KnowledgeBaseFileService',
    methodName: 'getDetail',
    version: '1.0',
    bizParams: {
      ...params,
    },
  })
}

//编辑知识库操作
export const editKnowledgeBaseFile = (params) => {
  return request('/edit', {
    serviceName: 'KnowledgeBaseFileService',
    methodName: 'edit',
    version: '1.0',
    bizParams: {
      ...params,
    },
  })
}

// 知识库召回
export const knowledgeBaseRetrieval = (params) => {
  return request('/retrieval', {
    serviceName: 'KnowledgeBaseService',
    methodName: 'retrieval',
    version: '1.0',
    bizParams: {
      ...params,
    },
  })
}

// 列表数据请求
export const getKnowledgeBaseFileChunkList = (params) => {
  return request('/list', {
    serviceName: 'KnowledgeBaseFileChunkService',
    methodName: 'getList',
    version: '1.0',
    bizParams: {
      ...params,
    },
  })
}

// 列表数据请求
export const getKnowledgeBaseFileChunkPageList = (params) => {
  return request('/list', {
    serviceName: 'KnowledgeBaseFileChunkService',
    methodName: 'getPageList',
    version: '1.0',
    bizParams: {
      ...params,
    },
  })
}

// 删除知识库操作
export const delKnowledgeBaseFileChunk = (params) => {
  return request('/delete', {
    serviceName: 'KnowledgeBaseFileChunkService',
    methodName: 'delete',
    version: '1.0',
    bizParams: {
      ...params,
    },
  })
}

//添加知识库操作
export const addKnowledgeBaseFileChunk = (params) => {
  return request('/add', {
    serviceName: 'KnowledgeBaseFileChunkService',
    methodName: 'add',
    version: '1.0',
    bizParams: {
      ...params,
    },
  })
}

//知识库详情
export const getKnowledgeBaseFileChunkDetail = (params) => {
  return request('/detail', {
    serviceName: 'KnowledgeBaseFileChunkService',
    methodName: 'getDetail',
    version: '1.0',
    bizParams: {
      ...params,
    },
  })
}

//编辑知识库操作
export const editKnowledgeBaseFileChunk = (params) => {
  return request('/edit', {
    serviceName: 'KnowledgeBaseFileChunkService',
    methodName: 'edit',
    version: '1.0',
    bizParams: {
      ...params,
    },
  })
}
