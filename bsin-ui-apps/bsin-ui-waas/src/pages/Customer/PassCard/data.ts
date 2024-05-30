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
    title: '会员卡号',
    dataIndex: 'tokenId',
    hideInTable: true,
    fieldProps: {
      maxLength: 20,
    },
  },

  // table里面的内容
  {
    title: '客户编号',
    width: 190,
    dataIndex: 'customerNo',
    hideInSearch: true,
  },
  {
    title: '会员卡号',
    width: 160,
    dataIndex: 'tokenId',
    hideInSearch: true,
  },
  {
    title: 'amount',
    width: 100,
    dataIndex: 'amount',
    hideInSearch: true,
  },
  {
    title: 'TBA Address',
    width: 320,
    dataIndex: 'tbaAddress',
    hideInSearch: true,
  },
  {
    title: '状态',
    width: 100,
    dataIndex: 'status',
    hideInSearch: true,
  },
  {
    title: '备注',
    width: 100,
    dataIndex: 'remark',
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
