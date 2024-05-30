import { request } from '@umijs/max'

// 列表查询
export const getDigitalAssetsCollectionList = (params) => {
  return request('/list', {
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
  return request('/list', {
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
  return request('/list', {
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
  return request('/add', {
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
  return request('/del', {
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
  return request('/view', {
    serviceName: 'MintJournalService',
    methodName: 'getDetail',
    version: '1.0',
    bizParams: {
      ...params,
    },
  });
};
