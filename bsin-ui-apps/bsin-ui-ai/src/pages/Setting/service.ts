import { request } from 'umi'
let aiAgent = process.env.contextPath_aiAgent;

import type { CurrentUser, GeographicItemType } from './data'

export async function queryCurrent(): Promise<{ data: CurrentUser }> {
  return request('/api/accountSettingCurrentUser')
}

export async function queryProvince(): Promise<{ data: GeographicItemType[] }> {
  return request('/api/geographic/province')
}

export async function queryCity(
  province: string,
): Promise<{ data: GeographicItemType[] }> {
  return request(`/api/geographic/city/${province}`)
}

export async function query() {
  return request('/api/users')
}

// 查询客户信息
export const getCustomerDetail = (params) => {
  return request(aiAgent + '/getDetail', {
    serviceName: 'CustomerService',
    methodName: 'getDetail',
    version: '1.0',
    bizParams: {
      ...params,
    },
  })
}

// 获取公众号验证码
export const getMpVerifyCode = (params) => {
  return request(aiAgent + '/getMpVerifyCode', {
    serviceName: 'AiCustomerFunctionService',
    methodName: 'getMpVerifyCode',
    version: '1.0',
    bizParams: {
      ...params,
    },
  })
}

// 验证公众号验证码
export const verifyMpCode = (params) => {
  return request(aiAgent + '/verifyMpCode', {
    serviceName: 'AiCustomerFunctionService',
    methodName: 'verifyMpCode',
    version: '1.0',
    bizParams: {
      ...params,
    },
  })
}

// 设置钱包信息
export const settingWallet = (params) => {
  return request(aiAgent + '/settingWallet', {
    serviceName: 'CustomerService',
    methodName: 'settingWallet',
    version: '1.0',
    bizParams: {
      ...params,
    },
  })
}

// 设置手机
export const settingPhone = (params) => {
  return request(aiAgent + '/settingPhone', {
    serviceName: 'CustomerService',
    methodName: 'settingPhone',
    version: '1.0',
    bizParams: {
      ...params,
    },
  })
}

// 设置手机
export const settingEmail = (params) => {
  return request(aiAgent + '/settingEmail', {
    serviceName: 'CustomerService',
    methodName: 'settingEmail',
    version: '1.0',
    bizParams: {
      ...params,
    },
  })
}

// 用户信息更新
export const eidtCustomerService = (params) => {
  return request(aiAgent + '/edit', {
    serviceName: 'CustomerService',
    methodName: 'edit',
    version: '1.0',
    bizParams: {
      ...params,
    },
  })
}
