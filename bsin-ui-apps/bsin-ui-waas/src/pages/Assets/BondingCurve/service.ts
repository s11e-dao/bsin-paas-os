import { request } from '@umijs/max'
let crmPath = process.env.contextPath_crm;
let waasPath = process.env.contextPath_waas;

// 分页查询曲线配置
export const getCurvePageList = (params) => {
  return request(waasPath + '/bondingCurveToken/getPageList', {
    serviceName: 'BondingCurveTokenService',
    methodName: 'getPageList',
    version: '1.0',
    bizParams: {
      ...params,
    },
  });
};

// 查询曲线配置
export const getCurveList = (params) => {
  return request(waasPath + '/bondingCurveToken/getList', {
    serviceName: 'BondingCurveTokenService',
    methodName: 'getList',
    version: '1.0',
    bizParams: {
      ...params,
    },
  });
};

// 创建曲线配置
export const addCurve = (params) => {
  return request(waasPath + '/bondingCurveToken/add', {
    serviceName: 'BondingCurveTokenService',
    methodName: 'add',
    version: '1.0',
    bizParams: {
      ...params,
    },
  });
};

// 创建曲线配置
export const editCurve = (params) => {
  return request(waasPath + '/bondingCurveToken/edit', {
    serviceName: 'BondingCurveTokenService',
    methodName: 'edit',
    version: '1.0',
    bizParams: {
      ...params,
    },
  });
};

// 删除曲线配置
export const deleteCurve = (params) => {
  return request(waasPath + '/bondingCurveToken/delete', {
    serviceName: 'BondingCurveTokenService',
    methodName: 'delete',
    version: '1.0',
    bizParams: {
      ...params,
    },
  });
};

// 查询曲线配置详情
export const getCurveDetail = (params) => {
  console.log('params', params);
  return request(waasPath + '/bondingCurveToken/getDetail', {
    serviceName: 'BondingCurveTokenService',
    methodName: 'getDetail',
    version: '1.0',
    bizParams: {
      ...params,
    },
  });
};

// 查询积分铸造明细
export const getBondingCurveTokenJournalPageList = (params) => {
  console.log('params', params);
  return request(waasPath + '/bondingCurveToken/getJournalPageList', {
    serviceName: 'BondingCurveTokenService',
    methodName: 'getJournalPageList',
    version: '1.0',
    bizParams: {
      ...params,
    },
  });
};

// 查询积分曲线点
export const getBondingCurveTokenTrendList = (params) => {
  console.log('params', params);
  return request(waasPath + '/bondingCurveToken/getTrendList', {
    serviceName: 'BondingCurveTokenService',
    methodName: 'getTrendList',
    version: '1.0',
    bizParams: {
      ...params,
    },
  });
};

export const getBondingCurveTokenJournalList = (params) => {
  console.log('params', params);
  return request(waasPath + '/bondingCurveToken/getJournalList', {
    serviceName: 'BondingCurveTokenService',
    methodName: 'getJournalList',
    version: '1.0',
    bizParams: {
      ...params,
    },
  });
};

// 查询铸造/销毁详情
export const getTransactionDetail = (params) => {
  console.log('params', params);
  return request(waasPath + '/bondingCurveToken/getTransactionDetail', {
    serviceName: 'BondingCurveTokenService',
    methodName: 'getTransactionDetail',
    version: '1.0',
    bizParams: {
      ...params,
    },
  });
};

// 铸造/销毁
export const addTransaction = (params) => {
  console.log('params', params);
  if (params.method == 'mint') {
    return request(waasPath + '/bondingCurveToken/mint', {
      serviceName: 'BondingCurveTokenService',
      methodName: 'mint',
      version: '1.0',
      bizParams: {
        ...params,
      },
    });
  } else if (params.method == 'redeem') {
    return request(waasPath + '/bondingCurveToken/redeem', {
      serviceName: 'BondingCurveTokenService',
      methodName: 'redeem',
      version: '1.0',
      bizParams: {
        ...params,
      },
    });
  }
};
