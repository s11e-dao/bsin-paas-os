import { request } from '@umijs/max'
let upmsPath = process.env.contextPath_upms;

// 获取字典列表
export const getPageList = (params: any) => {
  return request(upmsPath + '/dict/getPageList', {
    serviceName: 'DictService',
    methodName: 'getPageList',
    bizParams: {
      ...params,
    },
  });
};

// 新增字典
export const addPageList = (params: any) => {
  return request(upmsPath + '/dict/add', {
    serviceName: 'DictService',
    methodName: 'add',
    bizParams: {
      ...params,
    },
  });
};

// 编辑字典
export const editPageList = (params: any) => {
  return request(upmsPath + '/dict/edit', {
    serviceName: 'DictService',
    methodName: 'edit',
    bizParams: {
      ...params,
    },
  });
};

// 删除字典
export const deletePageList = (params: any) => {
  return request(upmsPath + '/dict/delete', {
    serviceName: 'DictService',
    methodName: 'delete',
    bizParams: {
      ...params,
    },
  });
};

// 字典项查询
export const getDictItemPageList = (params: any) => {
  return request(upmsPath + '/dict/getDictItemPageList', {
    serviceName: 'DictService',
    methodName: 'getDictItemPageList',
    bizParams: {
      ...params,
    },
  });
};

// 字典项新增
export const addDictItemPageList = (params: any) => {
  return request(upmsPath + '/dict/addItem', {
    serviceName: 'DictService',
    methodName: 'addItem',
    bizParams: {
      ...params,
    },
  });
};

// 字典项编辑
export const editDictItemPageList = (params: any) => {
  return request(upmsPath + '/dict/editItem', {
    serviceName: 'DictService',
    methodName: 'editItem',
    bizParams: {
      ...params,
    },
  });
};

// 字典项删除
export const deleteDictItemPageList = (params: any) => {
  return request(upmsPath + '/dict/deleteItem', {
    serviceName: 'DictService',
    methodName: 'deleteItem',
    bizParams: {
      ...params,
    },
  });
};
