import { request } from '@umijs/max'
let waasPath = process.env.contextPath_waas;
// const [tradingMode, setTradingMode] = useState('');

// 发行
export const issueDigitalPoints = (params) => {
  return request(waasPath + '/digitalPoints/issue', {
    serviceName: 'DigitalPointsService',
    methodName: 'issue',
    version: '1.0',
    bizParams: {
      ...params,
    },
  });
};

// 查询详情
export const getDigitalPointsDetail = (params) => {
  console.log('params', params);
  return request(waasPath + '/digitalPoints/getDetail', {
    serviceName: 'DigitalAssetsCollectionService',
    methodName: 'getDetail',
    version: '1.0',
    bizParams: {
      ...params,
    },
  });
};

// 获取数字积分参数
export const getTokenParam = (params) => {
  console.log('params', params);
  return request(waasPath + '/tokenParam/getDetail', {
    serviceName: 'TokenParamService',
    methodName: 'getDetail',
    version: '1.0',
    bizParams: {
      ...params,
    },
  });
};

// 设置数字积分参数
export const editTokenParam = (params) => {
  console.log('params', params);
  return request(waasPath + '/tokenParam/edit', {
    serviceName: 'TokenParamService',
    methodName: 'edit',
    version: '1.0',
    bizParams: {
      ...params,
    },
  });
};

// 查询数字积分交易记录pageList
export const getDigitalPointsTradingPageList = (params) => {
  params.assetsType = '3';
  console.log('params', params);
  // setTradingMode(params.tradingMode);
  if (
    params.tradingMode == '1' ||
    params.tradingMode == '2' ||
    params.tradingMode == '4'
  ) {
    return request(waasPath + '/mintJournal/getPageList', {
      serviceName: 'MintJournalService',
      methodName: 'getPageList',
      version: '1.0',
      bizParams: {
        ...params,
      },
    });
  } else {
    return request(waasPath + '/transferJournal/getPageList', {
      serviceName: 'TransferJournalService',
      methodName: 'getPageList',
      version: '1.0',
      bizParams: {
        ...params,
      },
    });
  }
};

// 查询数字积分释放pageList
export const getDigitalPointsReleasePageList = (params) => {
  params.assetsType = '3';
  console.log('params', params);
  // setTradingMode(params.tradingMode);
  if (
    params.tradingMode == '1' ||
    params.tradingMode == '2' ||
    params.tradingMode == '4'
  ) {
    return request(waasPath + '/mintJournal/getPageList', {
      serviceName: 'MintJournalService',
      methodName: 'getPageList',
      version: '1.0',
      bizParams: {
        ...params,
      },
    });
  } else {
    return request(waasPath + '/transferJournal/getPageList', {
      serviceName: 'TransferJournalService',
      methodName: 'getPageList',
      version: '1.0',
      bizParams: {
        ...params,
      },
    });
  }
};

// 查询数字积分交易记录detail
export const getDigitalPointsTradingDetail = (params) => {
  params.assetsType = '3';
  console.log('params', params);
  // if (tradingMode == '1' || tradingMode == '2' || tradingMode == '3')
  if (
    params.tradingMode == '1' ||
    params.tradingMode == '2' ||
    params.tradingMode == '3'
  ) {
    return request(waasPath + '/mintJournal/getDetail', {
      serviceName: 'MintJournalService',
      methodName: 'getDetail',
      version: '1.0',
      bizParams: {
        ...params,
      },
    });
  } else {
    return request(waasPath + '/transferJournal/getDetail', {
      serviceName: 'TransferJournalService',
      methodName: 'getDetail',
      version: '1.0',
      bizParams: {
        ...params,
      },
    });
  }
};

// 查询数字积分释放detail
export const getDigitalPointsReleaseDetail = (params) => {
  params.assetsType = '3';
  console.log('params', params);
  // if (tradingMode == '1' || tradingMode == '2' || tradingMode == '3')
  if (
    params.tradingMode == '1' ||
    params.tradingMode == '2' ||
    params.tradingMode == '3'
  ) {
    return request(waasPath + '/mintJournal/getDetail', {
      serviceName: 'MintJournalService',
      methodName: 'getDetail',
      version: '1.0',
      bizParams: {
        ...params,
      },
    });
  } else {
    return request(waasPath + '/transferJournal/getDetail', {
      serviceName: 'TransferJournalService',
      methodName: 'getDetail',
      version: '1.0',
      bizParams: {
        ...params,
      },
    });
  }
};
