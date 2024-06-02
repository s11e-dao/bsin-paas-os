import { request } from '@umijs/max'
let waasPath = process.env.contextPath_waas;

// 分页查询转账
export const getTransferJournalPageList = (params) => {
  return request(waasPath + '/transferJournal/getPageList', {
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
  return request(waasPath + '/transferJournal/getDetail', {
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
  return request(waasPath + '/transferJournal/add', {
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
  return request(waasPath + '/transferJournal/delete', {
    serviceName: 'TransferJournalService',
    methodName: 'delete',
    version: '1.0',
    bizParams: {
      ...params,
    },
  });
};
