import { request } from '@umijs/max'
let crmPath = process.env.contextPath_crm;

// 分页查询曲线配置
export const getCurvePageList = (params) => {
  return request(crmPath + '/bondingCurveToken/getCurvePageList', {
    serviceName: 'BondingCurveTokenService',
    methodName: 'getCurvePageList',
    version: '1.0',
    bizParams: {
      ...params,
    },
  });
};

// 查询曲线配置
export const getCurveList = (params) => {
  return request(crmPath + '/bondingCurveToken/getCurveList', {
    serviceName: 'BondingCurveTokenService',
    methodName: 'getCurveList',
    version: '1.0',
    bizParams: {
      ...params,
    },
  });
};

// 创建曲线配置
export const addCurve = (params) => {
  return request(crmPath + '/bondingCurveToken/addCurve', {
    serviceName: 'BondingCurveTokenService',
    methodName: 'addCurve',
    version: '1.0',
    bizParams: {
      ...params,
    },
  });
};

// 创建曲线配置
export const editCurve = (params) => {
  return request(crmPath + '/bondingCurveToken/editCurve', {
    serviceName: 'BondingCurveTokenService',
    methodName: 'editCurve',
    version: '1.0',
    bizParams: {
      ...params,
    },
  });
};

// 删除曲线配置
export const deleteCurve = (params) => {
  return request(crmPath + '/bondingCurveToken/deleteCurve', {
    serviceName: 'BondingCurveTokenService',
    methodName: 'deleteCurve',
    version: '1.0',
    bizParams: {
      ...params,
    },
  });
};

// 查询曲线配置详情
export const getCurveDetail = (params) => {
  console.log('params', params);
  return request(crmPath + '/bondingCurveToken/getCurveDetail', {
    serviceName: 'BondingCurveTokenService',
    methodName: 'getCurveDetail',
    version: '1.0',
    bizParams: {
      ...params,
    },
  });
};

// 查询积分铸造明细
export const getBondingCurveTokenJournalPageList = (params) => {
  console.log('params', params);
  return request(crmPath + '/bondingCurveToken/getBondingCurveTokenJournalPageList', {
    serviceName: 'BondingCurveTokenService',
    methodName: 'getBondingCurveTokenJournalPageList',
    version: '1.0',
    bizParams: {
      ...params,
    },
  });
};

// 查询积分曲线点
export const getBondingCurveTokenJournalList = (params) => {
  console.log('params', params);
  return request(crmPath + '/bondingCurveToken/getBondingCurveTokenJournalList', {
    serviceName: 'BondingCurveTokenService',
    methodName: 'getBondingCurveTokenJournalList',
    version: '1.0',
    bizParams: {
      ...params,
    },
  });
};

// 查询铸造/销毁详情
export const getTransactionDetail = (params) => {
  console.log('params', params);
  return request(crmPath + '/bondingCurveToken/getTransactionDetail', {
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
    return request(crmPath + '/bondingCurveToken/mint', {
      serviceName: 'BondingCurveTokenService',
      methodName: 'mint',
      version: '1.0',
      bizParams: {
        ...params,
      },
    });
  } else if (params.method == 'redeem') {
    return request(crmPath + '/bondingCurveToken/redeem', {
      serviceName: 'BondingCurveTokenService',
      methodName: 'redeem',
      version: '1.0',
      bizParams: {
        ...params,
      },
    });
  }
};
