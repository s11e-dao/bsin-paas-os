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
    title: '资产集合编号',
    dataIndex: 'digitalAssetsCollectionNo',
    hideInTable: true,
  },
  {
    title: 'tokenId',
    dataIndex: 'tokenId',
    hideInTable: true,
    fieldProps: {
      maxLength: 20,
    },
  },
  {
    title: '交易hash',
    dataIndex: 'txHash',
    hideInTable: true,
    fieldProps: {
      maxLength: 20,
    },
  },

  // table里面的内容
  {
    title: '铸造流水号',
    width: 120,
    dataIndex: 'serialNo',
    fixed: 'left',
    hideInSearch: true,
  },
  {
    title: '资产集合编号',
    width: 120,
    dataIndex: 'digitalAssetsCollectionNo',
    hideInSearch: true,
  },
  {
    title: 'tokenId',
    width: 70,
    dataIndex: 'tokenId',
    hideInSearch: true,
  },
  {
    title: '铸造数量',
    width: 75,
    dataIndex: 'amount',
    hideInSearch: true,
  },
  {
    title: '交易hash',
    width: 380,
    dataIndex: 'txHash',
    hideInSearch: true,
  },
  {
    title: '接收地址',
    width: 290,
    dataIndex: 'toAddress',
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
