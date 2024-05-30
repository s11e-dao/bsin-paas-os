import type { ProColumns } from '@ant-design/pro-table';

export type columnsObtainCodeDataType = {
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

const columnsObtainCodeData: ProColumns<columnsObtainCodeDataType>[] = [
  // 配置搜索框

  // table里面的内容
  {
    title: 'ID',
    width: 190,
    dataIndex: 'serialNo',
    fixed: 'left',
    hideInSearch: true,
  },
  {
    title: '资产编号',
    width: 160,
    dataIndex: 'assetsNo',
    hideInSearch: true,
  },
  {
    title: '领取码',
    width: 160,
    dataIndex: 'password',
    hideInSearch: true,
  },
  {
    title: '状态',
    width: 160,
    dataIndex: 'status',
    hideInSearch: true,
    valueEnum: {
      '1': {
        text: '未领取',
      },
      '2': {
        text: '已领取',
      },
    },
  },
  {
    title: '更新时间',
    width: 160,
    dataIndex: 'updateTime',
    hideInSearch: true,
  },
  {
    title: '创建时间',
    width: 160,
    dataIndex: 'createTime',
    hideInSearch: true,
  },
];

export default columnsObtainCodeData;
