import { request } from '@umijs/max'

// 列表数据请求
export const getMetadataTemplatePageList = (params) => {
  return request('/getPageList', {
    serviceName: 'MetadataTemplateService',
    methodName: 'getPageList',
    version: '1.0',
    bizParams: {
      ...params,
    },
  });
};

// 新增元数据
export const addMetadataTemplate = (params) => {
  return request('/add', {
    serviceName: 'MetadataTemplateService',
    methodName: 'add',
    version: '1.0',
    bizParams: {
      ...params,
    },
  });
};

// 删除元数据
export const deleteMetadataTemplate = (params) => {
  return request('/delete', {
    serviceName: 'MetadataTemplateService',
    methodName: 'delete',
    version: '1.0',
    bizParams: {
      ...params,
    },
  });
};

// 查询模板详情
export const getMetadataTemplateDetail = (params) => {
  console.log('params', params);
  return request('/view', {
    serviceName: 'MetadataTemplateService',
    methodName: 'getDetail',
    version: '1.0',
    bizParams: {
      ...params,
    },
  });
};
