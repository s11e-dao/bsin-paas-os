import { request } from '@umijs/max'
let aiPath = process.env.contextPath_aiAgent;

// 列表数据请求
export const getToolPageList = (params) => {
  return request(aiPath + '/tool/getPageList', {
    serviceName: 'ToolService',
    methodName: 'getPageList',
    version: '1.0',
    bizParams: {
      ...params,
    },
  })
}
export const getToolList = (params) => {
  return request(aiPath + '/tool/getList', {
    serviceName: 'ToolService',
    methodName: 'getList',
    version: '1.0',
    bizParams: {
      ...params,
    },
  })
}

// 删除
export const delTool = (params) => {
  return request(aiPath + '/tool/delete', {
    serviceName: 'ToolService',
    methodName: 'delete',
    version: '1.0',
    bizParams: {
      ...params,
    },
  })
}

//添加
export const addTool = (params) => {
  return request(aiPath + '/tool/add', {
    serviceName: 'ToolService',
    methodName: 'add',
    version: '1.0',
    bizParams: {
      ...params,
    },
  })
}

//编辑
export const editTool = (params) => {
  return request(aiPath + '/tool/edit', {
    serviceName: 'ToolService',
    methodName: 'edit',
    version: '1.0',
    bizParams: {
      ...params,
    },
  })
}

// 详情
export const getToolDetail = (params) => {
  return request(aiPath + '/tool/getDetail', {
    serviceName: 'ToolService',
    methodName: 'getDetail',
    version: '1.0',
    bizParams: {
      ...params,
    },
  })
}

// 列表数据请求
export const getWxPlatformMenuPageList = (params) => {
  return request(aiPath + '/wxPlatformMenu/getPageList', {
    serviceName: 'WxPlatformMenuService',
    methodName: 'getPageList',
    version: '1.0',
    bizParams: {
      ...params,
    },
  })
}
export const getWxPlatformMenuList = (params) => {
  return request(aiPath + '/wxPlatformMenu/getList', {
    serviceName: 'WxPlatformMenuService',
    methodName: 'getList',
    version: '1.0',
    bizParams: {
      ...params,
    },
  })
}

export const getWxPlatformMenuTemplateMenuTreeList = (params) => {
  return request(aiPath + '/wxPlatformMenu/getMenuTemplateMenuTreeList', {
    serviceName: 'WxPlatformMenuService',
    methodName: 'getMenuTemplateMenuTreeList',
    version: '1.0',
    bizParams: {
      ...params,
    },
  })
}

export const getWxPlatformMenuTemplate = (params) => {
  return request(aiPath + '/wxPlatformMenu/getMenuTemplateMenuTree', {
    serviceName: 'WxPlatformMenuService',
    methodName: 'getMenuTemplateMenuTree',
    version: '1.0',
    bizParams: {
      ...params,
    },
  })
}

// 删除
export const delWxPlatformMenu = (params) => {
  return request(aiPath + '/wxPlatformMenu/delete', {
    serviceName: 'WxPlatformMenuService',
    methodName: 'delete',
    version: '1.0',
    bizParams: {
      ...params,
    },
  })
}

//添加
export const addWxPlatformMenu = (params) => {
  return request(aiPath + '/wxPlatformMenu/add', {
    serviceName: 'WxPlatformMenuService',
    methodName: 'add',
    version: '1.0',
    bizParams: {
      ...params,
    },
  })
}

//编辑
export const editWxPlatformMenu = (params) => {
  return request(aiPath + '/wxPlatformMenu/edit', {
    serviceName: 'WxPlatformMenuService',
    methodName: 'edit',
    version: '1.0',
    bizParams: {
      ...params,
    },
  })
}

// 详情
export const getWxPlatformMenuDetail = (params) => {
  return request(aiPath + '/wxPlatformMenu/getDetail', {
    serviceName: 'WxPlatformMenuService',
    methodName: 'getDetail',
    version: '1.0',
    bizParams: {
      ...params,
    },
  })
}

// 列表数据请求
export const getWxPlatformPageList = (params) => {
  return request(aiPath + '/wxPlatform/getPageList', {
    serviceName: 'WxPlatformService',
    methodName: 'getPageList',
    version: '1.0',
    bizParams: {
      ...params,
    },
  })
}
export const getWxPlatformList = (params) => {
  return request(aiPath + '/wxPlatform/getList', {
    serviceName: 'WxPlatformService',
    methodName: 'getList',
    version: '1.0',
    bizParams: {
      ...params,
    },
  })
}

// 删除
export const delWxPlatform = (params) => {
  return request(aiPath + '/wxPlatform/delete', {
    serviceName: 'WxPlatformService',
    methodName: 'delete',
    version: '1.0',
    bizParams: {
      ...params,
    },
  })
}

//添加
export const addWxPlatform = (params) => {
  return request(aiPath + '/wxPlatform/add', {
    serviceName: 'WxPlatformService',
    methodName: 'add',
    version: '1.0',
    bizParams: {
      ...params,
    },
  })
}

//编辑
export const editWxPlatform = (params) => {
  return request(aiPath + '/wxPlatform/edit', {
    serviceName: 'WxPlatformService',
    methodName: 'edit',
    version: '1.0',
    bizParams: {
      ...params,
    },
  })
}

// 详情
export const getWxPlatformDetail = (params) => {
  return request(aiPath + '/wxPlatform/getDetail', {
    serviceName: 'WxPlatformService',
    methodName: 'getDetail',
    version: '1.0',
    bizParams: {
      ...params,
    },
  })
}

// 详情
export const startPlatform = (params) => {
  return request(aiPath + '/wxPlatform/loginIn', {
    serviceName: 'WxPlatformService',
    methodName: 'loginIn',
    version: '1.0',
    bizParams: {
      ...params,
    },
  })
}

// 同步公众号菜单
export const syncMpMenu = (params) => {
  return request(aiPath + '/wxPlatformMenu/syncMpMenu', {
    serviceName: 'WxPlatformMenuService',
    methodName: 'syncMpMenu',
    version: '1.0',
    bizParams: {
      ...params,
    },
  })
}

// 移除公众号菜单
export const removeMpMenu = (params) => {
  return request(aiPath + '/wxPlatformMenu/removeMpMenu', {
    serviceName: 'WxPlatformMenuService',
    methodName: 'removeMpMenu',
    version: '1.0',
    bizParams: {
      ...params,
    },
  })
}
