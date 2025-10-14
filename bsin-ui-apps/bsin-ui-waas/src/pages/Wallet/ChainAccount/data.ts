import type { ProColumns } from '@ant-design/pro-table';

export type columnsDataType = {
  serialNo: string;
  walletName: string;
  chainName: string;
  coin: string;
  chainCoinName: string;
  chainCoinKey: string;
  address: string;
  walletType: string;
  walletStatus: string;
  walletTag: string;
  balance: string;
  freezeBalance: string;
  status: string;
  remark: string;
  createTime: string;
  updateTime: string;
};

const columnsData: ProColumns<columnsDataType>[] = [
  // 搜索表单
  {
    title: '钱包ID',
    dataIndex: 'walletNo',
    hideInTable: true,
  },
  {
    title: '账户名称',
    dataIndex: 'walletName',
    hideInTable: true,
    fieldProps: { maxLength: 128 },
  },
  {
    title: '链名',
    dataIndex: 'chainName',
    hideInTable: true,
  },
  {
    title: '币种',
    dataIndex: 'coin',
    hideInTable: true,
  },
  {
    title: '类型',
    dataIndex: 'walletType',
    valueType: 'select',
    hideInTable: true,
    valueEnum: {
      1: { text: '普通钱包' },
      2: { text: '多签钱包' },
    },
  },
  {
    title: '状态',
    dataIndex: 'walletStatus',
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
    title: '账户ID',
    dataIndex: 'serialNo',
    width: 190,
    fixed: 'left',
    hideInSearch: true,
  },
  {
    title: '钱包ID',
    dataIndex: 'walletNo',
    width: 190,
    hideInSearch: true,
  },
  {
    title: '账户名称',
    dataIndex: 'walletName',
    width: 160,
    hideInSearch: true,
  },
  {
    title: '链名',
    dataIndex: 'chainName',
    width: 120,
    hideInSearch: true,
  },
  {
    title: '币种',
    dataIndex: 'coin',
    width: 100,
    hideInSearch: true,
  },
  {
    title: '币种名称',
    dataIndex: 'chainCoinName',
    width: 140,
    hideInSearch: true,
  },
  {
    title: '链上币Key',
    dataIndex: 'chainCoinKey',
    width: 180,
    hideInSearch: true,
  },
  {
    title: '地址',
    dataIndex: 'address',
    width: 260,
    hideInSearch: true,
  },
  {
    title: '类型',
    dataIndex: 'walletType',
    width: 100,
    valueType: 'select',
    hideInSearch: true,
    valueEnum: {
      1: { text: '普通钱包' },
      2: { text: '多签钱包' },
    },
  },
  {
    title: '状态',
    dataIndex: 'walletStatus',
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
    title: '余额',
    dataIndex: 'balance',
    width: 120,
    hideInSearch: true,
  },
  {
    title: '冻结余额',
    dataIndex: 'freezeBalance',
    width: 120,
    hideInSearch: true,
  },
  {
    title: '账户状态',
    dataIndex: 'status',
    width: 100,
    hideInSearch: true,
    valueEnum: {
      '1': { text: '正常', status: 'Success' },
      '2': { text: '冻结', status: 'Error' },
    },
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
    width: 200,
    hideInSearch: true,
    dataIndex: 'action',
    fixed: 'right',
  },
];

export default columnsData;
