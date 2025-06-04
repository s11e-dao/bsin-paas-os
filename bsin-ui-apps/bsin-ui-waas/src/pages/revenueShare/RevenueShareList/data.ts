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
    valueEnum: {
      0: {
        text: 'ERC20',
      },
      1: {
        text: 'ERC721',
      },
      2: {
        text: 'ERC1155',
      },
    },
  },
  {
    title: '协议名称',
    dataIndex: 'protocolName',
    hideInTable: true,
    fieldProps: {
      maxLength: 20,
    },
  },
  {
    title: '协议编号',
    dataIndex: 'protocolCode',
    hideInTable: true,
    fieldProps: {
      maxLength: 20,
    },
  },

  // table里面的内容
  {
    title: '协议ID',
    width: 190,
    dataIndex: 'serialNo',
    fixed: 'left',
    hideInSearch: true,
  },
  {
    title: '协议名称',
    width: 160,
    dataIndex: 'protocolName',
    hideInSearch: true,
  },
  {
    title: '协议编号',
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
      0: {
        text: 'ERC20',
      },
      1: {
        text: 'ERC721',
      },
      2: {
        text: 'ERC1155',
      },
    },
  },
  {
    title: '协议描述',
    width: 160,
    dataIndex: 'description',
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
    width: 100,
    hideInSearch: true,
    dataIndex: 'action',
    fixed: 'right',
  },
];

export default columnsData;
