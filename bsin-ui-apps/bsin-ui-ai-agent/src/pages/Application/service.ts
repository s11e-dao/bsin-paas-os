import { request } from '@umijs/max';
let crmPath = process.env.contextPath_crm;
const aiAgentPath = process.env.contextPath_aiAgent;


// 列表数据请求
export const getLLMPageList = (params) => {
  return request(aiAgentPath + '/llm/getPageList', {
    serviceName: 'LLMService',
    methodName: 'getPageList',
    version: '1.0',
    bizParams: {
      ...params,
    },
  });
};

// 删除
export const delLLMInfo = (params) => {
  return request(aiAgentPath + '/llm/delete', {
    serviceName: 'LLMService',
    methodName: 'delete',
    version: '1.0',
    bizParams: {
      ...params,
    },
  });
};

//添加
export const addLLMInfo = (params) => {
  return request(aiAgentPath + '/llm/add', {
    serviceName: 'LLMService',
    methodName: 'add',
    version: '1.0',
    bizParams: {
      ...params,
    },
  });
};

//编辑
export const editLLMInfo = (params) => {
  return request(aiAgentPath + '/llm/edit', {
    serviceName: 'LLMService',
    methodName: 'edit',
    version: '1.0',
    bizParams: {
      ...params,
    },
  });
};



// 分页查询
export const getBizRoleAppPageList = (params) => {
  return request(crmPath + '/bizRoleApp/getPageList', {
    serviceName: 'MerchantAppService',
    methodName: 'getPageList',
    version: '1.0',
    bizParams: {
      ...params,
    },
  });
};

// 创建
export const addBizRoleApp = (params) => {
  return request(crmPath + '/bizRoleApp/add', {
    serviceName: 'MerchantAppService',
    methodName: 'add',
    version: '1.0',
    bizParams: {
      ...params,
    },
  });
};

// 编辑
export const editBizRoleApp = (params) => {
  return request(crmPath + '/bizRoleApp/edit', {
    serviceName: 'BizRoleAppService',
    methodName: 'edit',
    version: '1.0',
    bizParams: {
      ...params,
    },
  });
};

// 删除
export const deleteBizRoleApp = (params) => {
  return request(crmPath + '/bizRoleApp/delete', {
    serviceName: 'BizRoleAppService',
    methodName: 'delete',
    version: '1.0',
    bizParams: {
      ...params,
    },
  });
};

// 查询详情
export const getBizRoleAppDetail = (params) => {
  console.log('params', params);
  return request(crmPath + '/bizRoleApp/getDetail', {
    serviceName: 'BizRoleAppService',
    methodName: 'getDetail',
    version: '1.0',
    bizParams: {
      ...params,
    },
  });
};

// 个人微信登录/退出
export const wechatAgentLogin = (params: any) => {
  return request(crmPath + '/bizRoleApp/wechatAgentLogin', {
    serviceName: 'BizRoleAppService',
    methodName: 'wechatAgentLogin',
    version: '1.0',
    bizParams: {
      ...params,
    },
  });
};


export const getAgentPagetList = (params: any) => {
  return request(aiAgentPath + '/aiAgent/getPageList', {
    serviceName: 'AgentService',
    methodName: 'getPageList',
    version: '1.0',
    bizParams: {
      ...params,
    },
  });
};

export const getRuleSetList = (params: any) => {
  return request(aiAgentPath + '/aiAgent/page', {
    serviceName: 'AgentService',
    methodName: 'page',
    version: '1.0',
    bizParams: {
      ...params,
    },
  });
};


export const deleteAgent = (params: any) => {
  return request(aiAgentPath + '/aiAgent/delete', {
    serviceName: 'AgentService',
    methodName: 'delete',
    version: '1.0',
    bizParams: {
      ...params,
    },
  });
};

export const editAgent = (params: any) => {
  return request(aiAgentPath + '/aiAgent/create', {
    serviceName: 'AgentService',
    methodName: 'create',
    version: '1.0',
    bizParams: {
      ...params,
    },
  });
};
