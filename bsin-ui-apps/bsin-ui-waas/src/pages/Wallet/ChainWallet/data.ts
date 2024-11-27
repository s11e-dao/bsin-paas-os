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
    title: '类型',
    dataIndex: 'type',
    valueType: 'select',
    hideInTable: true,
    valueEnum: {
      1: {
        text: '首页',
      },
      2: {
        text: '数字资产页',
      },
      3: {
        text: '其他',
      },
    },
  },
  {
    title: '名称',
    dataIndex: 'title',
    hideInTable: true,
    fieldProps: {
      maxLength: 20,
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
    title: '名称',
    width: 160,
    dataIndex: 'title',
    hideInSearch: true,
  },
  {
    title: '图片',
    width: 160,
    dataIndex: 'imageUrl',
    hideInSearch: true,
  },
  {
    title: '链接',
    width: 160,
    dataIndex: 'linkUrl',
    hideInSearch: true,
  },
  {
    title: '类型',
    width: 100,
    hideInSearch: true,
    dataIndex: 'type',
    valueType: 'select',
    valueEnum: {
      1: {
        text: '首页',
      },
      2: {
        text: '数字资产页',
      },
      3: {
        text: '其他',
      },
    },
  },
  {
    title: '状态',
    width: 100,
    hideInSearch: true,
    dataIndex: 'status',
    valueType: 'select',
    valueEnum: {
      1: {
        text: '首页',
      },
      2: {
        text: '数字资产页',
      },
      3: {
        text: '其他',
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
    width: 100,
    hideInSearch: true,
    dataIndex: 'action',
    fixed: 'right',
  },
];

export default columnsData;
