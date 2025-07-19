import type { ProColumns } from '@ant-design/pro-table';

export type RevenueShareDataType = {
  serialNo: string;
  transactionNo: string;
  tenantId: string;
  amount: number;
  superTenantAmount: number;
  tenantAmount: number;
  sysAgentAmount: number;
  customerAmount: number;
  distributorAmount: number;
  exchangeDigitalPointsAmount: number;
  status: string;
  createTime: string;
};

const columnsData: ProColumns<RevenueShareDataType>[] = [
  // 配置搜索框
  {
    title: '交易编号',
    dataIndex: 'transactionNo',
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
    title: '状态',
    dataIndex: 'status',
    valueType: 'select',
    hideInTable: true,
    valueEnum: {
      'PENDING': {
        text: '待处理',
      },
      'SUCCESS': {
        text: '成功',
      },
      'FAILED': {
        text: '失败',
      },
    },
  },

  // table里面的内容
  {
    title: '分账ID',
    width: 190,
    dataIndex: 'serialNo',
    fixed: 'left',
    hideInSearch: true,
  },
  {
    title: '交易编号',
    width: 160,
    dataIndex: 'transactionNo',
    hideInSearch: true,
  },
  {
    title: '租户ID',
    width: 160,
    dataIndex: 'tenantId',
    hideInSearch: true,
  },
  {
    title: '交易金额',
    width: 120,
    dataIndex: 'amount',
    hideInSearch: true,
    render: (text) => `¥${text}`,
  },
  {
    title: '运营平台分账',
    width: 140,
    dataIndex: 'superTenantAmount',
    hideInSearch: true,
    render: (text) => `¥${text}`,
  },
  {
    title: '租户平台分账',
    width: 140,
    dataIndex: 'tenantAmount',
    hideInSearch: true,
    render: (text) => `¥${text}`,
  },
  {
    title: '代理商分账',
    width: 120,
    dataIndex: 'sysAgentAmount',
    hideInSearch: true,
    render: (text) => `¥${text}`,
  },
  {
    title: '消费者返利',
    width: 120,
    dataIndex: 'customerAmount',
    hideInSearch: true,
    render: (text) => `¥${text}`,
  },
  {
    title: '分销者分账',
    width: 120,
    dataIndex: 'distributorAmount',
    hideInSearch: true,
    render: (text) => `¥${text}`,
  },
  {
    title: '数字积分兑换',
    width: 140,
    dataIndex: 'exchangeDigitalPointsAmount',
    hideInSearch: true,
    render: (text) => `¥${text}`,
  },
  {
    title: '状态',
    width: 100,
    hideInSearch: true,
    dataIndex: 'status',
    valueType: 'select',
    valueEnum: {
      'PENDING': {
        text: '待处理',
        status: 'Processing',
      },
      'SUCCESS': {
        text: '成功',
        status: 'Success',
      },
      'FAILED': {
        text: '失败',
        status: 'Error',
      },
    },
  },
  {
    title: '创建时间',
    width: 160,
    dataIndex: 'createTime',
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
