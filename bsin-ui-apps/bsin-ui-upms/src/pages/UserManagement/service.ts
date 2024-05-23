import { request } from '@umijs/max'
let upmsPath = process.env.contextPath_upms;

// 根据机构id查询岗位
export const getUserInfo = (params) => {
  return request(upmsPath + '/user/getPageList', {
    serviceName: 'UserService',
    methodName: 'getPageList',
    bizParams: {
      ...params,
    },
  });
};

// add添加用户
export const addUserInfo = (params) => {
  return request(upmsPath + '/user/add', {
    serviceName: 'UserService',
    methodName: 'add',
    bizParams: {
      ...params,
    },
  });
};

// edit添加用户
export const editUserInfo = (params) => {
  return request(upmsPath + '/user/edit', {
    serviceName: 'UserService',
    methodName: 'edit',
    bizParams: {
      ...params,
    },
  });
};

// del删除用户
export const delUserInfo = (params) => {
  return request(upmsPath + '/user/delete', {
    serviceName: 'UserService',
    methodName: 'delete',
    bizParams: {
      ...params,
    },
  });
};

// 穿梭框左侧
// 根据用户查询可以分配的岗位
export const getAssignPostListAll = (params) => {
  console.log('可分配参数', params);
  return request(upmsPath + '/post/getAssignablePostByUserId', {
    serviceName: 'PostService',
    methodName: 'getAssignablePostByUserId',
    bizParams: {
      ...params,
    },
  });
};

// 穿梭框右侧
// 根据用户查询已经分配的岗位
export const getAssignedPostByUserId = (params) => {
  console.log('已分配参数', params);
  return request(upmsPath + '/post/getAssignedPostByUserId', {
    serviceName: 'PostService',
    methodName: 'getAssignedPostByUserId',
    bizParams: {
      ...params,
    },
  });
};

// 分配岗位
export const assignPost = (params) => {
  console.log('分配操作', params);
  return request(upmsPath + '/user/assignPost', {
    serviceName: 'UserService',
    methodName: 'assignPost',
    bizParams: {
      ...params,
    },
  });
};
