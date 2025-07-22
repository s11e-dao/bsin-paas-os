import { request } from '@umijs/max';

let waasPath = process.env.contextPath_waas;
let crmPath = process.env.contextPath_crm;

// 平台应用查询接口
export async function getBizRoleAppList(params: any) {
  return request(crmPath + '/bizRoleApp/getPageList', {
    serviceName: 'BizRoleAppService',
    methodName: 'getPageList',
    version: '1.0',
    bizParams: {
      ...params,
    },
  });
}

// 获取平台应用支付通道配置
export async function getBizRoleAppPayChannelConfig(params: {
  bizRoleAppId: string;
  payChannelCode: string;
}) {
  return request(waasPath + '/payChannelConfig/getBizRoleAppPayChannelConfig', {
    serviceName: 'PayChannelConfigService',
    methodName: 'getBizRoleAppPayChannelConfig',
    version: '1.0',
    bizParams: {
      ...params,
    },
  });
}

// 微信分账API接口 - 请求分账
export async function requestProfitShare(params: { transactionNo: string }) {
  return request(waasPath + '/profitShare/request', {
    serviceName: 'ProfitSharingApiService',
    methodName: 'requestProfitShare',
    version: '1.0',
    bizParams: {
      ...params,
    },
  });
}

// 查询分账结果
export async function queryProfitShareResult(params: { transactionNo: string }) {
  return request(waasPath + '/profitShare/query', {
    serviceName: 'ProfitSharingApiService',
    methodName: 'queryProfitShareResult',
    version: '1.0',
    bizParams: {
      ...params,
    },
  });
}

// 请求分账回退
export async function requestProfitShareReturn(params: {
  orderId: string;
  outReturnNo: string;
  returnMchid: string;
  amount: number;
  description: string;
}) {
  return request(waasPath + '/profitShare/return', {
    serviceName: 'ProfitSharingApiService',
    methodName: 'requestProfitShareReturn',
    version: '1.0',
    bizParams: {
      ...params,
    },
  });
}

// 查询分账回退结果
export async function queryProfitShareReturnResult(params: {
  orderId: string;
  outReturnNo: string;
}) {
  return request(waasPath + '/profitShare/returnQuery', {
    serviceName: 'ProfitSharingApiService',
    methodName: 'queryProfitShareReturnResult',
    version: '1.0',
    bizParams: {
      ...params,
    },
  });
}

// 解冻剩余资金
export async function unfreezeRemainingFunds(params: {
  transactionId: string;
  outOrderNo: string;
  description?: string;
}) {
  return request(waasPath + '/profitShare/unfreeze', {
    serviceName: 'ProfitSharingApiService',
    methodName: 'unfreezeRemainingFunds',
    version: '1.0',
    bizParams: {
      ...params,
    },
  });
}

// 查询剩余待分金额
export async function queryRemainingAmount(params: { transactionId: string }) {
  return request(waasPath + '/profitShare/remaining', {
    serviceName: 'ProfitSharingApiService',
    methodName: 'queryRemainingAmount',
    version: '1.0',
    bizParams: {
      ...params,
    },
  });
}

// 添加分账接收方
export async function addProfitShareReceiver(params: {
  receiverId: string;
  receiverName: string;
  receiverType: string;
  relationType: string;
  customRelation?: string;
}) {
  return request(waasPath + '/profitShare/addReceiver', {
    serviceName: 'ProfitSharingApiService',
    methodName: 'addProfitShareReceiver',
    version: '1.0',
    bizParams: {
      ...params,
    },
  });
}

// 删除分账接收方
export async function deleteProfitShareReceiver(params: { receiverId: string }) {
  return request(waasPath + '/profitShare/deleteReceiver', {
    serviceName: 'ProfitSharingApiService',
    methodName: 'deleteProfitShareReceiver',
    version: '1.0',
    bizParams: {
      ...params,
    },
  });
}

// 申请分账账单
export async function applyProfitShareBill(params: {
  billDate: string;
  tarType: string;
}) {
  return request(waasPath + '/profitShare/applyBill', {
    serviceName: 'ProfitSharingApiService',
    methodName: 'applyProfitShareBill',
    version: '1.0',
    bizParams: {
      ...params,
    },
  });
}

// 下载账单
export async function downloadProfitShareBill(params: {
  billDate: string;
  tarType: string;
}) {
  return request(waasPath + '/profitShare/downloadBill', {
    serviceName: 'ProfitSharingApiService',
    methodName: 'downloadProfitShareBill',
    version: '1.0',
    bizParams: {
      ...params,
    },
  });
}
