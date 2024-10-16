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
    title: '租户ID',
    dataIndex: 'tenantId',
    hideInTable: true,
  },
  {
    title: '产品ID',
    dataIndex: 'productId',
    hideInTable: true,
    fieldProps: {
      maxLength: 20,
    },
  },

  // table里面的内容
  {
    title: 'ID',
    width: 180,
    dataIndex: 'serialNo',
    fixed: 'left',
    hideInSearch: true,
  },
  {
    title: '租户ID',
    width: 180,
    dataIndex: 'tenantId',
    hideInSearch: true,
  },
  {
    title: '产品ID',
    width: 180,
    dataIndex: 'productId',
    hideInSearch: true,
  },
  {
    title: '费用',
    width: 100,
    dataIndex: 'fee',
    hideInSearch: true,
  },
  {
    title: '免费次数',
    width: 100,
    dataIndex: 'freeTimes',
    hideInSearch: true,
  },
  {
    title: '状态',
    width: 100,
    hideInSearch: true,
    dataIndex: 'status',
    valueType: 'select',
    valueEnum: {
      0: {
        text: '待审核',
      },
      1: {
        text: '已生效',
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
    width: 60,
    hideInSearch: true,
    dataIndex: 'action',
    fixed: 'right',
  },
];

export default columnsData;
