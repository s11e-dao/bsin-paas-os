import type { ProColumns } from '@ant-design/pro-table';

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

const columnsData: ProColumns<ProfitSharingConfigDataType>[] = [
  // 配置搜索框
  {
    title: '租户ID',
    dataIndex: 'tenantId',
    hideInTable: true,
    fieldProps: {
      maxLength: 32,
    },
  },

  // table里面的内容
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
    render: (text) => `${text}%`,
  },
  {
    title: '租户平台分佣比例',
    width: 160,
    dataIndex: 'tenantRate',
    hideInSearch: true,
    render: (text) => `${text}%`,
  },
  {
    title: '代理商分佣比例',
    width: 160,
    dataIndex: 'sysAgentRate',
    hideInSearch: true,
    render: (text) => `${text}%`,
  },
  {
    title: '消费者返利比例',
    width: 160,
    dataIndex: 'customerRate',
    hideInSearch: true,
    render: (text) => `${text}%`,
  },
  {
    title: '分销者分佣比例',
    width: 160,
    dataIndex: 'distributorRate',
    hideInSearch: true,
    render: (text) => `${text}%`,
  },
  {
    title: '数字积分兑换比例',
    width: 160,
    dataIndex: 'exchangeDigitalPointsRate',
    hideInSearch: true,
    render: (text) => `${text}%`,
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

export default columnsData;
