import { request } from '@umijs/max'
let crmPath = process.env.contextPath_crm;

// 分页查询转账
export const getTransferJournalPageList = (params) => {
  return request('/getPageList', {
    serviceName: 'TransferJournalService',
    methodName: 'getPageList',
    version: '1.0',
    bizParams: {
      ...params,
    },
  });
};

// 查看转账详情
export const getTransferJournalDetail = (params) => {
  return request('/getDetail', {
    serviceName: 'TransferJournalService',
    methodName: 'getDetail',
    version: '1.0',
    bizParams: {
      ...params,
    },
  });
};

// 添加转让
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

// 删除转让
export const deleteTransferJournal = (params) => {
  return request('/delete', {
    serviceName: 'TransferJournalService',
    methodName: 'delete',
    version: '1.0',
    bizParams: {
      ...params,
    },
  });
};
