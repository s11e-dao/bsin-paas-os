import { request } from '@umijs/max'
let aiPath = process.env.contextPath_ai;

// 列表数据请求
export const getPromptTemplatePageList = (params) => {
  return request(aiPath + '/getPageList', {
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
  return request(aiPath + '/getList', {
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
  return request(aiPath + '/delete', {
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
  return request(aiPath + '/add', {
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
  return request(aiPath + '/edit', {
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
  return request(aiPath + '/getDetail', {
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
  return request(aiPath + '/findMenuTree', {
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
  return request(aiPath + '/getMenusByAppIdAndPromptTemplateId', {
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
  return request(aiPath + '/authorizeMenus', {
    serviceName: 'PromptTemplateService',
    methodName: 'authorizeMenus',
    version: '1.0',
    bizParams: {
      ...params,
    },
  })
}
