import { request } from '@umijs/max'
let upmsPath = process.env.contextPath_upms;

// 获取地区树
export const getSubNodeTree = (params: any) => {
  return request(upmsPath + '/region/getSubNodeTree', {
    serviceName: 'RegionService',
    methodName: 'getSubNodeTree',
    bizParams: {
      ...params,
    },
  });
};

// 获取地区树
export const getTopLayerRegions = (params: any) => {
  return request(upmsPath + '/region/getSubNodeTree', {
    serviceName: 'RegionService',
    methodName: 'getTopLayerRegions',
    bizParams: {
      ...params,
    },
  });
};

// 新增地区树
export const addSubNodeTree = (params: any) => {
  return request(upmsPath + '/region/add', {
    serviceName: 'RegionService',
    methodName: 'add',
    bizParams: {
      ...params,
    },
  });
};

// 编辑地区树
export const editSubNodeTree = (params: any) => {
  return request(upmsPath + '/region/edit', {
    serviceName: 'RegionService',
    methodName: 'edit',
    bizParams: {
      ...params,
    },
  });
};

// 删除地区树
export const deleteSubNodeTree = (params: any) => {
  return request(upmsPath + '/region/delete', {
    serviceName: 'RegionService',
    methodName: 'delete',
    bizParams: {
      ...params,
    },
  });
};
