import { request } from '@umijs/max'
let waasPath = process.env.contextPath_waas;

// 列表数据请求
export const getMetadataFileList = (params) => {
  return request('/getPageList', {
    serviceName: 'MetadataFileService',
    methodName: 'getFileList',
    version: '1.0',
    bizParams: {
      ...params,
    },
  });
};

// 文件夹选择
export const getMetadataPathList = (params) => {
  return request('/getPathPageList', {
    serviceName: 'MetadataFileService',
    methodName: 'getFileList',
    version: '1.0',
    bizParams: {
      ...params,
    },
  });
};

// 创建目录
export const makeDirectory = (params) => {
  return request('/makeDirectory', {
    serviceName: 'MetadataFileService',
    methodName: 'makeDirectory',
    version: '1.0',
    bizParams: {
      ...params,
    },
  });
};

// 上传数据
export const saveFile = (params) => {
  return request('/saveFile', {
    serviceName: 'MetadataFileService',
    methodName: 'uploadFile',
    version: '1.0',
    bizParams: {
      ...params,
    },
  });
};
