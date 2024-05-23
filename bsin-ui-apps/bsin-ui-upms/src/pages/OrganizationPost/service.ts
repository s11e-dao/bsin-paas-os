import { request } from '@umijs/max'
let upmsPath = process.env.contextPath_upms;

// post列表数据请求,查询全部
export const getPostList = (params) => {
  return request(upmsPath + '/post/getPostListByTenantId', {
    serviceName: 'PostService',
    methodName: 'getPostListByTenantId',
    bizParams: {
      ...params,
    },
  });
};

// 根据机构id查询岗位,已分配，无分页
export const getAssignedPageListByOrgId = (params) => {
  return request(upmsPath + '/post/getPostListByOrgId', {
    serviceName: 'PostService',
    methodName: 'getPostListByOrgId',
    bizParams: {
      ...params,
    },
  });
};

// 根据机构id查询岗位有分页
export const getPageListByOrgId = (params) => {
  return request(upmsPath + '/post/getPageListByOrgId', {
    serviceName: 'PostService',
    methodName: 'getPageListByOrgId',
    bizParams: {
      ...params,
    },
  });
};

// 分配岗位
export const assignPost = (params) => {
  return request(upmsPath + '/org/assignPost', {
    serviceName: 'OrgService',
    methodName: 'assignPost',
    bizParams: {
      ...params,
    },
  });
};

// 移除岗位
export const unbindPost = (params) => {
  return request(upmsPath + '/org/unbindPost', {
    serviceName: 'OrgService',
    methodName: 'unbindPost',
    bizParams: {
      ...params,
    },
  });
};
