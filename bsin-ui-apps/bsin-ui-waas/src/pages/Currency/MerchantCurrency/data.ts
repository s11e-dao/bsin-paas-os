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
    title: '币种名称',
    dataIndex: 'chainCoinName',
    hideInTable: true,
    fieldProps: {
      maxLength: 20,
    },
  },
  {
    title: '币种符号',
    dataIndex: 'coin',
    hideInTable: true,
    fieldProps: {
      maxLength: 20,
    },
  },

  // table里面的内容
  {
    title: 'ID',
    width: 190,
    dataIndex: 'serialNo',
    hideInSearch: true,
  },
  {
    title: '币种名称',
    width: 160,
    dataIndex: 'chainCoinName',
    hideInSearch: true,
  },
  {
    title: '币种符号',
    width: 160,
    dataIndex: 'coin',
    hideInSearch: true,
  },
  {
    title: '币种描述',
    width: 160,
    dataIndex: 'remark',
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
