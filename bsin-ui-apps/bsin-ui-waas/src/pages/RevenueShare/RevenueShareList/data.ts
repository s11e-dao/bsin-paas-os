import type { ProColumns } from '@ant-design/pro-table';

/**
 * 分账明细数据类型
 */
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

/**
 * 分账明细表格列配置
 */
const columnsData: ProColumns<RevenueShareDataType>[] = [
  // 搜索配置
  {
    title: '交易编号',
    dataIndex: 'transactionNo',
    hideInTable: true,
    fieldProps: {
      maxLength: 32,
      placeholder: '请输入交易编号',
    },
  },
  {
    title: '租户ID',
    dataIndex: 'tenantId',
    hideInTable: true,
    fieldProps: {
      maxLength: 32,
      placeholder: '请输入租户ID',
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

  // 表格显示列
  {
    title: '分账ID',
    width: 190,
    dataIndex: 'serialNo',
    fixed: 'left',
    hideInSearch: true,
    ellipsis: true,
  },
  {
    title: '交易编号',
    width: 160,
    dataIndex: 'transactionNo',
    hideInSearch: true,
    ellipsis: true,
  },
  {
    title: '租户ID',
    width: 160,
    dataIndex: 'tenantId',
    hideInSearch: true,
    ellipsis: true,
  },
  {
    title: '交易金额',
    width: 120,
    dataIndex: 'amount',
    hideInSearch: true,
    render: (_, record) => `¥${record.amount?.toFixed(2) || '0.00'}`,
  },
  {
    title: '运营平台分账',
    width: 140,
    dataIndex: 'superTenantAmount',
    hideInSearch: true,
    render: (_, record) => `¥${record.superTenantAmount?.toFixed(2) || '0.00'}`,
  },
  {
    title: '租户平台分账',
    width: 140,
    dataIndex: 'tenantAmount',
    hideInSearch: true,
    render: (_, record) => `¥${record.tenantAmount?.toFixed(2) || '0.00'}`,
  },
  {
    title: '合伙人分账',
    width: 120,
    dataIndex: 'sysAgentAmount',
    hideInSearch: true,
    render: (_, record) => `¥${record.sysAgentAmount?.toFixed(2) || '0.00'}`,
  },
  {
    title: '消费者返利',
    width: 120,
    dataIndex: 'customerAmount',
    hideInSearch: true,
    render: (_, record) => `¥${record.customerAmount?.toFixed(2) || '0.00'}`,
  },
  {
    title: '分销者分账',
    width: 120,
    dataIndex: 'distributorAmount',
    hideInSearch: true,
    render: (_, record) => `¥${record.distributorAmount?.toFixed(2) || '0.00'}`,
  },
  {
    title: '数字积分兑换',
    width: 140,
    dataIndex: 'exchangeDigitalPointsAmount',
    hideInSearch: true,
    render: (_, record) => `¥${record.exchangeDigitalPointsAmount?.toFixed(2) || '0.00'}`,
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
    valueType: 'dateTime',
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
