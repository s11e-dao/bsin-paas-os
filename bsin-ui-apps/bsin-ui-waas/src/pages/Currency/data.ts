import type { ProColumns } from '@ant-design/pro-table';

export type columnsDataType = {
  serialNo: string;
  chainCoinKey: string;
  chainCoinName: string;
  shortName: string;
  coin: string;
  chainName: string;
  contractAddress: string;
  coinDecimal: string;
  unit: string;
  status: number;
  type: number;
  remark: string;
  logoUrl: string;
  createBy: string;
  createTime: string;
  updateBy: string;
  updateTime: string;
};

const columnsData: ProColumns<columnsDataType>[] = [
  // 配置搜索框
  {
    title: '币种名称',
    dataIndex: 'chainCoinName',
    hideInTable: true,
    fieldProps: {
      maxLength: 50,
    },
  },
  {
    title: '币种符号',
    dataIndex: 'coin',
    hideInTable: true,
    fieldProps: {
      maxLength: 20,
    },
  },
  {
    title: '状态',
    dataIndex: 'status',
    hideInTable: true,
    valueType: 'select',
    valueEnum: {
      "0": { text: '下架', status: 'Default' },
      "1": { text: '上架', status: 'Success' },
    },
  },

  // table里面的内容
  {
    title: 'ID',
    width: 180,
    dataIndex: 'serialNo',
    hideInSearch: true,
    ellipsis: true,
  },
  {
    title: '币种Key',
    width: 150,
    dataIndex: 'chainCoinKey',
    hideInSearch: true,
  },
  {
    title: '币种名称',
    width: 150,
    dataIndex: 'chainCoinName',
    hideInSearch: true,
  },
  {
    title: '币种简称',
    width: 120,
    dataIndex: 'shortName',
    hideInSearch: true,
  },
  {
    title: '币种符号',
    width: 100,
    dataIndex: 'coin',
    hideInSearch: true,
  },
  {
    title: '链名',
    width: 120,
    dataIndex: 'chainName',
    hideInSearch: true,
  },
  {
    title: '合约地址',
    width: 180,
    dataIndex: 'contractAddress',
    hideInSearch: true,
    ellipsis: true,
  },
  {
    title: '精度',
    width: 80,
    dataIndex: 'coinDecimal',
    hideInSearch: true,
  },
  {
    title: '状态',
    width: 100,
    dataIndex: 'status',
    hideInSearch: true,
    valueEnum: {
      "0": { text: '下架', status: 'Default' },
      "1": { text: '上架', status: 'Success' },
    },
  },
  {
    title: '类型',
    width: 100,
    dataIndex: 'type',
    hideInSearch: true,
    valueEnum: {
      "1": { text: '默认', status: 'Processing' },
      "2": { text: '自定义', status: 'Warning' },
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
    width: 160,
    hideInSearch: true,
    dataIndex: 'action',
    fixed: 'right',
  },
];

export default columnsData;
