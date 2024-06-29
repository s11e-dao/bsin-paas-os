import type { ProColumns } from '@ant-design/pro-table';

export type columnsDataType = {
  order: number;
  acName: string;
  custNo: string;
  acNo: string;
  balance: string;
  custType: string;
  openAcDate: string;
  status: string;
  startTime: string;
  endTime: string;
};

const columnsData: ProColumns<columnsDataType>[] = [
  // 配置搜索框
  {
    title: '交易hash',
    dataIndex: 'txHash',
    hideInTable: true,
    fieldProps: {
      maxLength: 20,
    },
  },
  {
    title: '接收地址',
    dataIndex: 'to',
    hideInTable: true,
    fieldProps: {
      maxLength: 20,
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
    // tooltip: '过长会自动收缩',
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
    width: 80,
    dataIndex: 'txAmount',
    hideInSearch: true,
  },
  {
    title: '交易类型',
    width: 80,
    dataIndex: 'transactionType',
    hideInSearch: true,
  },
  {
    title: '交易状态',
    width: 80,
    dataIndex: 'transactionStatus',
    hideInSearch: true,
    valueEnum: {
      0: { text: '成功', status: 'Processing' },
      1: { text: '失败', status: 'Error' },
    },
  },
  {
    title: '交易时间',
    width: 160,
    dataIndex: 'createTime',
    hideInSearch: true,
  },
  {
    title: '合约地址',
    width: 160,
    dataIndex: 'contractAddress',
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
    title: '执行方法',
    width: 160,
    dataIndex: 'contractMethod',
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
