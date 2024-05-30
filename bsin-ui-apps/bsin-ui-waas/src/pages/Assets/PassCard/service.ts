import { request } from '@umijs/max'

// 发行会员卡
export const collectPassCard = (params) => {
  console.log('params', params);
  return request('/collectPassCard', {
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
  return request('/openPassCard', {
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
  return request('/openPassCard', {
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
  return request('/view', {
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
  return request('/getDigitalPassCardTrandingPageList', {
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
  return request('/getDigitalPassCardTrandingPageList', {
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
  return request('/getDigitalPassCardTrandingPageList', {
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
  return request('/getDigitalPassCardTrandingPageList', {
    serviceName: 'MintJournalService',
    methodName: 'getDetail',
    version: '1.0',
    bizParams: {
      ...params,
    },
  });
};
