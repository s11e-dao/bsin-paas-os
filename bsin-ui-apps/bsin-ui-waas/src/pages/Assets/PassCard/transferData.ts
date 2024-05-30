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
    title: '流水号',
    width: 170,
    dataIndex: 'serialNo',
    fixed: 'left',
    hideInSearch: true,
  },
  {
    title: '会员卡资产编号',
    width: 170,
    dataIndex: 'digitalAssetsCollectionNo',
    hideInSearch: true,
  },
  {
    title: 'tokenId',
    width: 75,
    dataIndex: 'tokenId',
    hideInSearch: true,
  },
  {
    title: '转账数量',
    width: 70,
    dataIndex: 'amount',
    hideInSearch: true,
  },
  {
    title: 'fromAddress',
    width: 120,
    dataIndex: 'fromAddress',
    hideInSearch: true,
  },
  {
    title: 'toAddress',
    width: 120,
    dataIndex: 'toAddress',
    hideInSearch: true,
  },
  {
    title: 'toPhone',
    width: 70,
    dataIndex: 'toPhone',
    hideInSearch: true,
  },
  {
    title: 'fromCustomerNo',
    width: 170,
    dataIndex: 'fromCustomerNo',
    hideInSearch: true,
  },
  {
    title: 'toCustomerNo',
    width: 170,
    dataIndex: 'toCustomerNo',
    hideInSearch: true,
  },
  {
    title: '交易hash',
    width: 300,
    dataIndex: 'txHash',
    hideInSearch: true,
  },
  {
    title: '交易时间',
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
