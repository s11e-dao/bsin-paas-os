import { request } from 'umi';
let crmPath = process.env.contextPath_crm;

import type { CurrentUser, GeographicItemType } from './data';

export async function queryCurrent(): Promise<{ data: CurrentUser }> {
  return request('/api/accountSettingCurrentUser');
}

export async function queryProvince(): Promise<{ data: GeographicItemType[] }> {
  return request('/api/geographic/province');
}

export async function queryCity(
  province: string,
): Promise<{ data: GeographicItemType[] }> {
  return request(`/api/geographic/city/${province}`);
}

export async function query() {
  return request('/api/users');
}

// 查询客户信息
export const getCustomerDetail = (params) => {
  return request('/getDetail', {
    serviceName: 'CustomerService',
    methodName: 'getDetail',
    version: '1.0',
    bizParams: {
      ...params,
    },
  });
};

// 设置钱包信息
export const settingWallet = (params) => {
  return request('/settingWallet', {
    serviceName: 'CustomerService',
    methodName: 'settingWallet',
    version: '1.0',
    bizParams: {
      ...params,
    },
  });
};

// 设置手机
export const settingPhone = (params) => {
  return request('/settingPhone', {
    serviceName: 'CustomerService',
    methodName: 'settingPhone',
    version: '1.0',
    bizParams: {
      ...params,
    },
  });
};

// 设置手机
export const settingEmail = (params) => {
  return request('/settingEmail', {
    serviceName: 'CustomerService',
    methodName: 'settingEmail',
    version: '1.0',
    bizParams: {
      ...params,
    },
  });
};

// 用户信息更新
export const eidtCustomerService = (params) => {
  return request('/eidtCustomerService', {
    serviceName: 'CustomerService',
    methodName: 'edit',
    version: '1.0',
    bizParams: {
      ...params,
    },
  });
};
