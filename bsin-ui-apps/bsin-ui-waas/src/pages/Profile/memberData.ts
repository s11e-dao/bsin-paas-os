import type { ProColumns } from '@ant-design/pro-table';

export type columnsMemberDataType = {
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

const columnsMemberData: ProColumns<columnsMemberDataType>[] = [
  // 配置搜索框
  {
    title: '名称',
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
    title: '资产符号',
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

  // table里面的内容
  {
    title: 'ID',
    width: 190,
    dataIndex: 'serialNo',
    fixed: 'left',
    hideInSearch: true,
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
    width: 300,
    hideInSearch: true,
    dataIndex: 'action',
    fixed: 'right',
  },
];

export default columnsMemberData;
