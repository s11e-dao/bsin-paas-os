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
  // {
  //   title: '客户号',
  //   dataIndex: 'customerNo',
  //   hideInTable: true,
  //   fieldProps: {
  //     maxLength: 20,
  //   },
  // },
  {
    title: '账户编号',
    dataIndex: 'accountNo',
    hideInTable: true,
    fieldProps: {
      maxLength: 20,
    },
  },

  {
    title: '冻结状态',
    width: 100,
    hideInTable: true,
    dataIndex: 'status',
    valueType: 'select',
    valueEnum: {
      '1': {
        text: '已冻结',
      },
      '2': {
        text: '部分解冻',
      },
      '3': {
        text: '已解冻',
      },
    },
  },

  {
    title: '冻结类型',
    width: 100,
    hideInTable: true,
    dataIndex: 'status',
    valueType: 'select',
    valueEnum: {
      '1': {
        text: '社区提案',
      },
      '2': {
        text: '订单',
      },
      '3': {
        text: '数字资产',
      },
      '4': {
        text: '任务',
      },
    },
  },

  // table里面的内容
  // {
  //   title: '客户号',
  //   width: 120,
  //   dataIndex: 'customerNo',
  //   hideInSearch: true,
  // },
  {
    title: '账户编号',
    width: 120,
    dataIndex: 'accountNo',
    hideInSearch: true,
  },
  {
    title: '冻结金额',
    width: 120,
    dataIndex: 'freezeAmount',
    hideInSearch: true,
  },
  {
    title: '冻结状态',
    width: 100,
    hideInSearch: true,
    dataIndex: 'status',
    valueType: 'select',
    valueEnum: {
      '1': {
        text: '已冻结',
      },
      '2': {
        text: '部分解冻',
      },
      '3': {
        text: '已解冻',
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
