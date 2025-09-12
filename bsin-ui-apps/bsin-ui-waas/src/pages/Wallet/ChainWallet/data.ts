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

export default [
  // 搜索表单
  {
    title: '钱包ID',
    dataIndex: 'serialNo',
    hideInTable: true,
  },
  {
    title: '账户名称',
    dataIndex: 'walletName',
    hideInTable: true,
    fieldProps: { maxLength: 128 },
  },
  {
    title: '类型',
    dataIndex: 'type',
    valueType: 'select',
    hideInTable: true,
    valueEnum: {
      1: { text: '默认钱包' },
      2: { text: '自定义钱包' },
    },
  },
  {
    title: '状态',
    dataIndex: 'status',
    valueType: 'select',
    hideInTable: true,
    valueEnum: {
      1: { text: '正常' },
      2: { text: '冻结' },
      3: { text: '注销' },
    },
  },
  {
    title: '账户标签',
    dataIndex: 'walletTag',
    valueType: 'select',
    hideInTable: true,
    valueEnum: {
      NONE: { text: '无' },
      DEPOSIT: { text: '寄存' },
    },
  },
  // 表格内容
  {
    title: '钱包ID',
    dataIndex: 'serialNo',
    width: 190,
    fixed: 'left',
    hideInSearch: true,
  },
  {
    title: '账户名称',
    dataIndex: 'walletName',
    width: 160,
    hideInSearch: true,
  },
  {
    title: '类型',
    dataIndex: 'type',
    width: 100,
    valueType: 'select',
    hideInSearch: true,
    valueEnum: {
      1: { text: '默认钱包' },
      2: { text: '自定义钱包' },
    },
  },
  {
    title: '状态',
    dataIndex: 'status',
    width: 100,
    valueType: 'select',
    hideInSearch: true,
    valueEnum: {
      1: { text: '正常' },
      2: { text: '冻结' },
      3: { text: '注销' },
    },
  },
  {
    title: '账户标签',
    dataIndex: 'walletTag',
    width: 100,
    valueType: 'select',
    hideInSearch: true,
    valueEnum: {
      NONE: { text: '无' },
      DEPOSIT: { text: '寄存' },
    },
  },
  {
    title: '业务角色类型',
    dataIndex: 'bizRoleType',
    width: 130,
    hideInSearch: true,
  },
  {
    title: '业务角色编号',
    dataIndex: 'bizRoleTypeNo',
    width: 130,
    hideInSearch: true,
  },
  {
    title: '余额',
    dataIndex: 'balance',
    width: 120,
    hideInSearch: true,
  },
  {
    title: '备注',
    dataIndex: 'remark',
    width: 180,
    hideInSearch: true,
  },
  {
    title: '创建时间',
    dataIndex: 'createTime',
    width: 160,
    hideInSearch: true,
  },
  {
    title: '更新时间',
    dataIndex: 'updateTime',
    width: 160,
    hideInSearch: true,
  },
  {
    title: '操作',
    width: 180,
    hideInSearch: true,
    dataIndex: 'action',
    fixed: 'right',
  }
];
