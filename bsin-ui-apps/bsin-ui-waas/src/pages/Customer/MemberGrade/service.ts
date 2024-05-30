import { request } from '@umijs/max'

// 分页查询客户等级
export const getGradePageList = (params) => {
  return request('/getPageList', {
    serviceName: 'GradeService',
    methodName: 'getPageList',
    version: '1.0',
    bizParams: {
      ...params,
    },
  });
};

// 查询客户等级
export const getGradeList = (params) => {
  return request('/getList', {
    serviceName: 'GradeService',
    methodName: 'getList',
    version: '1.0',
    bizParams: {
      ...params,
    },
  });
};

// 查询等级下所有会员
export const getGradeMemberList = (params) => {
  return request('/getGradeMemberList', {
    serviceName: 'MemberService',
    methodName: 'getGradeMemberList',
    version: '1.0',
    bizParams: {
      ...params,
    },
  });
};

// 分页查询等级下所有会员
export const getGradeMemberPageList = (params) => {
  return request('/getGradeMemberPageList', {
    serviceName: 'MemberService',
    methodName: 'getGradeMemberPageList',
    version: '1.0',
    bizParams: {
      ...params,
    },
  });
};

// 查询等级下的会员列表
export const getGradeAndMemberList = (params) => {
  return request('/getGradeAndMemberList', {
    serviceName: 'GradeService',
    methodName: 'getGradeAndMemberList',
    version: '1.0',
    bizParams: {
      ...params,
    },
  });
};

// 查询等级下所有会员
export const addGradeMember = (params) => {
  return request('/addGradeMember', {
    serviceName: 'MemberService',
    methodName: 'getGradeMemberList',
    version: '1.0',
    bizParams: {
      ...params,
    },
  });
};

// 查询会员详情
export const getGradeMemberDetail = (params) => {
  return request('/getGradeMemberDetail', {
    serviceName: 'MemberService',
    methodName: 'getDetail',
    version: '1.0',
    bizParams: {
      ...params,
    },
  });
};

// 创建会员等级配置
export const addGrade = (params) => {
  return request('/add', {
    serviceName: 'GradeService',
    methodName: 'add',
    version: '1.0',
    bizParams: {
      ...params,
    },
  });
};

// 创建会员等级配置
export const editGrade = (params) => {
  return request('/edit', {
    serviceName: 'GradeService',
    methodName: 'edit',
    version: '1.0',
    bizParams: {
      ...params,
    },
  });
};

// 删除会员等级配置
export const deleteGrade = (params) => {
  return request('/del', {
    serviceName: 'GradeService',
    methodName: 'delete',
    version: '1.0',
    bizParams: {
      ...params,
    },
  });
};

// 查询会员等级配置详情
export const getGradeDetail = (params) => {
  console.log('params', params);
  return request('/view', {
    serviceName: 'GradeService',
    methodName: 'getDetail',
    version: '1.0',
    bizParams: {
      ...params,
    },
  });
};
