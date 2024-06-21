import { request } from '@umijs/max'
let aiAgent = process.env.contextPath_aiAgent;

// 列表数据请求
export const getPromptTemplatePageList = (params) => {
  return request(aiAgent + '/promptTemplate/getPageList', {
    serviceName: 'PromptTemplateService',
    methodName: 'getPageList',
    version: '1.0',
    bizParams: {
      ...params,
    },
  })
}

// 列表数据请求
export const getPromptTemplateList = (params) => {
  return request(aiAgent + '/promptTemplate/getList', {
    serviceName: 'PromptTemplateService',
    methodName: 'getList',
    version: '1.0',
    bizParams: {
      ...params,
    },
  })
}

// 删除操作
export const delPromptTemplate = (params) => {
  return request(aiAgent + '/promptTemplate/delete', {
    serviceName: 'PromptTemplateService',
    methodName: 'delete',
    version: '1.0',
    bizParams: {
      ...params,
    },
  })
}

//添加操作
export const addPromptTemplate = (params) => {
  return request(aiAgent + '/promptTemplate/add', {
    serviceName: 'PromptTemplateService',
    methodName: 'add',
    version: '1.0',
    bizParams: {
      ...params,
    },
  })
}

//编辑操作
export const editPromptTemplate = (params) => {
  return request(aiAgent + '/promptTemplate/edit', {
    serviceName: 'PromptTemplateService',
    methodName: 'edit',
    version: '1.0',
    bizParams: {
      ...params,
    },
  })
}

//详情
export const getPromptTemplateDetail = (params) => {
  return request(aiAgent + '/promptTemplate/getDetail', {
    serviceName: 'PromptTemplateService',
    methodName: 'getDetail',
    version: '1.0',
    bizParams: {
      ...params,
    },
  })
}

// 菜单数据请求
export const getMenuList = (params) => {
  return request(aiAgent + '/menu/findMenuTree', {
    serviceName: 'MenuService',
    methodName: 'findMenuTree',
    version: '1.0',
    bizParams: {
      ...params,
    },
  })
}

// 已有菜单数据请求
export const getMenusByAppIdAndPromptTemplateId = (params) => {
  return request(aiAgent + '/menu/getMenusByAppIdAndPromptTemplateId', {
    serviceName: 'MenuService',
    methodName: 'getMenusByAppIdAndPromptTemplateId',
    version: '1.0',
    bizParams: {
      ...params,
    },
  })
}

// 授权
export const empowerMenu = (params) => {
  return request(aiAgent + '/promptTemplate/authorizeMenus', {
    serviceName: 'PromptTemplateService',
    methodName: 'authorizeMenus',
    version: '1.0',
    bizParams: {
      ...params,
    },
  })
}
