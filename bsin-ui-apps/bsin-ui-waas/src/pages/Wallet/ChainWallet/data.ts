import type { ProColumns } from '@ant-design/pro-table';

export type columnsDataType = {
  serialNo: string;
  walletName: string;
  type: string;
  status: string;
  category: string;
  env: string;
  walletTag: string;
  balance: string;
  outUserId: string;
  remark: string;
  bizRoleType: string;
  bizRoleTypeNo: string;
  tenantId: string;
  platformName: string;
  createTime: string;
  updateTime: string;
};

const columnsData: ProColumns<columnsDataType>[] = [
  // 搜索表单
  {
    title: '钱包ID',
    dataIndex: 'serialNo',
    hideInTable: true,
  },
  {
    title: '钱包名称',
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
      '1': { text: '默认钱包' },
      '2': { text: '自定义钱包' },
    },
  },
  {
    title: '状态',
    dataIndex: 'status',
    valueType: 'select',
    hideInTable: true,
    valueEnum: {
      '1': { text: '正常', status: 'Success' },
      '2': { text: '冻结', status: 'Error' },
      '3': { text: '注销', status: 'Default' },
    },
  },
  {
    title: '钱包标签',
    dataIndex: 'walletTag',
    valueType: 'select',
    hideInTable: true,
    valueEnum: {
      NONE: { text: '无' },
      DEPOSIT: { text: '寄存' },
      GATHER: { text: '归集' },
    },
  },
  // 表格内容
  {
    title: '钱包ID',
    dataIndex: 'serialNo',
    width: 180,
    fixed: 'left',
    hideInSearch: true,
    ellipsis: true,
  },
  {
    title: '钱包名称',
    dataIndex: 'walletName',
    width: 160,
    hideInSearch: true,
  },
  {
    title: '类型',
    dataIndex: 'bizRoleType',
    width: 110,
    hideInSearch: true,
    valueEnum: {
      '1': { text: '运营平台' },
      '2': { text: '租户平台' },
      '4': { text: '合伙人' },
      '5': { text: '租户客户' },
      '6': { text: '门店' },
      '99': { text: '无' },
    },
  },
  {
    title: '类型',
    dataIndex: 'type',
    width: 110,
    hideInSearch: true,
    valueEnum: {
      '1': { text: '默认钱包', status: 'Processing' },
      '2': { text: '自定义钱包', status: 'Warning' },
    },
  },
  {
    title: '状态',
    dataIndex: 'status',
    width: 100,
    hideInSearch: true,
    valueEnum: {
      '1': { text: '正常', status: 'Success' },
      '2': { text: '冻结', status: 'Error' },
      '3': { text: '注销', status: 'Default' },
    },
  },
  {
    title: '分类',
    dataIndex: 'category',
    width: 100,
    hideInSearch: true,
    valueEnum: {
      '1': { text: 'MPC' },
      '2': { text: '多签' },
      '3': { text: 'EOA钱包' },
    },
  },
  {
    title: '环境',
    dataIndex: 'env',
    width: 100,
    hideInSearch: true,
  },
  {
    title: '钱包标签',
    dataIndex: 'walletTag',
    width: 100,
    hideInSearch: true,
    valueEnum: {
      NONE: { text: '无' },
      DEPOSIT: { text: '寄存' },
      GATHER: { text: '归集' },
    },
  },
  {
    title: '外部用户ID',
    dataIndex: 'outUserId',
    width: 140,
    hideInSearch: true,
  },
  {
    title: '备注',
    dataIndex: 'remark',
    width: 180,
    hideInSearch: true,
    ellipsis: true,
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
    width: 220,
    hideInSearch: true,
    dataIndex: 'action',
    fixed: 'right',
  }
];

export default columnsData;
