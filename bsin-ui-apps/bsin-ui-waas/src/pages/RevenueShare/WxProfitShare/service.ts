import { request } from '@umijs/max';

// 平台应用查询接口
export async function getBizRoleAppList(params: any) {
  return request('/crm/bizRoleApp/getPageList', {
    method: 'POST',
    data: params,
  });
}

// 获取平台应用支付通道配置
export async function getBizRoleAppPayChannelConfig(params: {
  bizRoleAppId: string;
  payChannelCode: string;
}) {
  return request('/waas/payChannelConfig/getBizRoleAppPayChannelConfig', {
    method: 'POST',
    data: params,
  });
}

// 微信分账API接口
export interface WxProfitShareParams {
  bizRoleAppId: string;
  payChannelCode: string;
  [key: string]: any;
}

// 请求分账
export async function requestProfitShare(params: WxProfitShareParams) {
  return request('/wx/profitShare/request', {
    method: 'POST',
    data: params,
  });
}

// 查询分账结果
export async function queryProfitShareResult(params: WxProfitShareParams) {
  return request('/wx/profitShare/query', {
    method: 'POST',
    data: params,
  });
}

// 请求分账回退
export async function requestProfitShareReturn(params: WxProfitShareParams) {
  return request('/wx/profitShare/return', {
    method: 'POST',
    data: params,
  });
}

// 查询分账回退结果
export async function queryProfitShareReturnResult(params: WxProfitShareParams) {
  return request('/wx/profitShare/returnQuery', {
    method: 'POST',
    data: params,
  });
}

// 解冻剩余资金
export async function unfreezeRemainingFunds(params: WxProfitShareParams) {
  return request('/wx/profitShare/unfreeze', {
    method: 'POST',
    data: params,
  });
}

// 查询剩余待分金额
export async function queryRemainingAmount(params: WxProfitShareParams) {
  return request('/wx/profitShare/remaining', {
    method: 'POST',
    data: params,
  });
}

// 添加分账接收方
export async function addProfitShareReceiver(params: WxProfitShareParams) {
  return request('/wx/profitShare/addReceiver', {
    method: 'POST',
    data: params,
  });
}

// 删除分账接收方
export async function deleteProfitShareReceiver(params: WxProfitShareParams) {
  return request('/wx/profitShare/deleteReceiver', {
    method: 'POST',
    data: params,
  });
}

// 申请分账账单
export async function applyProfitShareBill(params: WxProfitShareParams) {
  return request('/wx/profitShare/applyBill', {
    method: 'POST',
    data: params,
  });
}

// 下载账单
export async function downloadProfitShareBill(params: WxProfitShareParams) {
  return request('/wx/profitShare/downloadBill', {
    method: 'POST',
    data: params,
  });
}
