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
    dataIndex: 'customerNo',
    hideInTable: true,
  },
  {
    title: '租户编号',
    dataIndex: 'tenantId',
    hideInTable: true,
  },
  {
    title: '商户编号',
    dataIndex: 'merchantNo',
    hideInTable: true,
  },
  {
    title: '数字资产编号',
    dataIndex: 'digitalAssetsCollectionNo',
    hideInTable: true,
  },

  // table里面的内容
  {
    title: '客户号',
    width: 170,
    dataIndex: 'customerNo',
    hideInSearch: true,
  },
  {
    title: '租户ID',
    width: 170,
    dataIndex: 'tenantId',
    hideInSearch: true,
  },
  {
    title: '商户ID',
    width: 170,
    dataIndex: 'merchantNo',
    hideInSearch: true,
  },
  {
    title: '数字资产编号',
    width: 190,
    dataIndex: 'digitalAssetsCollectionNo',
    hideInSearch: true,
  },
  {
    title: 'tokenId',
    width: 80,
    dataIndex: 'tokenId',
    hideInSearch: true,
  },
  {
    title: 'amount',
    width: 80,
    dataIndex: 'amount',
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
