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
    title: '会员卡集合名称',
    dataIndex: 'name',
    hideInTable: true,
  },
  {
    title: '链网络环境',
    dataIndex: 'chainEnv',
    hideInTable: true,
    valueEnum: {
      test: {
        text: '测试网',
      },
      main: {
        text: '正式网',
      },
    },
    fieldProps: {
      maxLength: 20,
    },
  },
  {
    title: '会员卡符号',
    dataIndex: 'symbol',
    hideInTable: true,
    fieldProps: {
      maxLength: 20,
    },
  },
  {
    title: '链类型',
    dataIndex: 'chainType',
    hideInTable: true,
    fieldProps: {
      maxLength: 20,
    },
    valueEnum: {
      conflux: {
        text: 'conflux',
      },
      polygon: {
        text: 'polygon',
      },
      ethereum: {
        text: 'ethereum',
      },
      tron: {
        text: 'tron',
      },
      bsc: {
        text: 'bsc',
      },
      evm: {
        text: 'evm',
      },
    },
  },
  {
    title: '合约模板',
    dataIndex: 'contractProtocolNo',
    hideInTable: true,
    valueEnum: {
      conflux: {
        text: '树图',
      },
      polygon: {
        text: 'polygon',
      },
      bsc: {
        text: '币安',
      },
      tron: {
        text: '波场',
      },
      wenchang: {
        text: '文昌链',
      },
    },
    fieldProps: {
      maxLength: 20,
    },
  },

  // table里面的内容
  {
    title: '会员卡ID',
    width: 190,
    dataIndex: 'serialNo',
    fixed: 'left',
    hideInSearch: true,
  },
  {
    title: '会员卡集合名称',
    width: 160,
    dataIndex: 'name',
    hideInSearch: true,
  },
  {
    title: '会员卡符号',
    width: 160,
    dataIndex: 'symbol',
    hideInSearch: true,
  },
  {
    title: '总供应量',
    width: 80,
    dataIndex: 'totalSupply',
    hideInSearch: true,
  },
  {
    title: '库存',
    width: 80,
    dataIndex: 'inventory',
    hideInSearch: true,
  },
  {
    title: '集合类型',
    width: 100,
    dataIndex: 'collectionType',
    hideInSearch: true,
    valueEnum: {
      '1': {
        text: '数字徽章',
      },
      '2': {
        text: 'PFP',
      },
      '3': {
        text: '账户-DP',
      },
      '4': {
        text: '数字门票',
      },
      '5': {
        text: 'Pass卡',
      },
      '6': {
        text: '账户-BC',
      },
      '7': {
        text: '满减',
      },
      '8': {
        text: '权限',
      },
    },
  },
  {
    title: '上架状态',
    width: 80,
    dataIndex: 'status',
    hideInSearch: true,
    // 市场流通状态 0、未流通 1、流通中 2、流通完成
    valueEnum: {
      '0': {
        text: '未流通',
      },
      '1': {
        text: '流通中',
      },
      '2': {
        text: '流通完成',
      },
    },
  },
  {
    title: '合约地址',
    width: 380,
    dataIndex: 'contractAddress',
    hideInSearch: true,
    ellipsis: true,
  },
  {
    title: '创建者',
    width: 160,
    dataIndex: 'createBy',
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
