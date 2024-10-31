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
    dataIndex: 'type',
    hideInTable: true,
  },

  // table里面的内容
  {
    title: '会员号',
    width: 170,
    dataIndex: 'serialNo',
    hideInSearch: true,
  },
  {
    title: '租户ID',
    width: 170,
    dataIndex: 'tenantId',
    hideInSearch: true,
  },
  {
    title: '所属商户',
    width: 170,
    dataIndex: 'merchantNo',
    // hideInSearch: true,
  },
  {
    title: '客户号',
    width: 170,
    dataIndex: 'customerNo',
    hideInSearch: true,
  },
  // 会员状态: 0：禁用 1:正常
  {
    title: '状态',
    dataIndex: 'type',
    width: 80,
    // hideInSearch: true,
    valueType: 'select',
    valueEnum: {
      '0': {
        text: '禁用',
      },
      '1': {
        text: '正常',
      },
    },
    fieldProps: {
      maxLength: 20,
    },
  },
  {
    title: '昵称',
    width: 190,
    dataIndex: 'nickname',
    hideInSearch: true,
  },
  {
    title: '身份证号',
    width: 190,
    dataIndex: 'identityCard',
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
