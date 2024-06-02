import { request } from '@umijs/max'
let waasPath = process.env.contextPath_waas;

// 列表查询
export const getDigitalAssetsCollectionList = (params) => {
  return request(waasPath + '/digitalAssetsCollection/list', {
    serviceName: 'DigitalAssetsCollectionService',
    methodName: 'getList',
    version: '1.0',
    bizParams: {
      ...params,
    },
  });
};

// 列表查询
export const getDigitalAssetsItemList = (params) => {
  return request(waasPath + '/digitalAssetsCollection/getList', {
    serviceName: 'DigitalAssetsItemService',
    methodName: 'getList',
    version: '1.0',
    bizParams: {
      ...params,
    },
  });
};

// 分页查询铸造记录
export const getMintJournalPageList = (params) => {
  return request(waasPath + '/mintJournal/getPageList', {
    serviceName: 'MintJournalService',
    methodName: 'getPageList',
    version: '1.0',
    bizParams: {
      ...params,
    },
  });
};

// 创建铸造记录
export const addMintJournal = (params) => {
  return request(waasPath + '/mintJournal/add', {
    serviceName: 'MintJournalService',
    methodName: 'add',
    version: '1.0',
    bizParams: {
      ...params,
    },
  });
};

// 删除铸造记录
export const deleteMintJournal = (params) => {
  return request(waasPath + '/mintJournal/delete', {
    serviceName: 'MintJournalService',
    methodName: 'delete',
    version: '1.0',
    bizParams: {
      ...params,
    },
  });
};

// 查询铸造记录详情
export const getMintJournalDetail = (params) => {
  console.log('params', params);
  return request(waasPath + '/mintJournal/getDetail', {
    serviceName: 'MintJournalService',
    methodName: 'getDetail',
    version: '1.0',
    bizParams: {
      ...params,
    },
  });
};
