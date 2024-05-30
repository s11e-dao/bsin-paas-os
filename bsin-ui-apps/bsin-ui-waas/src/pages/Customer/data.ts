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
    title: '登录名',
    dataIndex: 'username',
    hideInTable: true,
    fieldProps: {
      maxLength: 20,
    },
  },
  {
    title: '手机号',
    dataIndex: 'phone',
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
    title: '登录名',
    width: 120,
    dataIndex: 'username',
    hideInSearch: true,
  },
  {
    title: '昵称',
    width: 120,
    dataIndex: 'nickname',
    hideInSearch: true,
  },
  {
    title: '姓名',
    width: 120,
    dataIndex: 'realName',
    hideInSearch: true,
  },
  {
    title: '手机号',
    width: 120,
    dataIndex: 'phone',
    hideInSearch: true,
  },
  {
    title: '头像',
    width: 60,
    dataIndex: 'avatar',
    valueType: 'image',
    hideInSearch: true,
  },
  {
    title: '客户类型',
    width: 100,
    hideInSearch: true,
    dataIndex: 'type',
    valueType: 'select',
    valueEnum: {
      '0': {
        text: '个人客户',
      },
      '1': {
        text: '商户客户',
      },
      '2': {
        text: '租户客户',
      },
      '3': {
        text: '顶级平台商家客户',
      },
    },
  },
  {
    title: '介绍',
    width: 160,
    dataIndex: 'description',
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
    width: 200,
    hideInSearch: true,
    dataIndex: 'action',
    fixed: 'right',
  },
];

export default columnsData;
