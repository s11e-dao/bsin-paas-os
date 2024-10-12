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
    title: '客户编号',
    dataIndex: 'merchantNo',
    hideInTable: true,
    width: 100,
    fieldProps: {
      maxLength: 20,
    },
  },
  {
    title: '账户编号',
    dataIndex: 'accountNo',
    hideInTable: true,
    width: 100,
    fieldProps: {
      maxLength: 20,
    },
  },
  {
    title: 'method',
    dataIndex: 'method',
    hideInTable: true,
    width: 50,
    fieldProps: {
      maxLength: 20,
    },
    valueEnum: {
      mint: {
        text: 'mint',
      },
      burn: {
        text: 'burn',
      },
    },
  },
  {
    title: '交易hash',
    dataIndex: 'txHash',
    width: 100,
    hideInTable: true,
    fieldProps: {
      maxLength: 20,
    },
  },

  // table里面的内容
  {
    title: '铸造流水号',
    width: 190,
    dataIndex: 'serialNo',
    fixed: 'left',
    hideInSearch: true,
  },
  {
    title: '客户号',
    width: 160,
    dataIndex: 'customerNo',
    hideInSearch: true,
  },
  {
    title: '劳动价值',
    width: 160,
    dataIndex: 'reserve',
    hideInSearch: true,
  },
  {
    title: '价格',
    width: 160,
    dataIndex: 'price',
    hideInSearch: true,
  },
  {
    title: '方法',
    width: 160,
    dataIndex: 'method',
    hideInSearch: true,
  },
  {
    title: '描述',
    width: 160,
    dataIndex: 'description',
    hideInSearch: true,
  },
  {
    title: '创建者',
    width: 160,
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
    title: '操作',
    width: 100,
    hideInSearch: true,
    dataIndex: 'action',
    fixed: 'right',
  },
];

export default columnsData;
