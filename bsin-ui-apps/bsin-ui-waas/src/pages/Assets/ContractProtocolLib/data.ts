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
    title: '协议类型',
    dataIndex: 'type',
    valueType: 'select',
    hideInTable: true,
    fieldProps: {
      maxLength: 10,
    },
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
      '9': {
        text: '其他',
      },
      '10': {
        text: '其他',
      },
    },
  },
  {
    title: '合约分类',
    dataIndex: 'category',
    valueType: 'select',
    hideInTable: true,
    fieldProps: {
      maxLength: 10,
    },
    valueEnum: {
      '1': {
        text: 'Core',
      },
      '2': {
        text: 'Factory',
      },
      '3': {
        text: 'Extension',
      },
      '4': {
        text: 'Wrapper',
      },
      '5': {
        text: 'Proxys',
      },
      '6': {
        text: 'Other',
      },
    },
  },
  {
    title: '协议标准',
    dataIndex: 'protocolStandards',
    valueType: 'select',
    hideInTable: true,
    fieldProps: {
      maxLength: 10,
    },
    valueEnum: {
      ERC20: {
        text: 'ERC20',
      },
      ERC721: {
        text: 'ERC721',
      },
      ERC1155: {
        text: 'ERC1155',
      },
      ERC3525: {
        text: 'ERC3525',
      },
      DaoBookCore: {
        text: 'DaoBookCore',
      },
      DaoBookFactory: {
        text: 'DaoBookFactory',
      },
      DaoBookExtension: {
        text: 'DaoBookExtension',
      },
      DaoBookWrapper: {
        text: 'DaoBookWrapper',
      },
    },
  },
  {
    title: '链类型',
    dataIndex: 'chainType',
    valueType: 'select',
    hideInTable: true,
    fieldProps: {
      maxLength: 10,
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
    title: '协议名称',
    dataIndex: 'protocolName',
    hideInTable: true,
    fieldProps: {
      maxLength: 10,
    },
  },
  {
    title: '协议编号',
    dataIndex: 'protocolCode',
    hideInTable: true,
    fieldProps: {
      maxLength: 10,
    },
  },

  // table里面的内容
  {
    title: '协议ID',
    width: 165,
    dataIndex: 'serialNo',
    fixed: 'left',
    hideInSearch: true,
  },
  {
    title: '协议名称',
    width: 100,
    dataIndex: 'protocolName',
    hideInSearch: true,
  },
  {
    title: '协议项目编号',
    width: 160,
    dataIndex: 'protocolCode',
    hideInSearch: true,
  },
  {
    title: '协议类型',
    width: 100,
    hideInSearch: true,
    dataIndex: 'type',
    valueType: 'select',
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
      '9': {
        text: '其他',
      },
      '10': {
        text: '其他',
      },
    },
  },
  {
    title: '协议标准',
    dataIndex: 'protocolStandards',
    hideInSearch: true,
    valueType: 'select',
    width: 100,
    valueEnum: {
      ERC20: {
        text: 'ERC20',
      },
      ERC721: {
        text: 'ERC721',
      },
      ERC1155: {
        text: 'ERC1155',
      },
      ERC3525: {
        text: 'ERC3525',
      },
      DaoBookCore: {
        text: 'DaoBookCore',
      },
      DaoBookFactory: {
        text: 'DaoBookFactory',
      },
      DaoBookExtension: {
        text: 'DaoBookExtension',
      },
      DaoBookWrapper: {
        text: 'DaoBookWrapper',
      },
    },
  },
  {
    title: '协议版本',
    width: 160,
    dataIndex: 'version',
    hideInSearch: true,
  },
  {
    title: '协议描述',
    width: 160,
    dataIndex: 'description',
    hideInSearch: true,
  },
  {
    title: '创建者',
    width: 165,
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
    width: 100,
    hideInSearch: true,
    dataIndex: 'action',
    fixed: 'right',
  },
];

export default columnsData;
