import { request } from '@umijs/max';

const waasPath = process.env.contextPath_waas;

// 分页查询参数类型
interface PageListParams {
  current?: number;
  pageSize?: number;
  [key: string]: any;
}

// 交易详情参数类型
interface DetailParams {
  serialNo: string;
}

// 添加交易参数类型
interface AddTransactionParams {
  [key: string]: any;
}

// 删除交易参数类型
interface DeleteTransactionParams {
  serialNo: string;
}

// 产品列表参数类型
interface ProductListParams {
  [key: string]: any;
}

/**
 * 分页查询交易列表
 * @param params 查询参数
 * @returns 分页数据
 */
export const getTransactionPageList = (params: PageListParams) => {
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
 * 添加交易
 * @param params 交易参数
 * @returns 添加结果
 */
export const addTransaction = (params: AddTransactionParams) => {
  return request(waasPath + '/transaction/openTenant', {
    serviceName: 'TransactionService',
    methodName: 'openTenant',
    version: '1.0',
    bizParams: {
      ...params,
    },
  });
};

/**
 * 删除交易
 * @param params 删除参数
 * @returns 删除结果
 */
export const deleteTransaction = (params: DeleteTransactionParams) => {
  return request(waasPath + '/transaction/delete', {
    serviceName: 'TransactionService',
    methodName: 'delete',
    version: '1.0',
    bizParams: {
      ...params,
    },
  });
};

/**
 * 查询交易详情
 * @param params 查询参数
 * @returns 交易详情
 */
export const getTransactionDetail = (params: DetailParams) => {
  return request(waasPath + '/transaction/getDetail', {
    serviceName: 'TransactionService',
    methodName: 'getDetail',
    version: '1.0',
    bizParams: {
      ...params,
    },
  });
};

/**
 * 查询产品列表
 * @param params 查询参数
 * @returns 产品列表
 */
export const getProductList = (params: ProductListParams) => {
  return request(waasPath + '/transaction/getList', {
    serviceName: 'TransactionService',
    methodName: 'getList',
    version: '1.0',
    bizParams: {
      ...params,
    },
  });
};

