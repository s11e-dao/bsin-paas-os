import { request } from '@umijs/max'
let waasPath = process.env.contextPath_waas;

// 列表数据请求
export const getMetadataTemplatePageList = (params) => {
  return request(waasPath + '/didProfile/getPageList', {
    serviceName: 'DidProfileServiceEngine',
    methodName: 'getPageList',
    version: '1.0',
    bizParams: {
      ...params,
    },
  });
};

// 新增元数据
export const addMetadataTemplate = (params) => {
  return request(waasPath + '/didProfile/create', {
    serviceName: 'DidProfileServiceEngine',
    methodName: 'add',
    version: '1.0',
    bizParams: {
      ...params,
    },
  });
};

// 删除元数据
export const deleteMetadataTemplate = (params) => {
  return request(waasPath + '/didProfile/delete', {
    serviceName: 'DidProfileServiceEngine',
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
  return request(waasPath + '/didProfile/getDetail', {
    serviceName: 'DidProfileServiceEngine',
    methodName: 'getDetail',
    version: '1.0',
    bizParams: {
      ...params,
    },
  });
};
