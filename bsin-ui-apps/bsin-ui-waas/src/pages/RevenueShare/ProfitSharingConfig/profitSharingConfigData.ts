import type { ProColumns } from '@ant-design/pro-table';

// 让利配置数据类型
export type ProfitSharingConfigDataType = {
  serialNo: string;
  tenantId: string;
  merchantNo: string;
  memberModel: string;
  callbackAddress: string;
  profitSharingRate: number;
  createBy: string;
  createTime: string;
  updateBy: string;
  updateTime: string;
  delFlag: number;
};

// 表单值类型
export interface FormValues {
  profitSharingRate: number;
}

// 配置项定义（不包含JSX）
export interface ConfigItemData {
  name: string;
  label: string;
  iconName: string;
  color: string;
  description: string;
}

// 默认表单值
export const defaultFormValues: FormValues = {
  profitSharingRate: 0,
};

// 配置项数据（不包含JSX）
export const configItemsData: ConfigItemData[] = [
  {
    name: 'profitSharingRate',
    label: '商户让利比例',
    iconName: 'GiftOutlined',
    color: '#1890ff',
    description: '商户让利给平台的比例'
  }
];

// 创建表格列配置的函数（返回纯数据，不包含JSX）
export const createProfitSharingConfigColumnsData = (): Omit<ProColumns<ProfitSharingConfigDataType>, 'render'>[] => [
  // 搜索字段
  {
    title: '配置ID',
    dataIndex: 'serialNo',
    hideInTable: true,
    fieldProps: {
      maxLength: 32,
    },
  },
  {
    title: '租户ID',
    dataIndex: 'tenantId',
    hideInTable: true,
    fieldProps: {
      maxLength: 32,
    },
  },
  {
    title: '商户编号',
    dataIndex: 'merchantNo',
    hideInTable: true,
    fieldProps: {
      maxLength: 32,
    },
  },
  {
    title: '会员模型',
    dataIndex: 'memberModel',
    valueType: 'select',
    hideInTable: true,
    valueEnum: {
      '1': { text: '平台会员' },
      '2': { text: '商户会员' },
      '3': { text: '店铺会员' },
    },
  },
  // 表格显示字段
  {
    title: '配置ID',
    width: 190,
    dataIndex: 'serialNo',
    fixed: 'left',
    hideInSearch: true,
  },
  {
    title: '租户ID',
    width: 160,
    dataIndex: 'tenantId',
    hideInSearch: true,
  },
  {
    title: '商户编号',
    width: 160,
    dataIndex: 'merchantNo',
    hideInSearch: true,
  },
  {
    title: '会员模型',
    width: 120,
    dataIndex: 'memberModel',
    hideInSearch: true,
    valueType: 'select',
    valueEnum: {
      '1': { text: '平台会员' },
      '2': { text: '商户会员' },
      '3': { text: '店铺会员' },
    },
  },
  {
    title: '回调地址',
    width: 200,
    dataIndex: 'callbackAddress',
    hideInSearch: true,
    ellipsis: true,
  },
  {
    title: '让利比例',
    width: 120,
    dataIndex: 'profitSharingRate',
    hideInSearch: true,
  },
  {
    title: '创建人',
    width: 120,
    dataIndex: 'createBy',
    hideInSearch: true,
  },
  {
    title: '创建时间',
    width: 160,
    dataIndex: 'createTime',
    hideInSearch: true,
  },
  {
    title: '更新人',
    width: 120,
    dataIndex: 'updateBy',
    hideInSearch: true,
  },
  {
    title: '更新时间',
    width: 160,
    dataIndex: 'updateTime',
    hideInSearch: true,
  },
  {
    title: '操作',
    width: 100,
    hideInSearch: true,
    dataIndex: 'action',
    fixed: 'right',
  },
];
