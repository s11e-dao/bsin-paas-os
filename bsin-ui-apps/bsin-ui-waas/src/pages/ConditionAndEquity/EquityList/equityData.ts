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

  // table里面的内容
  {
    title: '权益名称',
    width: 190,
    dataIndex: 'name',
    hideInSearch: true,
  },
  {
    title: '权益类型',
    width: 160,
    dataIndex: 'type',
    hideInSearch: true,
    // 1:数字资产 2：账户
    valueEnum: {
      '1': {
        text: '徽章',
      },
      '2': {
        text: '积分',
      }
    }
  },
  {
    title: '类型编号',
    width: 170,
    dataIndex: 'typeNo',
    hideInSearch: true,
  },
  {
    title: '权益值',
    width: 160,
    dataIndex: 'amount',
    hideInSearch: true,
  },
  {
    title: '满减总金额',
    width: 160,
    dataIndex: 'totalAmount',
    hideInSearch: true,
  },
  {
    title: '备注',
    width: 160,
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
