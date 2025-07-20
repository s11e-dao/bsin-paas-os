import type { ProColumns } from '@ant-design/pro-table';

// 分账配置数据类型
export type ProfitSharingConfigDataType = {
  serialNo: string;
  tenantId: string;
  superTenantRate: number;
  tenantRate: number;
  sysAgentRate: number;
  customerRate: number;
  distributorRate: number;
  exchangeDigitalPointsRate: number;
  createTime: string;
  updateTime: string;
};

// 表单值类型
export interface FormValues {
  superTenantRate: number;
  tenantRate: number;
  sysAgentRate: number;
  customerRate: number;
  distributorRate: number;
  exchangeDigitalPointsRate: number;
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
  superTenantRate: 0,
  tenantRate: 0,
  sysAgentRate: 0,
  customerRate: 0,
  distributorRate: 0,
  exchangeDigitalPointsRate: 0,
};

// 配置项数据（不包含JSX）
export const configItemsData: ConfigItemData[] = [
  {
    name: 'superTenantRate',
    label: '运营平台分佣比例',
    iconName: 'BankOutlined',
    color: '#1890ff',
    description: '平台运营方获得的分佣比例'
  },
  {
    name: 'tenantRate',
    label: '租户平台分佣比例',
    iconName: 'ShopOutlined',
    color: '#52c41a',
    description: '租户平台获得的分佣比例'
  },
  {
    name: 'sysAgentRate',
    label: '合伙人分佣比例',
    iconName: 'TeamOutlined',
    color: '#faad14',
    description: '合伙人获得的分佣比例'
  },
  {
    name: 'customerRate',
    label: '消费者返利比例',
    iconName: 'UserOutlined',
    color: '#722ed1',
    description: '消费者获得的返利比例'
  },
  {
    name: 'distributorRate',
    label: '分销者分佣比例',
    iconName: 'GiftOutlined',
    color: '#eb2f96',
    description: '分销者获得的分佣比例'
  },
  {
    name: 'exchangeDigitalPointsRate',
    label: '数字积分兑换比例',
    iconName: 'DollarOutlined',
    color: '#13c2c2',
    description: '佣金兑换数字积分的比例'
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
    title: '运营平台分佣比例',
    width: 160,
    dataIndex: 'superTenantRate',
    hideInSearch: true,
  },
  {
    title: '租户平台分佣比例',
    width: 160,
    dataIndex: 'tenantRate',
    hideInSearch: true,
  },
  {
    title: '合伙人分佣比例',
    width: 160,
    dataIndex: 'sysAgentRate',
    hideInSearch: true,
  },
  {
    title: '消费者返利比例',
    width: 160,
    dataIndex: 'customerRate',
    hideInSearch: true,
  },
  {
    title: '分销者分佣比例',
    width: 160,
    dataIndex: 'distributorRate',
    hideInSearch: true,
  },
  {
    title: '数字积分兑换比例',
    width: 160,
    dataIndex: 'exchangeDigitalPointsRate',
    hideInSearch: true,
  },
  {
    title: '创建时间',
    width: 160,
    dataIndex: 'createTime',
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
