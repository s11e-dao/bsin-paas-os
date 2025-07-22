import { request } from '@umijs/max';

const waasPath = process.env.contextPath_waas;

// 分页查询参数类型
interface PageListParams {
  current?: number;
  pageSize?: number;
  [key: string]: any;
}

// 详情查询参数类型
interface DetailParams {
  serialNo: string;
}

/**
 * 分页查询分账明细
 * @param params 查询参数
 * @returns 分页数据
 */
export const getRevenueSharePageList = (params: PageListParams) => {
  return request(waasPath + '/transaction/getPageList', {
    serviceName: 'TransactionService',
    methodName: 'getPageList',
    version: '1.0',
    bizParams: {
      ...params,
    },
  });
};

/**
 * 查询分账明细详情
 * @param params 查询参数
 * @returns 详情数据
 */
export const getRevenueShareDetail = (params: DetailParams) => {
  return request(waasPath + '/transaction/getDetail', {
    serviceName: 'TransactionService',
    methodName: 'getDetail',
    version: '1.0',
    bizParams: {
      ...params,
    },
  });
};
