import { request } from '@umijs/max'
let crmPath = process.env.contextPath_crm;
let omsPath = process.env.contextPath_oms;

// 分页查询
export const getCustomerPageList = (params) => {
  return request(crmPath + '/customer/getPageList', {
    serviceName: 'CustomerService',
    methodName: 'getPageList',
    version: '1.0',
    bizParams: {
      ...params,
    },
  });
};

// 创建
export const addCustomer = (params) => {
  return request(crmPath + '/customer/add', {
    serviceName: 'CustomerService',
    methodName: 'add',
    version: '1.0',
    bizParams: {
      ...params,
    },
  });
};

// 删除
export const deleteCustomer = (params) => {
  return request(crmPath + '/customer/delete', {
    serviceName: 'CustomerService',
    methodName: 'delete',
    version: '1.0',
    bizParams: {
      ...params,
    },
  });
};

// 查询详情
export const getCustomerDetail = (params) => {
  console.log('params', params);
  return request(crmPath + '/customer/getDetail', {
    serviceName: 'CustomerService',
    methodName: 'getDetail',
    version: '1.0',
    bizParams: {
      ...params,
    },
  });
};

export const getCustomerDeliveryAddress = (params) => {
  console.log('params', params);
  return request(omsPath + '/deliveryAddress/getList', {
    serviceName: 'DeliveryAddressService',
    methodName: 'getList',
    version: '1.0',
    bizParams: {
      ...params,
    },
  });
};
