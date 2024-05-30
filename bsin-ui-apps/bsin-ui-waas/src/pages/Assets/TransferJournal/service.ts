import { request } from '@umijs/max'

// 分页查询合约配置
export const getTransferJournalPageList = (params) => {
  return request('/list', {
    serviceName: 'TransferJournalService',
    methodName: 'getPageList',
    version: '1.0',
    bizParams: {
      ...params,
    },
  });
};

// 创建合约配置
export const addTransferJournal = (params) => {
  return request('/add', {
    serviceName: 'TransferJournalService',
    methodName: 'add',
    version: '1.0',
    bizParams: {
      ...params,
    },
  });
};

// 删除合约配置
export const deleteTransferJournal = (params) => {
  return request('/del', {
    serviceName: 'TransferJournalService',
    methodName: 'delete',
    version: '1.0',
    bizParams: {
      ...params,
    },
  });
};

// 查询合约配置详情
export const getTransferJournalDetail = (params) => {
  console.log('params', params);
  return request('/view', {
    serviceName: 'TransferJournalService',
    methodName: 'getDetail',
    version: '1.0',
    bizParams: {
      ...params,
    },
  });
};
