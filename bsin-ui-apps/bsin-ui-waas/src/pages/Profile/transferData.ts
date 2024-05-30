import type { ProColumns } from '@ant-design/pro-table';

export type columnsTransferDataType = {
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

const columnsTransferData: ProColumns<columnsTransferDataType>[] = [
  // 配置搜索框
  {
    title: '名称',
    dataIndex: 'name',
    hideInTable: true,
  },
  {
    title: 'chainEnv',
    dataIndex: 'chainEnv',
    hideInTable: true,
    valueEnum: {
      test: {
        text: '测试网',
      },
      main: {
        text: '主网',
      },
    },
    fieldProps: {
      maxLength: 20,
    },
  },
  {
    title: 'chainType',
    dataIndex: 'chainType',
    hideInTable: true,
    valueEnum: {
      conflux: {
        text: 'conflux',
      },
      bsc: {
        text: 'bsc',
      },
    },
    fieldProps: {
      maxLength: 20,
    },
  },

  // table里面的内容
  {
    title: '资产ID',
    width: 180,
    dataIndex: 'serialNo',
    fixed: 'left',
    hideInSearch: true,
  },
  {
    title: '交易hash',
    width: 600,
    dataIndex: 'txHash',
    hideInSearch: true,
  },
  {
    title: 'fromCustomerNo',
    width: 180,
    dataIndex: 'fromCustomerNo',
    hideInSearch: true,
  },
  {
    title: 'fromAddress',
    width: 400,
    dataIndex: 'fromAddress',
    hideInSearch: true,
  },
  {
    title: 'toCustomerNo',
    width: 180,
    dataIndex: 'toCustomerNo',
    hideInSearch: true,
  },
  {
    title: 'toAddress',
    width: 400,
    dataIndex: 'toAddress',
    hideInSearch: true,
  },
  {
    title: 'toName',
    width: 160,
    dataIndex: 'toName',
    hideInSearch: true,
  },
  {
    title: 'toPhone',
    width: 160,
    dataIndex: 'toPhone',
    hideInSearch: true,
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

export default columnsTransferData;
