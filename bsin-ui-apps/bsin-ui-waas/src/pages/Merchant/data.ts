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
    title: '商户名称',
    dataIndex: 'merchantName',
    // valueType: 'select',
    hideInTable: true,
  },
  {
    title: '商户审核状态',
    dataIndex: 'authenticationStatus',
    valueType: 'select',
    valueEnum: {
      1: { text: '待认证' },
      2: { text: '认证成功' },
      3: { text: '认证失败' },
    },
    hideInTable: true,
    fieldProps: {
      maxLength: 20,
    },
  },
  {
    title: '业态',
    dataIndex: 'businessType',
    hideInTable: true,
  },

  // table里面的内容
  {
    title: '商户ID',
    width: 190,
    dataIndex: 'serialNo',
    fixed: 'left',
    hideInSearch: true,
  },
  {
    title: '商户名称',
    width: 160,
    dataIndex: 'merchantName',
    hideInSearch: true,
  },
  {
    title: '登录名',
    width: 170,
    dataIndex: 'username',
    hideInSearch: true,
  },
  {
    title: '商户审核状态',
    width: 160,
    dataIndex: 'authenticationStatus',
    hideInSearch: true,
    // 认证状态   1: 待认证  2：认证成功  3：认证失败
    valueEnum: {
      '1': {
        text: '待认证',
      },
      '2': {
        text: '认证成功',
      },
      '3': {
        text: '认证失败',
      },
    },
  },
  {
    title: '商户状态',
    width: 160,
    dataIndex: 'status',
    hideInSearch: true,
    valueEnum: {
      '0': {
        text: '正常',
      },
      '1': {
        text: '冻结',
      },
      '2': {
        text: '待审核',
      },
    },
  },
  {
    title: 'logo',
    width: 60,
    dataIndex: 'logoUrl',
    valueType: 'image',
    hideInSearch: true,
  },
  {
    title: '企业工商号',
    width: 160,
    dataIndex: 'businessNo',
    hideInSearch: true,
  },
  {
    title: '法人姓名',
    width: 160,
    dataIndex: 'legalPersonName',
    hideInSearch: true,
  },
  {
    title: '联系电话',
    width: 160,
    dataIndex: 'phone',
    hideInSearch: true,
  },
  {
    title: '商户类型',
    width: 160,
    dataIndex: 'type',
    hideInSearch: true,
    // 商户类型：1、企业商户 2、个人商户  99、平台直属商户
    valueEnum: {
      '1': {
        text: '企业商户',
      },
      '2': {
        text: '个人商户',
      },
      '99': {
        text: '平台直属商户',
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
    width: 110,
    hideInSearch: true,
    dataIndex: 'action',
    fixed: 'right',
  },
];

export default columnsData;
