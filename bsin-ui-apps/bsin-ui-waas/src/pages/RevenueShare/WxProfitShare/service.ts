import { request } from '@umijs/max';
import type { PageParams } from '@/types/common';

const waasPath = process.env.contextPath_waas;
const crmPath = process.env.contextPath_crm;

// 平台应用查询参数类型
interface BizRoleAppParams extends PageParams {
  appName?: string;
  appId?: string;
  status?: string;
}

// 支付通道配置参数类型
interface PayChannelConfigParams {
  bizRoleAppId: string;
  payChannelCode: string;
}

// 分账请求参数类型
interface ProfitShareRequestParams {
  transactionNo: string;
}

// 分账回退参数类型
interface ProfitShareReturnParams {
  orderId: string;
  outReturnNo: string;
  returnMchid: string;
  amount: number;
  description: string;
}

// 分账回退查询参数类型
interface ProfitShareReturnQueryParams {
  orderId: string;
  outReturnNo: string;
}

// 解冻资金参数类型
interface UnfreezeParams {
  transactionId: string;
  outOrderNo: string;
  description?: string;
}

// 查询剩余金额参数类型
interface RemainingAmountParams {
  transactionId: string;
}

// 添加接收方参数类型
interface AddReceiverParams {
  receiverId: string;
  receiverName: string;
  receiverType: string;
  relationType: string;
  customRelation?: string;
}

// 删除接收方参数类型
interface DeleteReceiverParams {
  receiverId: string;
}

// 账单参数类型
interface BillParams {
  billDate: string;
  tarType: string;
}

/**
 * 平台应用查询接口
 * @param params 查询参数
 * @returns 平台应用列表
 */
export async function getBizRoleAppList(params: BizRoleAppParams) {
  return request(crmPath + '/bizRoleApp/getPageList', {
    serviceName: 'BizRoleAppService',
    methodName: 'getPageList',
    version: '1.0',
    bizParams: {
      ...params,
    },
  });
}

/**
 * 获取平台应用支付通道配置
 * @param params 配置参数
 * @returns 支付通道配置
 */
export async function getBizRoleAppPayChannelConfig(params: PayChannelConfigParams) {
  return request(waasPath + '/payChannelConfig/getBizRoleAppPayChannelConfig', {
    serviceName: 'PayChannelConfigService',
    methodName: 'getBizRoleAppPayChannelConfig',
    version: '1.0',
    bizParams: {
      ...params,
    },
  });
}

/**
 * 微信分账API接口 - 请求分账
 * @param params 分账参数
 * @returns 分账结果
 */
export async function requestProfitShare(params: ProfitShareRequestParams) {
  return request(waasPath + '/profitShare/request', {
    serviceName: 'ProfitSharingApiService',
    methodName: 'requestProfitShare',
    version: '1.0',
    bizParams: {
      ...params,
    },
  });
}

/**
 * 查询分账结果
 * @param params 查询参数
 * @returns 分账结果
 */
export async function queryProfitShareResult(params: ProfitShareRequestParams) {
  return request(waasPath + '/profitShare/query', {
    serviceName: 'ProfitSharingApiService',
    methodName: 'queryProfitShareResult',
    version: '1.0',
    bizParams: {
      ...params,
    },
  });
}

/**
 * 请求分账回退
 * @param params 回退参数
 * @returns 回退结果
 */
export async function requestProfitShareReturn(params: ProfitShareReturnParams) {
  return request(waasPath + '/profitShare/return', {
    serviceName: 'ProfitSharingApiService',
    methodName: 'requestProfitShareReturn',
    version: '1.0',
    bizParams: {
      ...params,
    },
  });
}

/**
 * 查询分账回退结果
 * @param params 查询参数
 * @returns 回退结果
 */
export async function queryProfitShareReturnResult(params: ProfitShareReturnQueryParams) {
  return request(waasPath + '/profitShare/returnQuery', {
    serviceName: 'ProfitSharingApiService',
    methodName: 'queryProfitShareReturnResult',
    version: '1.0',
    bizParams: {
      ...params,
    },
  });
}

/**
 * 解冻剩余资金
 * @param params 解冻参数
 * @returns 解冻结果
 */
export async function unfreezeRemainingFunds(params: UnfreezeParams) {
  return request(waasPath + '/profitShare/unfreeze', {
    serviceName: 'ProfitSharingApiService',
    methodName: 'unfreezeRemainingFunds',
    version: '1.0',
    bizParams: {
      ...params,
    },
  });
}

/**
 * 查询剩余待分金额
 * @param params 查询参数
 * @returns 剩余金额
 */
export async function queryRemainingAmount(params: RemainingAmountParams) {
  return request(waasPath + '/profitShare/remaining', {
    serviceName: 'ProfitSharingApiService',
    methodName: 'queryRemainingAmount',
    version: '1.0',
    bizParams: {
      ...params,
    },
  });
}

/**
 * 添加分账接收方
 * @param params 接收方参数
 * @returns 添加结果
 */
export async function addProfitShareReceiver(params: AddReceiverParams) {
  return request(waasPath + '/profitShare/addReceiver', {
    serviceName: 'ProfitSharingApiService',
    methodName: 'addProfitShareReceiver',
    version: '1.0',
    bizParams: {
      ...params,
    },
  });
}

/**
 * 删除分账接收方
 * @param params 删除参数
 * @returns 删除结果
 */
export async function deleteProfitShareReceiver(params: DeleteReceiverParams) {
  return request(waasPath + '/profitShare/deleteReceiver', {
    serviceName: 'ProfitSharingApiService',
    methodName: 'deleteProfitShareReceiver',
    version: '1.0',
    bizParams: {
      ...params,
    },
  });
}

/**
 * 申请分账账单
 * @param params 账单参数
 * @returns 申请结果
 */
export async function applyProfitShareBill(params: BillParams) {
  return request(waasPath + '/profitShare/applyBill', {
    serviceName: 'ProfitSharingApiService',
    methodName: 'applyProfitShareBill',
    version: '1.0',
    bizParams: {
      ...params,
    },
  });
}

/**
 * 下载分账账单
 * @param params 账单参数
 * @returns 下载结果
 */
export async function downloadProfitShareBill(params: BillParams) {
  return request(waasPath + '/profitShare/downloadBill', {
    serviceName: 'ProfitSharingApiService',
    methodName: 'downloadProfitShareBill',
    version: '1.0',
    bizParams: {
      ...params,
    },
  });
}
