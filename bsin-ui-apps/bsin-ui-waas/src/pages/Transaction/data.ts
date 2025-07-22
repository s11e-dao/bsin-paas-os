import type { ProColumns } from '@ant-design/pro-table';

/**
 * 交易数据类型
 */
export type columnsDataType = {
  serialNo: string;
  txHash: string;
  from: string;
  to: string;
  txAmount: number;
  transactionType: number;
  transactionStatus: number;
  createTime: string;
  contractAddress: string;
  contractMethod: string;
  productId?: string;
  amount?: number;
  description?: string;
  status?: string;
  transactionTypeRender?: number;
  userId?: string;
};

/**
 * 交易表格列配置
 */
const columnsData: ProColumns<columnsDataType>[] = [
  // 配置搜索框
  {
    title: '交易类型',
    width: 120,
    hideInTable: true,
    dataIndex: 'transactionType',
    valueType: 'select',
    valueEnum: {
      "1": {
        text: '支付',
      },
      "2": {
        text: '充值',
      },
      "3": {
        text: '转账',
      },
      "4": {
        text: '提现',
      },
      "5": {
        text: '退款',
      },
      "6": {
        text: '结算',
      },
      "7": {
        text: '收入',
      },
      "8": {
        text: '赎回',
      },
    },
  },
  {
    title: '交易hash',
    dataIndex: 'txHash',
    hideInTable: true,
    fieldProps: {
      maxLength: 20,
      placeholder: '请输入交易hash',
    },
  },
  {
    title: '接收地址',
    dataIndex: 'to',
    hideInTable: true,
    fieldProps: {
      maxLength: 20,
      placeholder: '请输入接收地址',
    },
  },

  // table里面的内容
  {
    title: '交易hash',
    width: 190,
    dataIndex: 'txHash',
    hideInSearch: true,
    copyable: true,
    ellipsis: true,
    formItemProps: {
      rules: [
        {
          required: true,
          message: '此项为必填项',
        },
      ],
    }
  },
  {
    title: '转出地址',
    width: 160,
    dataIndex: 'from',
    hideInSearch: true,
    copyable: true,
    ellipsis: true,
    formItemProps: {
      rules: [
        {
          required: true,
          message: '此项为必填项',
        },
      ],
    }
  },
  {
    title: '转入地址',
    width: 160,
    dataIndex: 'to',
    hideInSearch: true,
    copyable: true,
    ellipsis: true,
    formItemProps: {
      rules: [
        {
          required: true,
          message: '此项为必填项',
        },
      ],
    }
  },
  {
    title: '交易金额',
    width: 120,
    dataIndex: 'txAmount',
    hideInSearch: true,
    render: (_, record) => `¥${record.txAmount?.toFixed(2) || '0.00'}`,
  },
  {
    title: '交易类型',
    width: 100,
    dataIndex: 'transactionType',
    hideInSearch: true,
    valueType: 'select',
    valueEnum: {
      1: { text: '支付', status: 'Success' },
      2: { text: '充值', status: 'Processing' },
      3: { text: '转账', status: 'Warning' },
      4: { text: '提现', status: 'Error' },
      5: { text: '退款', status: 'Default' },
      6: { text: '结算', status: 'Success' },
      7: { text: '收入', status: 'Success' },
      8: { text: '赎回', status: 'Warning' },
    },
  },
  {
    title: '交易状态',
    width: 100,
    dataIndex: 'transactionStatus',
    hideInSearch: true,
    valueEnum: {
      0: { text: '成功', status: 'Success' },
      1: { text: '失败', status: 'Error' },
    },
  },
  {
    title: '交易时间',
    width: 160,
    dataIndex: 'createTime',
    hideInSearch: true,
    valueType: 'dateTime',
  },
  {
    title: '合约地址',
    width: 160,
    dataIndex: 'contractAddress',
    hideInSearch: true,
    copyable: true,
    ellipsis: true,
  },
  {
    title: '执行方法',
    width: 160,
    dataIndex: 'contractMethod',
    hideInSearch: true,
    ellipsis: true,
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
