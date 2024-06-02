import { request } from '@umijs/max'
let waasPath = process.env.contextPath_waas;

// 发行会员卡
export const collectPassCard = (params) => {
  console.log('params', params);
  return request(waasPath + '/customerProfile/collect', {
    serviceName: 'CustomerProfileService',
    methodName: 'collect',
    version: '1.0',
    bizParams: {
      ...params,
    },
  });
};

// 开卡
export const openPassCard = (params) => {
  console.log('params', params);
  return request(waasPath + '/customerProfile/claim', {
    serviceName: 'CustomerPassCardService',
    methodName: 'claim',
    version: '1.0',
    bizParams: {
      ...params,
    },
  });
};

// 添加follow 记录
export const openPassCardByProfileFollow = (params) => {
  console.log('params', params);
  return request(waasPath + '/customerProfile/follow', {
    serviceName: 'CustomerProfileService',
    methodName: 'follow',
    version: '1.0',
    bizParams: {
      ...params,
    },
  });
};

// 查询详情
export const getDigitalPassCardDetail = (params) => {
  console.log('params', params);
  return request(waasPath + '/digitalAssetsCollection/getDetail', {
    serviceName: 'DigitalAssetsCollectionService',
    methodName: 'getDetail',
    version: '1.0',
    bizParams: {
      ...params,
    },
  });
};

// 查询数字会员卡开卡记录pageList
export const getDigitalPassCardOpenPageList = (params) => {
  return request(waasPath + '/mintJournal/getPageList', {
    serviceName: 'MintJournalService',
    methodName: 'getPageList',
    version: '1.0',
    bizParams: {
      ...params,
    },
  });
};

// 查询数字会员卡开卡detail
export const getDigitalPassCardOpenDetail = (params) => {
  return request(waasPath + '/mintJournal/getDetail', {
    serviceName: 'MintJournalService',
    methodName: 'getDetail',
    version: '1.0',
    bizParams: {
      ...params,
    },
  });
};

// 查询数字会员卡流转pageList
export const getDigitalPassCardTransferPageList = (params) => {
  return request(waasPath + '/transferJournal/getPageList', {
    serviceName: 'TransferJournalService',
    methodName: 'getPageList',
    version: '1.0',
    bizParams: {
      ...params,
    },
  });
};

// 查询数字会员卡流转记录detail
export const getDigitalPassCardTransferDetail = (params) => {
  return request(waasPath + '/mintJournal/getDetail', {
    serviceName: 'MintJournalService',
    methodName: 'getDetail',
    version: '1.0',
    bizParams: {
      ...params,
    },
  });
};
